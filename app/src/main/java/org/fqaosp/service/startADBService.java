package org.fqaosp.service;

import android.os.Looper;

import org.fqaosp.utils.CMD;

public class startADBService {

    public static void main(String[] args) {
        // 利用looper让线程循环
        Looper.prepareMainLooper();
        // 开一个子线程启动服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                new adbSocketService(new adbSocketService.SocketListener() {
                    @Override
                    public CMD sendCMD(String cmdstr) {
                        return new CMD(cmdstr,false);
                    }
                });
            }
        }).start();
        Looper.loop();
    }
}
