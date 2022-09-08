package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;
import static org.fqaosp.utils.permissionRequest.getExternalStorageManager;
import static org.fqaosp.utils.permissionRequest.grantAndroidData;
import static org.fqaosp.utils.permissionRequest.grantAndroidObb;
import static org.fqaosp.utils.permissionRequest.requestExternalStoragePermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESEARCHAdapter;
import org.fqaosp.adapter.FILESELECTAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.entity.SearchFileInfo;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class fileSearchActivity extends AppCompatActivity {
    private ArrayList<SearchFileInfo> searchFileInfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    private EditText fsaet1, fsaet2, fsaet3, fsaet4, fsaet5;
    private Button fsabt1, fsabt2, fsabt3, fsabt4, fsabt5, fsabt6;
    private Spinner fsasp;
    private String[] sizetype = {"byte", "KB", "MB", "GB", "TB"};
    private int spinner_index = 0, nowItemIndex = -1;
    public static String TAG = "fileSearchActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_search_activity);
        fuckActivity.getIns().add(this);
        setTitle("文件搜索");
        initBT();
        checkPeer(this);
    }

    private void initBT() {
        lv1 = findViewById(R.id.fsalv);
        fsaet1 = findViewById(R.id.fsaet1);
        fsaet2 = findViewById(R.id.fsaet2);
        fsaet3 = findViewById(R.id.fsaet3);
        fsaet4 = findViewById(R.id.fsaet4);
        fsaet5 = findViewById(R.id.fsaet5);
        fsabt1 = findViewById(R.id.fsabt1);
        fsabt2 = findViewById(R.id.fsabt2);
        fsabt3 = findViewById(R.id.fsabt3);
        fsabt4 = findViewById(R.id.fsabt4);
        fsabt5 = findViewById(R.id.fsabt5);
        fsabt6 = findViewById(R.id.fsabt6);
        fsasp = findViewById(R.id.fsasp);
        fsasp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sizetype));
        btClick();
    }

    private String getButtonText(Button b) {
        return b.getText().toString();
    }

    private String[] getButtonTextToArray(Button b) {
        return getButtonText(b).split("-");
    }

    private Long getTimestamp(String time) {
        Date parse = null;
        try {
            parse = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
            long parseTime = parse.getTime();
            return parseTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private Long fileSizeToBytes(Long size) {
        switch (spinner_index) {
            case 0:
                return size;
            case 1:
                return size * 1024;
            case 2:
                return size * 1024 * 1024;
            case 3:
                return size * 1024 * 1024 * 1024;
            case 4:
                return size * 1024 * 1024 * 1024 * 1024;
        }
        return 0L;
    }

    private void searchFile(Context context) {
        searchFileInfos.clear();
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String time = getButtonText(fsabt2) + " " + getButtonText(fsabt3);
        String time2 = getButtonText(fsabt4) + " " + getButtonText(fsabt5);
        String searchText = fsaet1.getText().toString().trim();
        String fileTYPE = fsaet2.getText().toString().trim();
        String fileSize = fsaet3.getText().toString().trim();
        String fileSize2 = fsaet5.getText().toString().trim();
        String dirPath = fsaet4.getText().toString().trim();
        if (searchText.isEmpty()) {
            searchText = "*";
        }
        if (fileTYPE.isEmpty()) {
            fileTYPE = "*";
        }
        if (fileSize.isEmpty()) {
            fileSize = "0";
        }
        if (fileSize2.isEmpty()) {
            fileSize2 = "0";
        }
        if (dirPath.isEmpty()) {
            dirPath = externalStorage;
        }
        Long fileSizeToBytes = fileSizeToBytes(Long.parseLong(fileSize));
        Long fileSizeToBytes1 = fileSizeToBytes(Long.parseLong(fileSize2));
        Long timestamp = getTimestamp(time);
        Long timestamp2 = getTimestamp(time2);
        if (dirPath.indexOf("/Android/data") != -1) {
            DocumentFile doucmentFile = fileTools.getDoucmentFileOnData(context, dirPath);
            dirPath = dirPath.replaceAll("/Android/data", "");
            addFlistTree(doucmentFile, null, dirPath.split("/"), 0, fileSizeToBytes, fileSizeToBytes1, timestamp, timestamp2, fileTYPE, searchText);

        } else if (dirPath.indexOf("/Android/obb") != -1) {
            DocumentFile doucmentFile = fileTools.getDoucmentFileOnObb(context, dirPath);
            dirPath = dirPath.replaceAll("/Android/obb", "");
            addFlistTree(doucmentFile, null, dirPath.split("/"), 0, fileSizeToBytes, fileSizeToBytes1, timestamp, timestamp2, fileTYPE, searchText);
        } else {
            if (!dirPath.equals(externalStorage)) {
                dirPath = externalStorage + dirPath;
            }
            File file = new File(dirPath);
            addFlistTree(null, file, dirPath.split("/"), 0, fileSizeToBytes, fileSizeToBytes1, timestamp, timestamp2, fileTYPE, searchText);
        }
        Collections.sort(searchFileInfos, new Comparator<SearchFileInfo>() {
            @Override
            public int compare(SearchFileInfo searchFileInfo, SearchFileInfo t1) {
                return searchFileInfo.getFname().compareTo(t1.getFname());
            }
        });

    }

    private void btClick() {
        Activity that = this;
        fsabt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(that, "提示", "正在搜索匹配的文件,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            showFiles(lv1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        searchFile(that);
                        sendHandlerMSG(handler, 0);
                    }
                });
            }
        });

        fsabt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateSelect(that, fsabt2);
            }
        });
        fsabt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeSelect(that, fsabt3);
            }
        });
        fsabt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateSelect(that, fsabt4);
            }
        });
        fsabt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeSelect(that, fsabt5);
            }
        });

        fsabt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPeer(that);
            }
        });

        fsasp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                nowItemIndex = i;
                createLVMenu();
                return false;
            }
        });

    }

    private void checkPeer(Activity that){
        String extstorage = Environment.getExternalStorageDirectory().toString();
        String s = extstorage + "/Android/data";
        String s2 = extstorage + "/Android/obb";
        String s3 = extstorage + "/Android";
        DocumentFile doucmentFile = fileTools.getDoucmentFileOnData(that, s);
        DocumentFile doucmentFil2e = fileTools.getDoucmentFileOnObb(that, s2);
        File file = new File(s3);
        if (doucmentFile.isDirectory() && doucmentFil2e.isDirectory() && file.isDirectory()) {
            showSelectFile(extstorage, null, doucmentFile, doucmentFil2e, that);
        } else {
            AlertDialog.Builder ab = new AlertDialog.Builder(that);
            ab.setTitle("提示");
            ab.setMessage("没有授权data、obb、内部存储访问权限，是否现在进行授权？");
            ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestExternalStoragePermission(that);
                    getExternalStorageManager(that);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        grantAndroidData(that);
                        grantAndroidObb(that);
                    }
                    dialogInterface.cancel();
                    showInfoMsg(that,"提示","授权完成后，重启应用");
                }
            });
            ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            ab.create().show();

        }
    }

    //长按listview中的元素，显示一个菜单选项
    private void createLVMenu() {
        lv1.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 0, 0, "复制信息");
                contextMenu.add(0, 1, 0, "分享");
                contextMenu.add(0, 2, 0, "打开");
            }
        });

    }

    private boolean checkMap2StrCmp(String s1, String s2) {
        return ((s1.indexOf(s2) != -1 || s2.equals("*")) && !s2.isEmpty());
    }

    private boolean checkSizeCmp(long length, long filesize, long filesize2) {
        return (length > filesize && filesize > 0) && (length < filesize2 && filesize2 > 0);
    }

    //条件过滤
    private void checkMap2(DocumentFile dd, File file, Long filesize, Long filesize2, Long time1, Long time2, String ftype, String sstr) {
        long l = file == null ? dd.lastModified() : file.lastModified();
        Uri uri = file == null ? dd.getUri() : (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? FileProvider.getUriForFile(this, getPackageName() + ".provider", file) : Uri.fromFile(file));
        long length = 0;
        try {
            ParcelFileDescriptor r = getContentResolver().openFileDescriptor(uri, "r");
            length = r.getStatSize();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String path = uri.getPath();
        String name = file == null ? dd.getName() : file.getName();
        String nameType = fileTools.getPathByLastNameType(name);
//        Log.d(TAG,"input parm : [ " + filesize + " , " + filesize2 + " , " + time1 + " , " + time2 + " , " + ftype + " , " + sstr + " ] -- " + name + " -- indexname [ " + checkMap2StrCmp(name, sstr) + " ] -- size [ " + checkSizeCmp(length, filesize, filesize2)+ " ] -- type [ " + checkMap2StrCmp(nameType, ftype) + " ]");

        if(!sstr.isEmpty() && !ftype.isEmpty() && filesize >0 && filesize2 > 0 && time1 > 0 && time2 > 0){
            if (checkMap2StrCmp(name, sstr) && checkSizeCmp(l, time1, time2) && checkSizeCmp(length, filesize, filesize2) && checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

        if(!sstr.isEmpty() && !ftype.isEmpty() && filesize >0 && filesize2 > 0 && time1 == 0 && time2 ==0){
            if (checkMap2StrCmp(name, sstr) && checkSizeCmp(length, filesize, filesize2) && checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }
        if(!sstr.isEmpty() && !ftype.isEmpty() && filesize ==0 && filesize2 == 0&& time1 > 0 && time2 > 0){
            if (checkMap2StrCmp(name, sstr) && checkSizeCmp(l, time1, time2) && checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

        if(!sstr.isEmpty() && !ftype.isEmpty() && filesize ==0 && filesize2 == 0 && time1 == 0 && time2 ==0){
            if (checkMap2StrCmp(name, sstr) &&  checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

        if(!sstr.isEmpty() && ftype.isEmpty() && filesize ==0 && filesize2 == 0 && time1 == 0 && time2 ==0){
            if (checkMap2StrCmp(name, sstr) ) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }
//分割线
        if(sstr.isEmpty() && !ftype.isEmpty() && filesize >0 && filesize2 > 0 && time1 > 0 && time2 > 0){
            if (checkSizeCmp(l, time1, time2) && checkSizeCmp(length, filesize, filesize2) && checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

        if(sstr.isEmpty() && !ftype.isEmpty() && filesize >0 && filesize2 > 0 && time1 == 0 && time2 ==0){
            if (checkSizeCmp(length, filesize, filesize2) && checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }
        if(sstr.isEmpty() && !ftype.isEmpty() && filesize ==0 && filesize2 == 0&& time1 > 0 && time2 > 0){
            if (checkSizeCmp(l, time1, time2) && checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

        if(sstr.isEmpty() && !ftype.isEmpty() && filesize ==0 && filesize2 == 0 && time1 == 0 && time2 ==0){
            if (checkMap2StrCmp(nameType, ftype)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

//分割线
        if(sstr.isEmpty() && ftype.isEmpty() && filesize >0 && filesize2 > 0 && time1 > 0 && time2 > 0){
            if (checkSizeCmp(l, time1, time2) && checkSizeCmp(length, filesize, filesize2) ) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

        if(sstr.isEmpty() && ftype.isEmpty() && filesize >0 && filesize2 > 0 && time1 == 0 && time2 ==0){
            if (checkSizeCmp(length, filesize, filesize2) ) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }
        if(sstr.isEmpty() && ftype.isEmpty() && filesize ==0 && filesize2 == 0&& time1 > 0 && time2 > 0){
            if (checkSizeCmp(l, time1, time2)) {
                searchFileInfos.add(new SearchFileInfo(name, path, nameType, length, l, uri));
                checkboxs.add(false);
            }
        }

    }

    //搜索文件
    private void addFlistTree(DocumentFile dd, File file, String[] array, int index, Long filesize, Long filesize2, Long time1, Long time2, String ftype, String sstr) {
        if (dd != null && file == null) {
            //Log.d(TAG,dd.getName()+ " -- " +array.length + " -- " + index + " -- " + filesize + " -- " +filesize2 + " -- " + time1 + " -- " + time2 + " -- " + ftype + " -- " +sstr);
            if (array != null && array.length >= (index + 1)) {
                if (dd.isDirectory() && dd.listFiles() != null) {
                    if (array[index].isEmpty()) {
                        index = index + 1;
                        for (DocumentFile listFile : dd.listFiles()) {
                            if (array.length >= (index + 1) && array[index].equals(listFile.getName())) {
                                addFlistTree(listFile, file, array, index, filesize, filesize2, time1, time2, ftype, sstr);
                            }
                        }
                    } else if (!array[index].isEmpty() && array[index].equals(dd.getName())) {
                        index = index + 1;
                        for (DocumentFile listFile : dd.listFiles()) {
                            if (listFile.isDirectory()) {
                                addFlistTree(listFile, file, array, index, filesize, filesize2, time1, time2, ftype, sstr);
                            } else {
                                checkMap2(listFile, file, filesize, filesize2, time1, time2, ftype, sstr);
                            }
                        }
                    }
                }
            } else {

                if (dd.isDirectory() && dd.listFiles() != null) {
                    for (DocumentFile listFile : dd.listFiles()) {
                        if (listFile.isDirectory()) {
                            addFlistTree(listFile, file, array, index++, filesize, filesize2, time1, time2, ftype, sstr);
                        } else {
                            checkMap2(listFile, file, filesize, filesize2, time1, time2, ftype, sstr);
                        }
                    }
                }
            }


        }

        if (file != null && dd == null) {
            if (file.isDirectory() && file.listFiles() != null) {
                for (File listFile : file.listFiles()) {
                    addFlistTree(dd, listFile, array, index += 1, filesize, filesize2, time1, time2, ftype, sstr);
                }
            } else {
                checkMap2(dd, file, filesize, filesize2, time1, time2, ftype, sstr);
            }

        }
    }

    private void addFlist(DocumentFile dd, ArrayList<String> flist) {
        for (DocumentFile df : dd.listFiles()) {
            if (df.isDirectory()) {
                flist.add(df.getName());
            }
        }
    }

    //显示文件选择框
    private void showSelectFile(String extstorage, String path, DocumentFile dd, DocumentFile obb, Context context) {
        ArrayList<String> flist = new ArrayList<>();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        //判断文件路径是否为需要授权的data与obb
        if (path.indexOf("/Android/data") != -1) {
            if (dd.isDirectory()) {
                addFlist(dd, flist);
            }

        } else if (path.indexOf("/Android/obb") != -1) {
            if (obb.isDirectory()) {
                addFlist(obb, flist);
            }

        } else {
            //如果只是授权文件存储权限，则只需要通关file列出所有文件夹即可
            for (File file : new File(extstorage + "/" + path).listFiles()) {
                if (file.isDirectory()) {
                    flist.add(file.getName());
                }
            }
        }
        Collections.sort(flist, String::compareTo);
        String finalPath = path;
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        View view2 = getLayoutInflater().inflate(R.layout.file_select_activity, null);
        ab.setView(view2);
        ab.setTitle("选择文件夹");
        ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                fsaet4.setText(finalPath.replaceAll("//", "/"));
            }
        });
        ab.setPositiveButton("返回上一级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentFile dou = dd.getParentFile();
                DocumentFile dou2 = obb.getParentFile();
                if (dou == null) {
                    dou = dd;
                }
                if (dou2 == null) {
                    dou2 = obb;
                }
                showSelectFile(extstorage, new File(finalPath).getParent(), dou, dou2, context);
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = ab.create();
        ListView fsalv = view2.findViewById(R.id.fsalv);
        fsalv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipboardManager cpm = (ClipboardManager) fileSearchActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(finalPath.replaceAll("//", extstorage + "/") + "/" + flist.get(i));
                Toast.makeText(fileSearchActivity.this, "已复制完整路径", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        fsalv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                alertDialog.cancel();
                DocumentFile dou = dd;
                DocumentFile dou2 = obb;
                for (DocumentFile documentFile : dd.listFiles()) {
                    if (documentFile.getName().equals(flist.get(i))) {
                        dou = documentFile;
                    }
                }
                for (DocumentFile documentFile : obb.listFiles()) {
                    if (documentFile.getName().equals(flist.get(i))) {
                        dou2 = documentFile;
                    }
                }
                showSelectFile(extstorage, finalPath + "/" + flist.get(i), dou, dou2, context);
            }
        });

        FILESELECTAdapter fileselectAdapter = new FILESELECTAdapter(flist, context);
        fsalv.setAdapter(fileselectAdapter);
        alertDialog.show();

    }

    //显示时间选择器
    private void showTimeSelect(Context context, Button b) {
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        View view2 = getLayoutInflater().inflate(R.layout.time_select_activity, null);
        TimePicker timePicker = view2.findViewById(R.id.timeselect);
        timePicker.setIs24HourView(true);
        ab.setView(view2);
        ab.setTitle("选择时间");
        ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int hour = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                }
                int minute = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    minute = timePicker.getMinute();
                }
                String timestr = hour + ":" + minute;
                dialogInterface.cancel();
                b.setText(timestr);
            }
        });
        ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ab.create().show();
    }

    //显示日期选择器
    private void showDateSelect(Context context, Button b) {
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        View view2 = getLayoutInflater().inflate(R.layout.date_select_activity, null);
        DatePicker datePicker = view2.findViewById(R.id.dateselect);
        ab.setView(view2);
        ab.setTitle("选择日期");
        ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int year = datePicker.getYear();
                int dayOfMonth = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                String datestr = year + "-" + (month + 1) + "-" + dayOfMonth;
                dialogInterface.cancel();
                b.setText(datestr);
            }
        });
        ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ab.create().show();
    }

    private void showFiles(ListView listView) {
        FILESEARCHAdapter filesearchAdapter = new FILESEARCHAdapter(fileSearchActivity.this, checkboxs, searchFileInfos);
        listView.setAdapter(filesearchAdapter);
    }

    //调用外部应用打开文件
    private void openFile(Context context, Uri uri) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "*/*");
            context.startActivity(intent);
            Intent.createChooser(intent, "请选择对应的软件打开该附件！");
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "sorry附件不能打开，请下载相关软件！", Toast.LENGTH_SHORT).show();
        }
    }

    //分享文件到外部应用
    private void shareFile(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uri);  //传输图片或者文件 采用流的方式
            intent.setType("*/*");   //分享文件
            context.startActivity(Intent.createChooser(intent, "分享"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "sorry附件分享失败 [" + e.getMessage() + "]", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                ClipboardManager cpm = (ClipboardManager) fileSearchActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(searchFileInfos.get(nowItemIndex).toString());
                Toast.makeText(fileSearchActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                shareFile(this, searchFileInfos.get(nowItemIndex).getUri());
                break;
            case 2:
                openFile(this, searchFileInfos.get(nowItemIndex).getUri());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "帮助");
        menu.add(Menu.NONE, 1, 1, "退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Activity a = this;
        int itemId = item.getItemId();
        switch (itemId) {
            case 0:
                showInfoMsg(a, "帮助信息","该页面是用于文件搜索的，你可以搜索/Android/data或者obb或者/sdcard/里面的文件。\r\n" +
                        "1.如果你需要搜索某个文件，可以直接在搜索框里面输入，然后点击搜索即可，默认是从内置存储目录开始搜索。\r\n" +
                        "2.如果你需要搜索某个类型的文件，可以在\"文件类型\" 一栏输入文件类型的后缀名即可，比如需要搜索\"zip\"压缩包，直接输入zip然后再点击搜索即可.\r\n" +
                        "3.如果你需要搜索某个大小范围内的文件，可以在\"小\"里面输入文件最小值,在\"大\"里面输入文件最大的值，右边有个单位选择，默认是byte(字节),可以选择最高pb，然后再点击搜索即可.\r\n" +
                        "4.如果你需要从某个路径下搜索上述的条件，可以在下面选择路径那里选择你需要的路径，然后配合其它条件即可进行搜索。\r\n" +
                        "5.如果你需要从某个时间段间搜索文件，可以点击下面的开始日期和结束日期，然后点击搜索即可.\r\n" +
                        "6.支持多个条件一起搜索，有问题可以在GitHub提issue！\r\n" +
                        "7.长按搜索出来的文件可以选择\"以第三方程序打开\" 或者 \"分享给第三方程序\".\r\n");
                break;
            case 1:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 54 || requestCode == 55) && data != null) {
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
            Log.d(TAG, "grant ok");
        }
    }

}
