package org.fqaosp.utils;

import android.app.AppOpsManager;
import android.os.Build;

import org.fqaosp.entity.PKGINFO;

import java.util.ArrayList;

public class appopsCmdStr {
    private String localTmpFile="/data/local/tmp/fqaosp.apk";
    private String rm_local_tmp_file="rm -rf "+localTmpFile;
    private String install_cmd_head=rm_local_tmp_file+" && cp \"";
    private String install_cmd_end="\" " + localTmpFile +" && chmod 777 "+localTmpFile;

    public appopsCmdStr(){}

    public String getInstallLocalPkgCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getInstallLocalPkgOnDowngradeCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install -r -d " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" -r -d " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getInstallLocalPkgOnDebugCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install -r -t " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" -r -t " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getInstallLocalPkgOnExistsCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install  -r " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" -r " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getPKGUIDByCMD(String pkgname){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "cat /data/system/packages.xml |grep "+pkgname+"|cut -d' ' -f14|cut -d'\"' -f2";
        }
        return "pm list packages -U  "+pkgname+"|cut -d ':' -f3";
    }

    public String disableAppByAPPUIDCMD(String pkgname){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner $("+getPKGUIDByCMD(pkgname)+") -j DROP";
        return cmd;
    }

    public String disableAppByAPPUIDCMD(int pkguid){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner "+pkguid+" -j DROP";
        return cmd;
    }

    public String enableAppByAPPUIDCMD(String pkgname){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner $("+getPKGUIDByCMD(pkgname)+") -j ACCEPT";
        return cmd;
    }

    public String enableAppByAPPUIDCMD(int pkguid){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner "+pkguid+" -j ACCEPT";
        return cmd;
    }

    public static String getRunTraverseCMDStr(ArrayList<PKGINFO> pkginfos, ArrayList<Boolean> checkboxs , String cmdstr){
        int hit=0;
        StringBuilder sb = new StringBuilder();
        sb.append("aaa=(");
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                sb.append("\""+pkginfos.get(i).getPkgname()+"\" ");
                hit++;
            }
        }
        if(hit == 0){
            for (PKGINFO pkginfo : pkginfos) {
                sb.append("\""+pkginfo.getPkgname()+"\" ");
            }
        }

        sb.append(");for pp in ${aaa[@]};do "+cmdstr+";done;");
        return sb.toString();
    }


    //调用命令对选项进行相应授权、撤销操作
    public String getRunAppopsCMD(String pkgname,String pkgcate , int ss ,String uid){
        String cmdstr = "";
        if(pkgcate.indexOf("$") != -1){
            pkgcate = pkgcate.replaceAll("\\$","\\\\\\$");
        }
        switch (ss){
            case 0:
                if(uid == null){
                    cmdstr="pm revoke "+pkgname + " " + pkgcate;
                }else{
                    cmdstr="pm revoke --user "+uid + " "+pkgname + " " + pkgcate;
                }
                break;
            case 1:
            case 2:
                if(uid == null){
                    cmdstr="pm disable \"" +pkgname+"/"+ pkgcate+"\"";
                }else{
                    cmdstr="pm disable --user "+uid+" \"" +pkgname+"/"+ pkgcate+"\"";
                }

                break;
            case 3:
                if(uid == null){
                    cmdstr="pm grant "+pkgname + " " + pkgcate;
                }else{
                    cmdstr="pm grant --user " + uid + " " +pkgname + " " + pkgcate;
                }
                break;
            case 4:
            case 5:
                if(uid == null){
                    cmdstr="pm enable " +pkgname+"/"+ pkgcate;
                }else{
                    cmdstr="pm enable --user "+uid + " \"" +pkgname+"/"+ pkgcate+"\"";
                }
                break;
        }
        return cmdstr;
    }

    public String getRunAppopsBySwtichCMD(Boolean b,int mode ,String pkgname,String pkgcate , String uid){
        String cmdstr = null;
        if(b){
            switch (mode){
                case 0:
                case 1:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,4,uid);
                    break;
                case 2:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,3,uid);
                    break;
            }
        }else{
            switch (mode){
                case 0:
                case 1:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,1,uid);
                    break;
                case 2:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,0,uid);
                    break;
            }
        }
        return cmdstr;
    }


    //拼接命令参数字符串
    public String spliceCMDStr(PKGINFO pkginfo,Integer mode , int apops_opt_index,int apops_permis_index){
        StringBuilder sb = new StringBuilder();
        String cmdHead = "appops set --uid  "+pkginfo.getPkgname()+" ";
        String cmdWrite = "appops write-settings ";
        String modestr="";
        ArrayList<String> ops = new ArrayList<>();

        if(mode == null){
            if(apops_permis_index == 11 || apops_permis_index == 13){
                mode = 1;
            }else if(apops_permis_index == 12){
                mode =2;
            }else{
                mode =0;
            }
        }

        if(mode != null){
            if(mode == 0){
                switch (apops_opt_index){
                    case 0:
                        modestr = "default";
                        break;
                    case 1:
                        modestr = "ignore";
                        break;
                    case 2:
                        modestr = "allow";
                        break;
                    case 3:
                        modestr = "foreground";
                        break;
                }
            }
            if(mode ==1){
                switch (apops_opt_index){
                    case 0:
                        modestr = "true";
                        break;
                    case 1:
                        modestr = "false";
                        break;
                }
            }
            if(mode ==2){
                switch (apops_opt_index){
                    case 0:
                        modestr = "active";
                        break;
                    case 1:
                        modestr = "working_set";
                        break;
                    case 2:
                        modestr = "frequent";
                        break;
                    case 3:
                        modestr = "rare";
                        break;
                    case 4:
                        modestr = "restricted";
                        break;
                }
            }
        }

        switch (apops_permis_index){
            case 0 :
                ops.add(AppOpsManager.OPSTR_READ_PHONE_STATE);
                ops.add(AppOpsManager.OPSTR_READ_CONTACTS);
                ops.add(AppOpsManager.OPSTR_WRITE_CONTACTS);
                ops.add(AppOpsManager.OPSTR_READ_CALL_LOG);
                ops.add(AppOpsManager.OPSTR_WRITE_CALL_LOG);
                ops.add(AppOpsManager.OPSTR_CALL_PHONE);
                ops.add(AppOpsManager.OPSTR_READ_SMS);
                ops.add("WRITE_SMS");
                ops.add(AppOpsManager.OPSTR_SEND_SMS);
                ops.add(AppOpsManager.OPSTR_RECEIVE_SMS);
                ops.add("RECEIVE_EMERGECY_SMS");
                ops.add(AppOpsManager.OPSTR_RECEIVE_MMS);
                ops.add(AppOpsManager.OPSTR_RECEIVE_WAP_PUSH);
                ops.add("READ_ICC_SMS");
                ops.add("WRITE_ICC_SMS");
                ops.add(AppOpsManager.OPSTR_PROCESS_OUTGOING_CALLS);
                ops.add("READ_CELL_BROADCASTS");
                ops.add(AppOpsManager.OPSTR_ADD_VOICEMAIL);
                ops.add(AppOpsManager.OPSTR_ANSWER_PHONE_CALLS);
                ops.add(AppOpsManager.OPSTR_READ_CELL_BROADCASTS);
                ops.add(AppOpsManager.OPSTR_READ_PHONE_NUMBERS);
                ops.add(AppOpsManager.OPSTR_READ_PHONE_STATE);
                ops.add("android.permission-group.CALL_LOG");
                ops.add("android.permission-group.CONTACTS");
                ops.add("android.permission-group.PHONE");
                ops.add("android.permission-group.SMS");
                break;
            case 1:
                ops.add(AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE);
                ops.add(AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE);
                ops.add("ACCESS_MEDIA_LOCATION");
                ops.add("LEGACY_STORAGE");
                ops.add("WRITE_MEDIA_AUDIO");
                ops.add("READ_MEDIA_AUDIO");
                ops.add("WRITE_MEDIA_VIDEO");
                ops.add("READ_MEDIA_VIDEO");
                ops.add("READ_MEDIA_IMAGES");
                ops.add("WRITE_MEDIA_IMAGES");
                ops.add("MANAGE_EXTERNAL_STORAGE");
                ops.add(AppOpsManager.OPSTR_PICTURE_IN_PICTURE);
                ops.add("android.permission-group.READ_MEDIA_AURAL");
                ops.add("android.permission-group.READ_MEDIA_VISUAL");
                ops.add("android.permission-group.STORAGE");
                break;
            case 2:
                ops.add("READ_CLIPBOARD");
                ops.add("WRITE_CLIPBOARD");
                break;
            case 3:
                ops.add("RUN_ANY_IN_BACKGROUND");
                break;
            case 4:
                ops.add("RUN_IN_BACKGROUND");
                break;
            case 5:
                ops.add(AppOpsManager.OPSTR_CAMERA);
                ops.add("android.permission-group.CAMERA");
                break;
            case 6:
                ops.add(AppOpsManager.OPSTR_RECORD_AUDIO);
                ops.add("TAKE_AUDIO_FOCUS");
                ops.add("android.permission-group.MICROPHONE");
                break;
            case 7:
                ops.add(AppOpsManager.OPSTR_COARSE_LOCATION);
                ops.add(AppOpsManager.OPSTR_FINE_LOCATION);
                ops.add(AppOpsManager.OPSTR_MOCK_LOCATION);
                ops.add(AppOpsManager.OPSTR_MONITOR_HIGH_POWER_LOCATION);
                ops.add(AppOpsManager.OPSTR_MONITOR_LOCATION);
                ops.add("android.permission-group.LOCATION");
                break;
            case 8:
                ops.add(AppOpsManager.OPSTR_READ_CALENDAR);
                ops.add(AppOpsManager.OPSTR_WRITE_CALENDAR);
                ops.add("android.permission-group.CALENDAR");
                break;
            case 9:
                ops.add(AppOpsManager.OPSTR_USE_SIP);
                ops.add("WIFI_SCAN");
                ops.add("BLUETOOTH_SCAN");
                ops.add("BLUETOOTH_ADVERTISE");
                ops.add("BLUETOOTH_CONNECT");
                ops.add("BLUETOOTH_ADMIN");
                ops.add("BLUETOOTH");
                ops.add("NEARBY_DEVICES");
                ops.add(AppOpsManager.OPSTR_BODY_SENSORS);
                ops.add("android.permission-group.NEARBY_DEVICES");
                ops.add("android.permission-group.SENSORS");
                ops.add("BODY_SENSORS");
                ops.add("BODY_SENSORS_BACKGROUND");
                ops.add("HIGH_SAMPLING_RATE_SENSORS");
                ops.add("SENSOR_ENABLE");
                ops.add("android.permission-group.HARDWARE_CONTROLS");
                ops.add("SENSOR_INFO");
                break;
            case 10:
                ops.add("android.permission-group.NOTIFICATIONS");
                ops.add("ACCESS_NOTIFICATIONS");
                ops.add("POST_NOTIFICATION");
                ops.add("android.permission.POST_NOTIFICATIONS");
                break;
            case 11:
                sb.append("am set-inactive  "+pkginfo.getPkgname()+" "+modestr);
                break;
            case 12:
                sb.append("am set-standby-bucket "+pkginfo.getPkgname()+" " + modestr );
                break;
            case 13:
                appopsCmdStr acs = new appopsCmdStr();
                sb.append(modestr.equals("true")?acs.enableAppByAPPUIDCMD(Integer.valueOf(pkginfo.getApkuid().trim())):acs.disableAppByAPPUIDCMD(Integer.valueOf(pkginfo.getApkuid().trim())));
                break;
        }
        if(apops_permis_index < 11){
            for (String op : ops) {
                sb.append(cmdHead+" "+op+" "+modestr+";");
            }
            sb.append(cmdWrite);
        }
        return sb.toString();
    }



}
