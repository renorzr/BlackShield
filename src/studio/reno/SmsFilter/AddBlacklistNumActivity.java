package studio.reno.SmsFilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AddBlacklistNumActivity extends Activity {
	ListView listView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addblacklistnum);
        listView = (ListView) findViewById(R.id.smslist);
        SmsListAdapter adapter = new SmsListAdapter(this);
        final List<HashMap<String,String>> smsList = getSmsList();
        adapter.update(smsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				Intent i = new Intent();
				String num=smsList.get(position).get("from");
				i.putExtra("number", num);
				setResult(RESULT_OK,i);
				finish();
			}        	
        });
    }

	private List<HashMap<String, String>> getSmsList() {
		Uri uri = Uri.parse("content://sms/inbox");
        List<HashMap<String, String>> messages = new LinkedList<HashMap<String, String>>();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                Log.i("ABL", "cursor is null.");
                return messages;
            }

            int fromIdx = cursor.getColumnIndexOrThrow("address");
            int bodyIdx = cursor.getColumnIndexOrThrow("body");
            int timeIdx = cursor.getColumnIndexOrThrow("date");
            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                String from = cursor.getString(fromIdx);
                String body = cursor.getString(bodyIdx);
                long time = cursor.getLong(timeIdx);
        		HashMap<String,String> m = new HashMap<String,String>();
        		m.put("from", from);
        		m.put("content", body);
        		m.put("time", ""+time);
                messages.add(m);
            }
        } catch (Exception e) {
            Log.e("ABL", e.getMessage());
        } finally {
            cursor.close();
        }
        return messages;
	}
}
