package org.fqaosp.utils;

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
                sb.append(cmdHead+" READ_PHONE_STATE "+modestr+";");
                sb.append(cmdHead+" READ_CONTACTS "+modestr+";");
                sb.append(cmdHead+" WRITE_CONTACTS "+modestr+";");
                sb.append(cmdHead+" READ_CALL_LOG "+modestr+";");
                sb.append(cmdHead+" WRITE_CALL_LOG "+modestr+";");
                sb.append(cmdHead+" CALL_PHONE "+modestr+";");
                sb.append(cmdHead+" READ_SMS "+modestr+";");
                sb.append(cmdHead+" WRITE_SMS "+modestr+";");
                sb.append(cmdHead+" SEND_SMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_SMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_EMERGECY_SMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_MMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_WAP_PUSH "+modestr+";");
                sb.append(cmdHead+" READ_ICC_SMS "+modestr+";");
                sb.append(cmdHead+" WRITE_ICC_SMS "+modestr+";");
                sb.append(cmdHead+" PROCESS_OUTGOING_CALLS "+modestr+";");
                sb.append(cmdHead+" READ_CELL_BROADCASTS "+modestr+";");
                sb.append(cmdHead+" android:add_voicemail "+modestr+";");
                sb.append(cmdHead+" android:answer_phone_calls "+modestr+";");
                sb.append(cmdHead+" android:call_phone "+modestr+";");
                sb.append(cmdHead+" android:read_call_log "+modestr+";");
                sb.append(cmdHead+" android:read_contacts "+modestr+";");
                sb.append(cmdHead+" android:read_cell_broadcasts "+modestr+";");
                sb.append(cmdHead+" android:read_phone_numbers "+modestr+";");
                sb.append(cmdHead+" android:read_phone_state "+modestr+";");
                sb.append(cmdHead+" android:read_sms "+modestr+";");
                sb.append(cmdHead+" android:receive_mms "+modestr+";");
                sb.append(cmdHead+" android:receive_sms "+modestr+";");
                sb.append(cmdHead+" android:receive_wap_push "+modestr+";");
                sb.append(cmdHead+" android:send_sms "+modestr+";");
                sb.append(cmdHead+" android:write_call_log "+modestr+";");
                sb.append(cmdHead+" android:write_contacts "+modestr+";");
                sb.append(cmdHead+" android:process_outgoing_calls "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CALL_LOG "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CONTACTS "+modestr+";");
                sb.append(cmdHead+" android.permission-group.PHONE "+modestr+";");
                sb.append(cmdHead+" android.permission-group.SMS "+modestr+";");
                break;
            case 1:
                sb.append(cmdHead+" READ_EXTERNAL_STORAGE "+modestr+";");
                sb.append(cmdHead+" WRITE_EXTERNAL_STORAGE "+modestr+";");
                sb.append(cmdHead+" ACCESS_MEDIA_LOCATION "+modestr+";");
                sb.append(cmdHead+" LEGACY_STORAGE "+modestr+";");
                sb.append(cmdHead+" WRITE_MEDIA_AUDIO "+modestr+";");
                sb.append(cmdHead+" READ_MEDIA_AUDIO "+modestr+";");
                sb.append(cmdHead+" WRITE_MEDIA_VIDEO "+modestr+";");
                sb.append(cmdHead+" READ_MEDIA_VIDEO "+modestr+";");
                sb.append(cmdHead+" READ_MEDIA_IMAGES "+modestr+";");
                sb.append(cmdHead+" WRITE_MEDIA_IMAGES "+modestr+";");
                sb.append(cmdHead+" MANAGE_EXTERNAL_STORAGE "+modestr+";");
                sb.append(cmdHead+" android:picture_in_picture "+modestr+";");
                sb.append(cmdHead+" android.permission-group.READ_MEDIA_AURAL "+modestr+";");
                sb.append(cmdHead+" android.permission-group.READ_MEDIA_VISUAL "+modestr+";");
                sb.append(cmdHead+" android.permission-group.READ_MEDIA_VISUAL "+modestr+";");
                sb.append(cmdHead+" android.permission-group.STORAGE "+modestr+";");
                break;
            case 2:
                sb.append(cmdHead+" READ_CLIPBOARD "+modestr+";");
                sb.append(cmdHead+" WRITE_CLIPBOARD "+modestr+";");
                break;
            case 3:
                sb.append(cmdHead+" RUN_ANY_IN_BACKGROUND "+modestr+";");
                break;
            case 4:
                sb.append(cmdHead+" RUN_IN_BACKGROUND "+modestr+";");
                break;
            case 5:
                sb.append(cmdHead+" CAMERA "+modestr+";");
                sb.append(cmdHead+" android:camera "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CAMERA "+modestr+";");
                break;
            case 6:
                sb.append(cmdHead+" RECORD_AUDIO "+modestr+";");
                sb.append(cmdHead+" android:record_audio "+modestr+";");
                sb.append(cmdHead+" TAKE_AUDIO_FOCUS "+modestr+";");
                sb.append(cmdHead+" android.permission-group.MICROPHONE "+modestr+";");
                break;
            case 7:
                sb.append(cmdHead+" COARSE_LOCATION "+modestr+";");
                sb.append(cmdHead+" FINE_LOCATION "+modestr+";");
                sb.append(cmdHead+" android:coarse_location "+modestr+";");
                sb.append(cmdHead+" android:fine_location "+modestr+";");
                sb.append(cmdHead+" android:mock_location "+modestr+";");
                sb.append(cmdHead+" android:monitor_location_high_power "+modestr+";");
                sb.append(cmdHead+" android:monitor_location "+modestr+";");
                sb.append(cmdHead+" android.permission-group.LOCATION "+modestr+";");
                break;
            case 8:
                sb.append(cmdHead+" READ_CALENDAR "+modestr+";");
                sb.append(cmdHead+" WRITE_CALENDAR "+modestr+";");
                sb.append(cmdHead+" android:write_calendar "+modestr+";");
                sb.append(cmdHead+" android:read_calendar "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CALENDAR "+modestr+";");
                break;
            case 9:
                sb.append(cmdHead+" WIFI_SCAN "+modestr+";");
                sb.append(cmdHead+" android:use_sip "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_SCAN "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_ADVERTISE "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_CONNECT "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_ADMIN "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH "+modestr+";");
                sb.append(cmdHead+" NEARBY_DEVICES "+modestr+";");
                sb.append(cmdHead+" android.permission-group.NEARBY_DEVICES "+modestr+";");
                sb.append(cmdHead+" android.permission-group.SENSORS "+modestr+";");
                break;
            case 10:
                sb.append(cmdHead+" android.permission-group.NOTIFICATIONS "+modestr+";");
                sb.append(cmdHead+" ACCESS_NOTIFICATIONS "+modestr+";");
                sb.append(cmdHead+" POST_NOTIFICATION "+modestr+";");
                sb.append(cmdHead+" android.permission.POST_NOTIFICATIONS "+modestr+";");
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
            sb.append(cmdWrite);
        }
        return sb.toString();
    }



}
