package studio.reno.SmsFilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	Config config = Config.instance(context);
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";
        SmsChecker checker = SmsChecker.create();
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                str += String.format("SMS from %s:%s\n", msgs[i].getOriginatingAddress(), msgs[i].getMessageBody().toString());
            }
            
            //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            Log.d("sms",str);
            if (checker.isSpam(config, msgs)) this.abortBroadcast();
        }                         
    }
}