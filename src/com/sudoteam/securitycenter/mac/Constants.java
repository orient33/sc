package com.sudoteam.securitycenter.mac;
import com.sudoteam.securitycenter.R;

import android.app.AppOpsManager;
import com.sudoteam.securitycenter.mac.AppOpsState.OpsTemplate;

public class Constants {
	
    public static final OpsTemplate template = new OpsTemplate(
            new int[] {
            		AppOpsManager.OP_COARSE_LOCATION,
                    AppOpsManager.OP_FINE_LOCATION,
                    AppOpsManager.OP_GPS,
                    AppOpsManager.OP_WIFI_SCAN,
                    AppOpsManager.OP_NEIGHBORING_CELLS,
                    AppOpsManager.OP_MONITOR_LOCATION,
                    AppOpsManager.OP_MONITOR_HIGH_POWER_LOCATION,
                    AppOpsManager.OP_READ_CONTACTS,
                    AppOpsManager.OP_WRITE_CONTACTS,
                    AppOpsManager.OP_READ_CALL_LOG,
                    AppOpsManager.OP_WRITE_CALL_LOG,
                    AppOpsManager.OP_READ_CALENDAR,
                    AppOpsManager.OP_WRITE_CALENDAR,
                    AppOpsManager.OP_READ_CLIPBOARD,
                    AppOpsManager.OP_WRITE_CLIPBOARD,
                    AppOpsManager.OP_READ_SMS,
                    AppOpsManager.OP_RECEIVE_SMS,
                    AppOpsManager.OP_RECEIVE_EMERGECY_SMS,
                    AppOpsManager.OP_RECEIVE_MMS,
                    AppOpsManager.OP_RECEIVE_WAP_PUSH,
                    AppOpsManager.OP_WRITE_SMS,
                    AppOpsManager.OP_SEND_SMS,
                    AppOpsManager.OP_READ_ICC_SMS,
                    AppOpsManager.OP_WRITE_ICC_SMS,
                    AppOpsManager.OP_VIBRATE,
                    AppOpsManager.OP_CAMERA,
                    AppOpsManager.OP_RECORD_AUDIO,
                    AppOpsManager.OP_PLAY_AUDIO,
                    AppOpsManager.OP_TAKE_MEDIA_BUTTONS,
                    AppOpsManager.OP_TAKE_AUDIO_FOCUS,
                    AppOpsManager.OP_AUDIO_MASTER_VOLUME,
                    AppOpsManager.OP_AUDIO_VOICE_VOLUME,
                    AppOpsManager.OP_AUDIO_RING_VOLUME,
                    AppOpsManager.OP_AUDIO_MEDIA_VOLUME,
                    AppOpsManager.OP_AUDIO_ALARM_VOLUME,
                    AppOpsManager.OP_AUDIO_NOTIFICATION_VOLUME,
                    AppOpsManager.OP_AUDIO_BLUETOOTH_VOLUME,
                    AppOpsManager.OP_POST_NOTIFICATION,
                    AppOpsManager.OP_ACCESS_NOTIFICATIONS,
                    AppOpsManager.OP_CALL_PHONE,
                    AppOpsManager.OP_WRITE_SETTINGS,
                    AppOpsManager.OP_SYSTEM_ALERT_WINDOW,
                    AppOpsManager.OP_WAKE_LOCK
                    },
            new boolean[] { 
            		true,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    false,
                    false,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    false,
                    true,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    true,
                    true,
                    true
                    }
            );
}