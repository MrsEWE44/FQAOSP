package org.fqaosp.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class netUtils {

    /**
     * <p>
     * 下载内容
     * <p>
     * String url_name , String dirPath , String fileName
     * <p>
     * 下载链接、保存路径、保存名称
     */
    public void downLoad(String url_name, String dirPath, String fileName) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
            downLoad(url_name, dirPath, fileName);
        } else {
            File name = new File(dirPath + "/" + fileName);
            InputStream input = null;
            FileOutputStream fos = null;
            if (!name.exists()) {
                try {
                    HttpURLConnection huc = (HttpURLConnection) new URL(url_name).openConnection();
                    huc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
                    huc.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                    huc.setRequestProperty("Connection","keep-alive");
                    byte[] buff = new byte[1024];
                    int len = -1;
                    input = huc.getInputStream();
                    fos = new FileOutputStream(name);
                    while ((len = input.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                name.delete();
                downLoad(url_name, dirPath, fileName);
            }
        }
    }


    public String getHTML(String pageURL) throws IOException{
        StringBuilder pageHTML = new StringBuilder();
        URL url = new URL(pageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connection.setRequestProperty("Connection","keep-alive");
        InputStream inputStream = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            pageHTML.append(line);
            pageHTML.append("\r\n");
        }
        inputStream.close();
        br.close();
        return pageHTML.toString();
    }

    public long downloadFileOnUrlByAndorid(Context context , String filepath, String url){
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //允许漫游
        request.setAllowedOverRoaming(true);
        request.setTitle(url+" 下载中...");
        request.setDescription("文件正在下载当中...");
        //指定下载保存路径
        request.setDestinationUri(Uri.fromFile(new File(filepath)));
        //获取下载管理器
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        return downloadManager.enqueue(request);
    }


}
