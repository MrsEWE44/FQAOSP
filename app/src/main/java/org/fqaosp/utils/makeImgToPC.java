package org.fqaosp.utils;

import android.os.Build;

/**
 *
 * 挂载手机上的镜像文件到电脑上面
 * 实现类似driverdroid的功能
 * */

public class makeImgToPC {

    private final static String config_path1 = "/config/usb_gadget/g1";
    private final static String config_path2="/sys/class/android_usb/android0";

    //对外开放功能函数
    public Boolean mountLocalFile(String filePath,boolean enableMTP){

        int sdkInt = Build.VERSION.SDK_INT;

        //Android9以上同时开启mtp实现
        String cmdstr = "cd "+config_path1+" && echo -n 'msc' >configs/b.1/strings/0x409/configuration &&  rm -rf configs/b.1/f* && echo  0x05C6 > idVendor && echo 0x9015 > idProduct && echo -n '"+filePath+"' > functions/mass_storage.0/lun.0/file && ln -s functions/mtp.gs0 configs/b.1/f1 && ln -s functions/mass_storage.0 configs/b.1/f2 && ln -s functions/ffs.adb configs/b.1/f3 ";

        if(enableMTP == false){
            //Android9-10实现
            if (sdkInt >= Build.VERSION_CODES.P) {
                cmdstr="cd "+config_path1+" && echo -n 'msc' >configs/b.1/strings/0x409/configuration &&  rm -rf configs/b.1/f* && ln -s functions/mass_storage.0 configs/b.1/f1 && echo -n '"+filePath+"' >configs/b.1/f1/lun.0/file && setprop sys.usb.config mass_storage ";
            }

            //Android8以前实现，未测试
            if (sdkInt < Build.VERSION_CODES.P) {
                cmdstr="cd "+config_path2 +" && echo -n 0 > enable && echo -n '"+filePath+"' > f_mass_storage/lun/file && echo -n 'mass_storage' >functions && echo -n 1 >enable";
            }
        }
        CMD cmd2 = new CMD(cmdstr);
        return cmd2.getResultCode() == 0;
    }

}
