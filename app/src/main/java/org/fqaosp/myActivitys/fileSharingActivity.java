package org.fqaosp.myActivitys;

import static org.fqaosp.utils.permissionRequest.requestExternalStoragePermission;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESELECTAdapter;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fileSharingActivity extends AppCompatActivity {

    private View fileView, appView;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private ArrayList<String> fileList = new ArrayList<>();
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ViewPager viewPager;
    private EditText fsaet1, fsaet2;
    private Button fsab1;
    private boolean isRoot=false , isADB=false;

    private fileTools ft = new fileTools();
    private dialogUtils du = new dialogUtils();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_sharing_activity);
        fuckActivity.getIns().add(this);
        setTitle("文件共享");
        initLayout();
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
    }

    private void initLayout() {
        fileList.clear();
        fsab1 = findViewById(R.id.fsab1);
        fsaet1 = findViewById(R.id.fsaet1);
        fsaet2 = findViewById(R.id.fsaet2);
        fileView = getLayoutInflater().inflate(R.layout.file_select_activity, null);
        appView = getLayoutInflater().inflate(R.layout.file_select_activity, null);
        viewPager = findViewById(R.id.fsavp);
        views.add(fileView);
        views.add(appView);
        slist.add("文件选择");
        slist.add("应用选择");
        initBtClick();
        String ipAddress = getIpAddress();
        Log.d("myip", ipAddress);
        if (ipAddress.trim().isEmpty()) {
            fsaet1.setText("当前未连接WiFi或开启热点");
            fsab1.setEnabled(false);
        } else {
            ProgressDialog show = du.showMyDialog(this,  "正在加载内容,请稍后(可能会出现无响应，请耐心等待)....");
//            preventDismissDialog(show);
            Handler handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 0) {
                        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
                        viewPager.setAdapter(adapter);
                        show.dismiss();
                    }
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fsaet1.setText(ipAddress);
                    initFileView();
                    initAppView();
                    du.sendHandlerMSG(handler, 0);
                }
            }).start();

        }
    }

    //获取当前设备的ip地址
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    private void initBtClick() {
        Activity that = this;

        fsab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsab1.setEnabled(false);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        fsab1.setText("当前正在运行");
                        new HttpServer().start(that);
                    }
                });
            }
        });
    }

    private void initFileView() {
        checkPeer(this);
    }

    private void initAppView() {
        ListView fsalv = appView.findViewById(R.id.fsalv);
        fsalv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int i1 = 0; i1 < checkboxs.size(); i1++) {
                    if (checkboxs.get(i1)) {
                        PKGINFO pkginfo = pkginfos.get(i1);
                        fileList.add(pkginfo.getApkpath());
                        Toast.makeText(fileSharingActivity.this, pkginfo.getAppname() + " 已加入分享队列", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        fsalv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent2.setData(Uri.parse("package:" + pkginfo.getPkgname()));
                startActivity(intent2);
            }
        });

        du.queryPKGProcessDialog(this,this,fsalv,pkginfos,checkboxs,2,null,isRoot);
    }

    private void checkPeer(Activity that) {
        String extstorage = Environment.getExternalStorageDirectory().toString();
        String s3 = extstorage + "/Android";
        File file = new File(s3);
        if (file.isDirectory()) {
            showSelectFile(extstorage, null, that);
        } else {
            requestExternalStoragePermission(that);
        }
    }

    //显示文件选择框
    private void showSelectFile(String extstorage, String path, Context context) {
        String AnDir = extstorage + "/Android";
        ArrayList<String> flist = new ArrayList<>();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        //如果只是授权文件存储权限，则只需要通关file列出所有文件夹即可
        File file1 = new File(extstorage + "/" + path);
        if (file1.listFiles() != null) {
            for (File file : file1.listFiles()) {
                if(file.getAbsolutePath().indexOf(AnDir) == -1){
                    flist.add(file.getName());
                }
            }
        }
        Collections.sort(flist, String::compareTo);
        if (flist.size() >0 && flist.get(0).length() > extstorage.length()) {
            flist.add(0, "上一页");
        }else{
            flist.add( "上一页");
        }

        String finalPath = path;

        ListView fsalv = fileView.findViewById(R.id.fsalv);
        fsalv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String full_path = extstorage + "/" + finalPath + "/" + flist.get(i);
                File file = new File(full_path);
                fileList.add(full_path);
                Toast.makeText(context, file.getName() + " 已加入分享队列", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        fsalv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fffname = flist.get(i);
                String full_path = finalPath + "/" + fffname;
                File file = new File(extstorage + "/" + full_path);
                if (file.getName().indexOf("上一页") != -1) {
                    showSelectFile(extstorage, new File(finalPath).getParent(), context);
                } else {
                    if (file.isDirectory()) {
                        showSelectFile(extstorage, full_path, context);
                    }
                }
            }
        });

        FILESELECTAdapter fileselectAdapter = new FILESELECTAdapter(flist, context);
        fsalv.setAdapter(fileselectAdapter);

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
                du.showInfoMsg(a, "帮助信息", "该页面是用于文件、应用网络共享的，当有人跟你同处在一个局域网的时候，就可以通过这个功能来分享文件给对方，该功能不需要root权限。\r\n" +
                        "1.如何选择文件？长按你想要分享的文件名称即可加入分享队列，分享队列是全局的，也就是说，你这里选择完文件，还可以继续选择添加已经安装的应用程序，然后统一分享出去。\r\n" +
                        "2.如何让别人访问我分享的内容？让其它人在浏览器输入你界面上那个ip地址跟端口，即可访问你分享的内容，只要在同一个局域网下，任何设备，只要支持网络连接都可以访问。\r\n" +
                        "3.如何分享？选择完需要分享的文件或者应用后，点击开始分享即可开始运行。不允许后台挂着，此程序杜绝后台残留，故而没有加入后台常驻。\r\n" +
                        "4.如何停止分享？点击右上角三个点，然后点击退出，即可关闭。\r\n" +
                        "5.关于默认端口我需要修改吗？你可以不修改，默认端口为26456，除非该端口无法正常在你设备上运行，那可以修改成其它端口尝试。\r\n" +
                        "6.如果已经开启了共享，如何附加新的文件？在你开启共享后，可以继续长按添加文件夹或者文件到分享队列，对方只需要刷新页面即可访问。(这个本来是个bug，结果发现好像确实有用...)\r\n"
                );
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
            Log.d(fileSharingActivity.class.getName(), "grant ok");
        }
    }

    //http服务器内部类
    private class HttpServer {

        //判断是否为第一次访问
        private boolean isFirst = true;

        //临时list
        private ArrayList<String> tempList = new ArrayList<>();

        //保留上一级路径
        private String parenPath;

        //获取端口
        private String s = fsaet2.getText().toString();

        //获取ip
        private String s2 = fsaet1.getText().toString();

        private String ipAndPort = s2 + ":" + (s.isEmpty() ? 26456 : s);

        //html界面头部
        private String htmlhead = "<html>\n" +
                "<head>\n" +
                "\n" +
                "<h1 style=\"text-align: center;\">fqaosp file download web page</h1>\n" +
                "<meta charset=\"utf-8\">\n" +
                "\n" +
                "</head>\n" +
                "<body>\n";

        //html界面尾部
        private String htmlend = "</body>\n" +
                "</html>\n" +
                "\n" +
                "<script>\n" +
                "function bt(a){\n" +
                "\twindow.open(\"http://" + ipAndPort + "/fqaosp?file=\"+a)\n" +
                "}\n" +
                "</script>";

        private Context context;

        private ServerSocket serverSocket = null;

        public void start(Context context2) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            context = context2;
            tempList.addAll(fileList);
            String extstorage = Environment.getExternalStorageDirectory().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverSocket = new ServerSocket(s.isEmpty() ? 26456 : Integer.valueOf(s));
                        Log.d(fileSharingActivity.class.getName(), "listen port : " + serverSocket.getLocalPort());
//                System.out.println("服务器端正在监听端口："+serverSocket.getLocalPort());


                        while (true) {//死循环时刻监听客户端链接
                            //开始服务
                            try {
                                Socket socket = serverSocket.accept();
                                Log.d(fileSharingActivity.class.getName(), "new con : " + socket.getInetAddress() + ":" + socket.getPort());
//                    System.out.println("建立了与客户端一个新的tcp连接，客户端地址为："+socket.getInetAddress()
//                            +":"+socket.getPort());
                                //读取HTTP请求信息
                                InputStream socketIn = socket.getInputStream();
                                int size = socketIn.available();
                                byte[] b = new byte[size];
                                socketIn.read(b);
                                String request = new String(b);
                                String[] split = request.split("\r\n");

                                //创建HTTP响应结果
                                //创建响应协议、状态
                                String httpStatus = "HTTP/1.1 200 OK\r\n";
                                for (String s : split) {
                                    if (s.indexOf("GET") != -1) {
                                        String path = s.split(" ")[1];
                                        //判断是否为请求fqaosp
                                        if (path.indexOf("fqaosp") != -1) {
                                            String parm = path.split("\\?")[1];
                                            Log.d("parm", parm);
                                            //判断是否为请求fqaosp的文件列表以及文件下载
                                            if (parm.indexOf("file") != -1) {
                                                parm = parm.split("=")[1];
                                                Integer index = Integer.valueOf(parm.trim());
                                                String data = isFirst ? fileList.get(index) : tempList.get(index);
                                                //判断是否点击的“上一页”
                                                if(data.equals("上一页") && extstorage.equals(parenPath)){
                                                    tempList.clear();
                                                    tempList.addAll(fileList);
                                                    returnFileList(fileList, httpStatus, socket);
                                                }else{
                                                    if (data.equals("上一页")) {
                                                        data = parenPath;
                                                    }
                                                    data = data.replaceAll("//", "/");
                                                    File file = new File(data);

//                            Log.d("data",data + " -- " + parenPath + " -- " + file.getAbsolutePath());
                                                    if (file.isDirectory()) {
                                                        tempList.clear();
                                                        File[] files = file.listFiles();
                                                        if (files != null && files.length > 0) {
                                                            for (File listFile : files) {
                                                                tempList.add(listFile.getAbsolutePath());
                                                            }
                                                        }
                                                        Collections.sort(tempList, String::compareTo);

                                                        if (tempList.size() >0 && tempList.get(0).length() > extstorage.length()) {
                                                            tempList.add(0, "上一页");
                                                        }else{
                                                            tempList.add( "上一页");
                                                        }
                                                        parenPath = file.getParent();
                                                        returnFileList(tempList, httpStatus, socket);

                                                    } else {
                                                        String contentType = "attachment;filename=" + URLEncoder.encode(file.getName(), "utf-8");
                                                        //创建响应头
                                                        String responseHeader = "Content-disposition:" + contentType + "\r\nContent-Length: " + file.length() + "\r\n\r\n";
                                                        OutputStream socketOut = socket.getOutputStream();
                                                        //发送响应协议、状态码及响应头、正文
                                                        socketOut.write(httpStatus.getBytes());
                                                        socketOut.write(responseHeader.getBytes());
                                                        InputStream in = new FileInputStream(file);
                                                        int len = 0;
                                                        b = new byte[1024];
                                                        try {
                                                            while ((len = in.read(b)) != -1) {
                                                                //在这里会出现下载出错的问题，需要改善一下。
                                                                //2023年1月30日18点32分
                                                                socketOut.write(b, 0, len);
                                                            }
                                                        } catch (Exception e) {
                                                            Log.d(fileSharingActivity.class.getName(), e.getMessage());
                                                            e.printStackTrace();
                                                        } finally {
                                                            socketOut.close();
                                                        }

                                                    }
                                                }
                                            } else {
                                                returnText(httpStatus, socket, "参数错误.");
                                            }

                                        } else {
                                            returnFileList(fileList, httpStatus, socket);
                                        }

                                    }
                                }
                                socket.shutdownInput();
                                socket.shutdownOutput();
                                socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            isFirst=false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //返回文件列表
        public void returnFileList(ArrayList<String> slist, String httpStatus, Socket socket) throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append(htmlhead);
            if (slist.size() < 1) {
                sb.append("<h1>还没有选择文件哦!</h1>");
            } else {
                for (int i = 0; i < slist.size(); i++) {
                    File file = new File(slist.get(i));
                    if (file.isDirectory() || file.toString().equals("上一页")) {
                        sb.append("<div><table border=\"1\"><td><a href=\"" + "http://" + ipAndPort + "/fqaosp?file=" + i + "\" <h1>" + file.getName() + "</h1></a></td></table></div>");
                    } else {
                        sb.append("<div><table border=\"1\"><td>" + file.getName() + "</td><td>" + ft.getSize(file.length(), 0) + "</td><td><button onclick=\"bt(" + i + ")\">下载</button></td></table></div>");
                    }
                }
            }
            sb.append(htmlend);
            returnText(httpStatus, socket, sb.toString());
        }

        //返回文本内容给浏览器
        public void returnText(String httpStatus, Socket socket, String text) throws Exception {
            String contentType = "text/html;text/plain;charset=UTF-8";
            //创建响应头
            String responseHeader = "Content-Type:" + contentType + "\r\n\r\n";
            OutputStream socketOut = socket.getOutputStream();
            //发送响应协议、状态码及响应头、正文
            socketOut.write(httpStatus.getBytes());
            socketOut.write(responseHeader.getBytes());
            socketOut.write(text.getBytes());
            socketOut.close();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //退回主界面的时候，需要将残留服务进程干掉
        Activity activity = this;
        fuckActivity.getIns().kill(activity);
        System.exit(0);
    }
}
