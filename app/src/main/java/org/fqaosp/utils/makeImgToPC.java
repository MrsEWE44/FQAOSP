package org.fqaosp.utils;

/**
 *
 * 挂载手机上的镜像文件到电脑上面
 * 实现类似driverdroid的功能
 * */

public class makeImgToPC {

    private final static String config_path1 = "/config/usb_gadget/g1";
    private final static String config_path2="/sys/class/android_usb/android0";

    //Android8以后实现
    private  Boolean mode1(String filePath){
        CMD cmd = new CMD("[ -d "+config_path1+" ] && cd "+config_path1 +" && find ./ -name configu*");
        String configpath=cmd.getResult();
        if(!configpath.isEmpty()){
            CMD cmd1 = new CMD("echo -n 'msc' > "+config_path1+"/"+configpath);
            if(cmd1.getResultCode()==0){
                CMD cmd2 = new CMD("cd "+config_path1+" && for f in configs/b.1/f*; do rm $f; done && ln -s functions/mass_storage.0 configs/b.1/f1 && echo -n \""+filePath+"\" > configs/b.1/f1/lun.0/file");
                return cmd2.getResultCode() == 0;
            }
        }
        return false;
    }

    //Android8以前实现
    private  Boolean mode2(String filePath){
        CMD cmd = new CMD("[ -d "+config_path2+" ] && cd "+config_path1 +" && echo -n 0 >enable && echo -n '"+filePath+"' >f_mass_storage/lun/file && echo -n 'mass_storage' >functions && echo -n 1 >enable");
        return cmd.getResultCode()==0;
    }

    //对外开放功能函数
    public Boolean mountLocalFile(String filePath){
        if(mode1(filePath)){
           return true;
        }
        return mode2(filePath);
    }

}
