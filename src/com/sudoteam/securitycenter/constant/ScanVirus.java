
package com.sudoteam.securitycenter.constant;

import java.lang.String;

public class ScanVirus {

    public static final String SCAN_LOG_DB = "scanlog.db";

    public static final int SCAN_WORKING = 0;

    public static final int SCAN_PAUSE = 1;

    public static final int SCAN_COMPLETE_AND_BACK = 2;

    public static final int SCAN_CLEAR_VIRUS = 3;

    public static final int SCAN_STATE_UNCOMPLETE = 0x04;

    public static final int SCAN_STATE_COMPLETE = 0x08;

}