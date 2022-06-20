package org.fqaosp.threads;

import static org.fqaosp.utils.multiFunc.preventDismissDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;

public class alertDialogThread extends  Thread{


    public alertDialogThread(Context context, String msg, String cmd, String title, String sucessmsg, String errormsg) {
        this.context = context;
        this.msg = msg;
        this.cmd = cmd;
        this.title = title;
        this.sucessmsg = sucessmsg;
        this.errormsg = errormsg;
    }

    private Context context;
    private String msg , cmd ,title,sucessmsg,errormsg;


    @Override
    public void run() {
        Looper.prepare();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        AlertDialog show = alertDialog.show();
        preventDismissDialog(show);
        cmdThread ee = new cmdThread(cmd, sucessmsg,errormsg,context, show);
        ee.start();
        Looper.loop();
    }
}
