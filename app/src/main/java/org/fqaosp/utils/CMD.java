package org.fqaosp.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * 调用系统命令功能实现
 *
 * */

public class CMD {

    private StringBuilder sb = new StringBuilder();
    private Process exec=null;
    private Integer resultCode=-1;

    //运行命令
    public CMD(String cmd , Boolean root){
        try {
            Log.i("cmd ::: ",cmd);
            if(root){
                exec = Runtime.getRuntime().exec("su");
                DataOutputStream dos  = new DataOutputStream(exec.getOutputStream());
                dos.writeBytes(cmd + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
            }else{
                exec = Runtime.getRuntime().exec(cmd);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream(),"UTF-8"));
            String line="";
            while((line=reader.readLine()) != null){
                sb.append(line+"\n");
            }
            resultCode = exec.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //默认以root身份运行命令
    public CMD(String cmd){
        this(cmd,true);
    }


    //获取执行完命令后的状态码
    public Integer getResultCode(){
       return resultCode;
    }

    //获取命令返回结果
    public String getResult(){
        return sb.toString();
    }


}
