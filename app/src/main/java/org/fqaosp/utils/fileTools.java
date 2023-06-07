package org.fqaosp.utils;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件操作类
 * 2022年6月20日14点09分添加
 */

public class fileTools {

    private String TAG = "fileTools";

    public fileTools(){

    }


    public void checkTools(Context context,boolean isADB){
        fileTools ft = new fileTools();
        String filesDir = ft.getMyHomeFilesPath(context);
        if(isADB){
            filesDir=context.getExternalFilesDir(null).toString();
        }
        String scriptName = "fqtools.sh";
        String barfile = filesDir+"/"+scriptName;
        String busyFile = filesDir+"/busybox";
        String extractScriptFile = filesDir+"/extract.sh";
        File busyfile = new File(busyFile);
        File barFile = new File(barfile);
        File extractScriptFilef = new File(extractScriptFile);
        if(!busyfile.exists()){
            if(Build.CPU_ABI.equals("arm64-v8a")){
                ft.extactAssetsFile(context,"busybox",busyFile);
            }else if(Build.CPU_ABI.equals("armeabi-v7a") || Build.CPU_ABI2.equals("armeabi")){
                ft.extactAssetsFile(context,"busybox_arm",busyFile);
            }
        }
        if(!barFile.exists()){
            ft.extactAssetsFile(context,scriptName,barfile);
        }

        if(!extractScriptFilef.exists()) {
            ft.extactAssetsFile(context, "extract.sh", extractScriptFile);
        }

        if(isADB){
            CMD cmd = new shellUtils().getCMD("cp " + busyFile + " /data/local/tmp/", false);
        }

    }

    //读取文件
    public String readFileToPath(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //读取文件
    public Boolean writeDataToPath(String data, String filePath, Boolean isApp) {
        try {
            FileWriter writer = null;
            if (isApp) {
                writer = new FileWriter(filePath, true);
            } else {
                writer = new FileWriter(filePath);
            }
            writer.write(data, 0, data.length());
            writer.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    //释放资源文件
    public void extactAssetsFile(Context context, String fileName, String toPath) {
        AssetManager assets = context.getAssets();
        InputStream stream = null;
        try {
            stream = assets.open(fileName);
            copyFile(stream, toPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //根据路径获得document文件
    public DocumentFile getDoucmentFileOnData(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace(Environment.getExternalStorageDirectory().toString(), "").replace("/", "%2F");
        return DocumentFile.fromTreeUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }

    //根据路径获得document文件
    public DocumentFile getDoucmentFileOnObb(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace(Environment.getExternalStorageDirectory().toString(), "").replace("/", "%2F");
        return DocumentFile.fromTreeUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb/document/primary%3A" + path2));
    }

    //调用系统文件选择器选择一个文件夹
    public void execDirSelect(Context context, Activity activity, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            execFileSelect(context,activity,msg,43);
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            activity.startActivityForResult(intent, 43);
        }
    }

    public void execFileSelect(Context context, Activity activity, String msg){
        execFileSelect(context,activity,msg,0);
    }
    //调用系统文件选择器
    public void execFileSelect(Context context, Activity activity, String msg,int code) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//打开多个文件
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivityForResult(intent, code);
    }

    //获取在内部存储的自家路径
    public String getMyStorageHomePath(Context context) {
        return context.getExternalFilesDir(null).getParent();
    }

    //获取自家data的files路径
    public String getMyHomeFilesPath(Context context) {
        return context.getFilesDir().toString();
    }

    //选择文件时，判断是否为理想类型
    public void selectFile(Context context, String storage, Uri uri, ArrayList<String> list, ArrayList<Boolean> checkboxs, String msg, String equalstr) {
        String filePath = storage + "/" + uri.getPath().replaceAll("/document/primary:", "");
        String fileName = new stringUtils().getPathByLastNameType(filePath);
        if (fileName.equals(equalstr)) {
//            filePath=filePath.substring(0,filePath.lastIndexOf("/"));
            list.add(filePath);
            checkboxs.add(false);
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    //复制文件
    public Boolean copyFile(InputStream is, String outfile) {
        return copyFile(is, new File(outfile));
    }

    //复制文件
    public Boolean copyFile(InputStream is, File outFile) {
        try {
            return copyFile(is, new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    //复制文件
    public Boolean copyFile(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //复制文件
    public Boolean copyFile(String srcFile, String outFile) {
        return copyFile(new File(srcFile), new File(outFile));
    }

    //复制文件
    public Boolean copyFile(File srcFile, File outFile) {
        try {
            return copyFile(new FileInputStream(srcFile), outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断docum是否等于name,如果有等于的，就直接返回匹配项
    public DocumentFile checkDocum(DocumentFile dd2, String name) {
        for (DocumentFile documentFile : dd2.listFiles()) {
            if (documentFile.getName().equals(name)) {
                return documentFile;
            }
        }
        return dd2;
    }

    // 目标SD路径：/storage/emulated/0
    public String getSDPath(Context context){
        String sdPath = "";
        boolean isSDExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断SD卡是否存在
        if (isSDExist) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File externalFileRootDir = context.getExternalFilesDir("");
                do {
                    externalFileRootDir = Objects.requireNonNull(externalFileRootDir).getParentFile();
                } while (Objects.requireNonNull(externalFileRootDir).getAbsolutePath().contains("/Android"));
                sdPath = Objects.requireNonNull(externalFileRootDir).getAbsolutePath();
            } else {
                sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        } else {
            sdPath = Environment.getRootDirectory().toString();//获取跟目录
        }
        return sdPath;
    }

    public void getAllFileByEndName(String filePath, String file_end_name,List<File> files){

        //获取指定目录下的所有文件或者目录的File数组
        File[] fileArray = new File(filePath).listFiles();
        //遍历该File数组，得到每一个File对象
        if(fileArray != null){
            for (File file :fileArray){
                //判断file对象是否为目录
                if (file.isDirectory()){
                    //是：递归调用
                    getAllFileByEndName(file.getAbsolutePath(),file_end_name,files);
                }else{
                    //否：获取绝对路径输出在控制台
                    String filepath = file.getAbsolutePath();
                    if(filepath.indexOf(file_end_name) != -1){
                        files.add(file);
                    }
                }
            }
        }
    }

    //获取文件大小，带单位
    public String getSize(double size, int count) {
        String size_type[] = {"b", "KB", "MB", "GB", "TB", "PB"};
        if (size > 1024) {
            double d_size = size / 1024;
            count = count + 1;
            return getSize(d_size, count);
        }
        String sizestr = String.format("%.2f", size) + size_type[count];
        return sizestr;
    }

    public String uriToFilePath(Uri uri, Context context) {
        File file = null;
        if(uri == null) return file.toString();
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            String displayName = System.currentTimeMillis()+ Math.round((Math.random() + 1) * 1000)
                    +"."+ MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));

//            注释掉的方法可以获取到原文件的文件名，但是比较耗时
//            Cursor cursor = contentResolver.query(uri, null, null, null, null);
//            if (cursor.moveToFirst()) {
//                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));}

            try {
                InputStream is = contentResolver.openInputStream(uri);
                File cache = new File(context.getExternalCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                file = cache;
                copyFile(is, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.toString();
    }

}
