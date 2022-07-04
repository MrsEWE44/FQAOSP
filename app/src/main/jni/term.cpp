#include <dirent.h>
#include <fcntl.h>
#include <jni.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/wait.h>
#include <termios.h>
#include <unistd.h>

#define TERMUX_UNUSED(x) x __attribute__((__unused__))

jstring str2jstring(JNIEnv *pEnv, char buf[1024]);

#ifdef __APPLE__
# define LACKS_PTSNAME_R
#endif

static int throw_runtime_exception(JNIEnv *env, char const *message) {
    jclass exClass = env->FindClass("java/lang/RuntimeException");
    env->ThrowNew(exClass, message);
    return -1;
}

static int create_subprocess(JNIEnv *env,
                             char const *cmd,
                             char const *cwd,
                             char *const argv[],
                             char **envp,
                             int *pProcessId,
                             jint rows,
                             jint columns) {
    int ptm = open("/dev/ptmx", O_RDWR | O_CLOEXEC);
    if (ptm < 0) return throw_runtime_exception(env, "Cannot open /dev/ptmx");

#ifdef LACKS_PTSNAME_R
    char* devname;
#else
    char devname[64];
#endif
    if (grantpt(ptm) || unlockpt(ptm) ||
        #ifdef LACKS_PTSNAME_R
        (devname = ptsname(ptm)) == NULL
        #else
        ptsname_r(ptm, devname, sizeof(devname))
#endif
            ) {
        return throw_runtime_exception(env, "Cannot grantpt()/unlockpt()/ptsname_r() on /dev/ptmx");
    }

    // Enable UTF-8 mode and disable flow control to prevent Ctrl+S from locking up the display.
    struct termios tios;
    tcgetattr(ptm, &tios);
    tios.c_iflag |= IUTF8;
    tios.c_iflag &= ~(IXON | IXOFF);
    tcsetattr(ptm, TCSANOW, &tios);

    /** Set initial winsize. */
    struct winsize sz = {.ws_row = (unsigned short) rows, .ws_col = (unsigned short) columns};
    ioctl(ptm, TIOCSWINSZ, &sz);

    pid_t pid = fork();
    if (pid < 0) {
        return throw_runtime_exception(env, "Fork failed");
    } else if (pid > 0) {
        *pProcessId = (int) pid;
        return ptm;
    } else {
        // Clear signals which the Android java process may have blocked:
        sigset_t signals_to_unblock;
        sigfillset(&signals_to_unblock);
        sigprocmask(SIG_UNBLOCK, &signals_to_unblock, 0);

        close(ptm);
        setsid();

        int pts = open(devname, O_RDWR);
        if (pts < 0) exit(-1);

        dup2(pts, 0);
        dup2(pts, 1);
        dup2(pts, 2);

        DIR *self_dir = opendir("/proc/self/fd");
        if (self_dir != NULL) {
            int self_dir_fd = dirfd(self_dir);
            struct dirent *entry;
            while ((entry = readdir(self_dir)) != NULL) {
                int fd = atoi(entry->d_name);
                if (fd > 2 && fd != self_dir_fd) close(fd);
            }
            closedir(self_dir);
        }

        clearenv();
        if (envp) for (; *envp; ++envp) putenv(*envp);

        if (chdir(cwd) != 0) {
            char *error_message;
            // No need to free asprintf()-allocated memory since doing execvp() or exit() below.
            if (asprintf(&error_message, "chdir(\"%s\")", cwd) == -1) error_message = "chdir()";
            perror(error_message);
            fflush(stderr);
        }
        execvp(cmd, argv);
        // Show terminal output about failing exec() call:
        char *error_message;
        if (asprintf(&error_message, "exec(\"%s\")", cmd) == -1) error_message = "exec()";
        perror(error_message);
        _exit(1);
    }
}

jstring str2jstring(JNIEnv *env, char pat[1024]) {
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte *) pat);
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("utf-8");
    //将byte数组转换为java String,并输出
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

extern "C"
JNIEXPORT jint JNICALL
Java_org_fqaosp_naive_term_createSubprocess(JNIEnv *env, jclass clazz, jstring cmd, jstring cwd,
                                            jobjectArray args, jobjectArray envVars,
                                            jintArray processIdArray, jint rows, jint columns) {
    jsize size = args ? env->GetArrayLength(args) : 0;
    char **argv = NULL;
    if (size > 0) {
        argv = (char **) malloc((size + 1) * sizeof(char *));
        if (!argv) return throw_runtime_exception(env, "Couldn't allocate argv array");
        for (int i = 0; i < size; ++i) {
            jstring arg_java_string = (jstring) env->GetObjectArrayElement(args, i);
            char const *arg_utf8 = env->GetStringUTFChars(arg_java_string, NULL);
            if (!arg_utf8)
                return throw_runtime_exception(env, "GetStringUTFChars() failed for argv");
            argv[i] = strdup(arg_utf8);
            env->ReleaseStringUTFChars(arg_java_string, arg_utf8);
        }
        argv[size] = NULL;
    }

    size = envVars ? env->GetArrayLength(envVars) : 0;
    char **envp = NULL;
    if (size > 0) {
        envp = (char **) malloc((size + 1) * sizeof(char *));
        if (!envp) return throw_runtime_exception(env, "malloc() for envp array failed");
        for (int i = 0; i < size; ++i) {
            jstring env_java_string = (jstring) env->GetObjectArrayElement(envVars, i);
            char const *env_utf8 = env->GetStringUTFChars(env_java_string, 0);
            if (!env_utf8)
                return throw_runtime_exception(env, "GetStringUTFChars() failed for env");
            envp[i] = strdup(env_utf8);
            env->ReleaseStringUTFChars(env_java_string, env_utf8);
        }
        envp[size] = NULL;
    }

    int procId = 0;
    char const *cmd_cwd = env->GetStringUTFChars(cwd, NULL);
    char const *cmd_utf8 = env->GetStringUTFChars(cmd, NULL);
    int ptm = create_subprocess(env, cmd_utf8, cmd_cwd, argv, envp, &procId, rows, columns);
    env->ReleaseStringUTFChars(cmd, cmd_utf8);
    env->ReleaseStringUTFChars(cmd, cmd_cwd);

    if (argv) {
        for (char **tmp = argv; *tmp; ++tmp) free(*tmp);
        free(argv);
    }
    if (envp) {
        for (char **tmp = envp; *tmp; ++tmp) free(*tmp);
        free(envp);
    }

    int *pProcId = (int *) env->GetPrimitiveArrayCritical(processIdArray, NULL);
    if (!pProcId)
        return throw_runtime_exception(env,
                                       "JNI call GetPrimitiveArrayCritical(processIdArray, &isCopy) failed");

    *pProcId = procId;
    env->ReleasePrimitiveArrayCritical(processIdArray, pProcId, 0);

    return ptm;
}
extern "C"
JNIEXPORT void JNICALL
Java_org_fqaosp_naive_term_setPtyWindowSize(JNIEnv *env, jclass clazz, jint fd, jint rows,
                                            jint cols) {
    struct winsize sz = {.ws_row = (unsigned short) rows, .ws_col = (unsigned short) cols};
    ioctl(fd, TIOCSWINSZ, &sz);
}
extern "C"
JNIEXPORT jint JNICALL
Java_org_fqaosp_naive_term_waitFor(JNIEnv *env, jclass clazz, jint pid) {
    int status;
    waitpid(pid, &status, 0);
    if (WIFEXITED(status)) {
        return WEXITSTATUS(status);
    } else if (WIFSIGNALED(status)) {
        return -WTERMSIG(status);
    } else {
        // Should never happen - waitpid(2) says "One of the first three macros will evaluate to a non-zero (true) value".
        return 0;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_org_fqaosp_naive_term_close(JNIEnv *env, jclass clazz, jint file_descriptor) {
    close(file_descriptor);
}
extern "C"
JNIEXPORT void JNICALL
Java_org_fqaosp_naive_term_setPtyUTF8Mode(JNIEnv *env, jclass clazz, jint fd) {
    struct termios tios;
    tcgetattr(fd, &tios);
    if ((tios.c_iflag & IUTF8) == 0) {
        tios.c_iflag |= IUTF8;
        tcsetattr(fd, TCSANOW, &tios);
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_org_fqaosp_naive_term_system_1runcmd(JNIEnv *env, jclass clazz, jstring cmd) {
    return system(env->GetStringUTFChars(cmd,0));
}
extern "C"
JNIEXPORT jobject JNICALL
Java_org_fqaosp_naive_term_runcmd(JNIEnv *env, jclass clazz, jstring cmd) {
    //通过调用Java反射实现hashmap功能
    jclass class_hashmap = env->FindClass("java/util/HashMap");
    jmethodID hashmap_init = env->GetMethodID(class_hashmap, "<init>", "()V");
    jobject HashMap = env->NewObject(class_hashmap, hashmap_init);
    jmethodID HashMap_put = env->GetMethodID(class_hashmap, "put","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    FILE *pResultStr = NULL;
    char szBuf[1024] = {0};

    /* 创建子进程，执行shell命令 */
    pResultStr = popen(env->GetStringUTFChars(cmd, 0), "r");
    if (NULL == pResultStr) {
//        printf("popen faild. (%d, %s)\n",errno, strerror(errno));
        env->CallObjectMethod(HashMap, HashMap_put, env->NewStringUTF("-1"), str2jstring(env, "popen faild") );
        return HashMap;
    }
    /* 获取返回结果 */
    fread(szBuf, 1, sizeof(szBuf), pResultStr);

    /* 打印命令返回内容 */
//    printf("Info: %s\n", szBuf);

    /* 不要忘记关闭句柄 */
    pclose(pResultStr);
    env->CallObjectMethod(HashMap, HashMap_put, env->NewStringUTF("0"), str2jstring(env, szBuf));
    return HashMap;
}