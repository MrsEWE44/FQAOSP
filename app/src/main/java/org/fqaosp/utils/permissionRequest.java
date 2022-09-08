package org.fqaosp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.util.List;

public class permissionRequest {

    public static void grantAndroidData(Activity context){
        Uri uri1 = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri1);
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        context.startActivityForResult(intent1,54);
    }

    public static void grantAndroidObb(Activity context){
        Uri uri1 = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb");
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri1);
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        context.startActivityForResult(intent1,55);
    }

    public static void intentExternal(Context context){
        intentPKG(context,Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
    }

    public static void intentPKG(Context context,String action){
        Intent intent = new Intent(action);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static void getExternalStorageManager(Context context){
        // 通过api判断手机当前版本号
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 安卓11，判断有没有“所有文件访问权限”权限
            if (!Environment.isExternalStorageManager()) {
                intentExternal(context);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 安卓6 判断有没有读写权限权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "已获取文件访问权限", Toast.LENGTH_SHORT).show();
            }else{
                intentExternal(context);
            }
        }
    }

    public static void requestExternalStoragePermission(Activity activity){
        String p[] = {Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.REQUEST_COMPANION_PROFILE_WATCH};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(p,0);
        }
    }
}
