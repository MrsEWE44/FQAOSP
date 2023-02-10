package org.fqaosp.utils;

import android.util.Log;

import org.fqaosp.naive.term;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

/**
 *
 * 调用系统命令功能实现
 *
 * */

public class CMD {

    private StringBuilder sb = new StringBuilder();
    private Process exec=null;
    private Integer resultCode=-1;

    /**
     * @param cmd 你要執行的命令
     * @param root 是否以root權限運行命令
     * @param isterm 是否使用jni運行命令
     *
     * */
    public CMD(String cmd , Boolean root , Boolean isterm){
        String cmdhead = root ?"su":"/system/bin/sh" ;
        Log.i("cmd ::: ",cmd);
        if(isterm){
            HashMap<String,String> hashMap = term.runcmd(cmdhead+" -c '"+cmd+"' && exit;");
            for (String s : hashMap.keySet()) {
                resultCode = Integer.valueOf(s.replaceAll("\\s+",""));
                sb.append(hashMap.get(s));
                break;
            }
        }else{
            try{
                String cmds[] = {cmdhead,"-c",cmd};
                ProcessBuilder processBuilder = new ProcessBuilder(cmds);
                processBuilder.redirectErrorStream(true);
                exec = processBuilder.start();
                DataOutputStream dos  = new DataOutputStream(exec.getOutputStream());
                dos.writeBytes(cmd + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream(),"UTF-8"));
                String line="";
                while((line=reader.readLine()) != null){
                    sb.append(line+"\n");
                }
                resultCode = exec.waitFor();
                reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //默认以root身份运行命令
    public CMD(String cmd){
        this(cmd,true,true);
    }

    //对接shizuku命令
    public CMD(String cmds[]){
        try{
            Log.d("cmdstr", Arrays.toString(cmds));
            ShizukuRemoteProcess shizukuRemoteProcess = Shizuku.newProcess(cmds, null, null);
            resultCode=shizukuRemoteProcess.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(shizukuRemoteProcess.getInputStream()));
            String line = null;
            while((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            shizukuRemoteProcess.destroy();
            br.close();
        }catch (Exception e){
            sb.append(e.toString());
        }
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
