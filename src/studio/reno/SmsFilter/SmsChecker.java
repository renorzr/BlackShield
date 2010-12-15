package studio.reno.SmsFilter;

import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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

	public boolean isSpam(Context context, SmsMessage[] msgs) {
    	Config config = Config.instance(context);

//		Log.d("isSpam",msgs[0].getOriginatingAddress()+" matches "+config.get("compiledblacklist", "^$")+"?");
		String sender = msgs[0].getOriginatingAddress();
		return config.get("filterOn", true)
			&& !isContact(context, sender)
			&& (config.get("useBlacklist", true)?Pattern.matches(config.get("compiledblacklist", "^$"), sender):true);
	}

	private boolean isContact(Context context, String num) {
		Cursor cur = context.getContentResolver().query(Phone.CONTENT_URI, new String[] {Phone.NUMBER},Phone.NUMBER+"=?",new String[]{num},null);
//		Log.d("checker",num+" is"+(cur.getCount()>0?"":" not")+" contact");
		return cur.getCount()>0;
	}
}
