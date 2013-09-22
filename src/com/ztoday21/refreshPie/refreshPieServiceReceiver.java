package com.ztoday21.refreshPie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * User: hermit
 * Date: 13. 9. 22
 * Time: ¿ÀÀü 12:57
 */
public class refreshPieServiceReceiver extends BroadcastReceiver {
	@SuppressWarnings("static-access")
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{
			SharedPreferences prefs = context.getSharedPreferences(main._saveName, context.MODE_PRIVATE);

			if (prefs.getBoolean(Setting.keyAutostart, false))
				context.startService(new Intent(context,service_main.class));
		}
	}
}
