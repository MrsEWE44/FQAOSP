#include <jni.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


jstring str2jstring(JNIEnv *pEnv, char buf[1024]);


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
Java_org_fqaosp_naive_term_systemcmd(JNIEnv *env, jclass clazz, jstring cmd) {
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