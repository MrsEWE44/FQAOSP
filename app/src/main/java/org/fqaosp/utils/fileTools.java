package org.fqaosp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
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

/**
 * 文件操作类
 * 2022年6月20日14点09分添加
 */

public class fileTools {

    private final static String TAG = "fileTools";


    //获取文件大小，带单位
    public static String getSize(double size, int count) {
        String size_type[] = {"b", "KB", "MB", "GB", "TB", "PB"};
        if (size > 1024) {
            double d_size = size / 1024;
            count = count + 1;
            return getSize(d_size, count);
        }
        String sizestr = String.format("%.2f", size) + size_type[count];
        return sizestr;
    }

    //读取文件
    public static String readFileToPath(String filePath) {
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
    public static Boolean writeDataToPath(String data, String filePath, Boolean isApp) {
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
    public static void extactAssetsFile(Context context, String fileName, String toPath) {
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
    public static DocumentFile getDoucmentFileOnData(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace(Environment.getExternalStorageDirectory().toString(), "").replace("/", "%2F");
        return DocumentFile.fromTreeUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }

    //根据路径获得document文件
    public static DocumentFile getDoucmentFileOnObb(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace(Environment.getExternalStorageDirectory().toString(), "").replace("/", "%2F");
        return DocumentFile.fromTreeUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb/document/primary%3A" + path2));
    }

    //调用系统文件选择器选择一个文件夹
    public static void execDirSelect(Context context, Activity activity, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            execFileSelect(context,activity,msg,43);
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            activity.startActivityForResult(intent, 43);
        }
    }

    public static void execFileSelect(Context context, Activity activity, String msg){
        execFileSelect(context,activity,msg,0);
    }
    //调用系统文件选择器
    public static void execFileSelect(Context context, Activity activity, String msg,int code) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//打开多个文件
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivityForResult(intent, code);
    }

    //获取在内部存储的自家路径
    public static String getMyStorageHomePath(Context context) {
        return context.getExternalFilesDir(null).getParent();
    }

    //获取自家data的files路径
    public static String getMyHomeFilesPath(Context context) {
        return context.getFilesDir().toString();
    }

    //选择文件时，判断是否为理想类型
    public static void selectFile(Context context, String storage, Uri uri, ArrayList<String> list, ArrayList<Boolean> checkboxs, String msg, String equalstr) {
        String filePath = storage + "/" + uri.getPath().replaceAll("/document/primary:", "");
        String fileName = getPathByLastNameType(filePath);
        if (fileName.equals(equalstr)) {
//            filePath=filePath.substring(0,filePath.lastIndexOf("/"));
            list.add(filePath);
            checkboxs.add(false);
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    //获取文件结尾类型
    public static String getPathByLastNameType(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }

    //获取路径文件名称
    public static String getPathByLastName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    //复制文件
    public static Boolean copyFile(InputStream is, String outfile) {
        return copyFile(is, new File(outfile));
    }

    //复制文件
    public static Boolean copyFile(InputStream is, File outFile) {
        try {
            return copyFile(is, new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    //复制文件
    public static Boolean copyFile(InputStream is, OutputStream os) {
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
    public static Boolean copyFile(String srcFile, String outFile) {
        return copyFile(new File(srcFile), new File(outFile));
    }

    //复制文件
    public static Boolean copyFile(File srcFile, File outFile) {
        try {
            return copyFile(new FileInputStream(srcFile), outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断docum是否等于name,如果有等于的，就直接返回匹配项
    public static DocumentFile checkDocum(DocumentFile dd2, String name) {
        for (DocumentFile documentFile : dd2.listFiles()) {
            if (documentFile.getName().equals(name)) {
                return documentFile;
            }
        }
        return dd2;
    }

}
