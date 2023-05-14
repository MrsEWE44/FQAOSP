package org.fqaosp.service;

import org.fqaosp.utils.CMD;

import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class adbSocketClient {

    private final int PORT = 10504;
    private SocketListener listener;
    private PrintWriter printWriter;
    private CMD cmd;

    public adbSocketClient(final String cmdstr, SocketListener listener) {
        this.listener = listener;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("127.0.0.1", PORT));
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    // 发送指令
                    printWriter.println(cmdstr);
                    printWriter.flush();
                    // 读取服务端返回
                    readServerData(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void readServerData(final Socket socket) {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            cmd = (CMD)ois.readObject();
            this.listener.getCMD(cmd);
            ois.close();
            printWriter.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CMD getCMD(){
        return  cmd;
    }

    public interface SocketListener {
        void getCMD(CMD cmd);
    }

}
