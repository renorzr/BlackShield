package studio.reno.SmsFilter;

import java.util.regex.Pattern;

import android.telephony.SmsMessage;
import android.util.Log;

public class SmsChecker {
	private static SmsChecker filter = null;
	public static SmsChecker create(){
		if (filter==null){
			filter = new SmsChecker();
		}
		return filter;
	}
	
	private SmsChecker(){
		
	}

	public boolean isSpam(Config config, SmsMessage[] msgs) {
//		Log.d("isSpam",msgs[0].getOriginatingAddress()+" matches "+config.get("compiledblacklist", "^$")+"?");
		String sender = msgs[0].getOriginatingAddress();
		return config.get("filterOn", true)
			&& !isContact(sender)
			&& (config.get("useBlacklist", true)?Pattern.matches(config.get("compiledblacklist", "^$"), sender):true);
	}

	private boolean isContact(String num) {
		return false;
	}
}
