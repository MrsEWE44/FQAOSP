package org.fqaosp.naive;

import java.util.HashMap;

public final class term {
    static {
        System.loadLibrary("term");
    }

    public static native HashMap<String,String> runcmd(String cmd);

    public static native int systemcmd(String cmd);

}
