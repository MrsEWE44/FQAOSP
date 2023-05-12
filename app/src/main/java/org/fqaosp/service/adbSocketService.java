package org.fqaosp.service;

import android.util.Log;

import org.fqaosp.utils.CMD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class adbSocketService {

    private final int PORT = 10504;
    private SocketListener listener;

    public adbSocketService(SocketListener listener) {
        this.listener = listener;
        try {
            // 利用ServerSocket类启动服务，然后指定一个端口
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("fqaosp adb server running " + PORT + " port");
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
            // 新建一个线程池用来并发处理客户端的消息
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    5,
                    10,
                    50000,
                    TimeUnit.MILLISECONDS,
                    queue
            );
            while (true) {
                Socket socket = serverSocket.accept();
                // 接收到新消息
                executor.execute(new processMsg(socket));
            }
        } catch (Exception e) {
            System.out.println("SocketServer create Exception:" + e);
        }
    }

    class processMsg implements Runnable {
        Socket socket;

        public processMsg(Socket s) {
            socket = s;
        }

        public void run() {
            try {
                // 通过流读取内容
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line =bufferedReader.readLine();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                CMD cmd = listener.sendCMD(line);
                oos.writeObject(cmd);
                oos.flush();
                oos.close();
                bufferedReader.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("socket connection error：" + e.toString());
            }
        }
    }

    public interface SocketListener{

        CMD sendCMD(String cmdstr);

    }

}
