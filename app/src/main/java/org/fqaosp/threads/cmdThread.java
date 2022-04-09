package org.fqaosp.threads;

import static org.fqaosp.utils.multiFunc.dismissDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.fqaosp.utils.CMD;
import org.fqaosp.utils.multiFunc;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class cmdThread extends  Thread{


    public cmdThread(String cmd, String sucess_msg, String error_msg, Context context, AlertDialog ddd) {
        this.cmd = cmd;
        this.sucess_msg = sucess_msg;
        this.error_msg = error_msg;
        this.context = context;
        this.ddd = ddd;
    }

    private String cmd;
    private String sucess_msg,error_msg;
    private Context context;
    private AlertDialog ddd;


    @Override
    public void run() {
        CMD cmd = new CMD(this.cmd);
        if(cmd.getResultCode() == 0){
            Looper.prepare();
            Toast.makeText(this.context, sucess_msg, Toast.LENGTH_SHORT).show();
            dismissDialog(ddd);
            Looper.loop();
        }else{
            Looper.prepare();
            Toast.makeText(this.context, error_msg, Toast.LENGTH_SHORT).show();
            dismissDialog(ddd);
            Looper.loop();
        }
    }



}
