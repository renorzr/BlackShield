package studio.reno.SmsFilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AddBlacklistNumActivity extends Activity {
	ListView listView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addblacklistnum);
        final Resources res = getResources();
        final Config config = Config.instance(this);
        listView = (ListView) findViewById(R.id.smslist);
        SmsListAdapter adapter = new SmsListAdapter(this);
        final List<HashMap<String,String>> smsList = getSmsList();
        adapter.update(smsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				final CheckBox cbTrashMsg = new CheckBox(AddBlacklistNumActivity.this);
				cbTrashMsg.setText(res.getString(R.string.trash_this_msg));
				cbTrashMsg.setChecked(config.get("trashMsg", true));
				cbTrashMsg.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton b, boolean checked) {
						config.put("trashMsg", checked).commit();
					}			
				});
				final Map<String,String> msg=smsList.get(position);
				final String num=msg.get("from");
				
				new AlertDialog.Builder(AddBlacklistNumActivity.this)
				.setTitle(String.format(res.getString(R.string.add_this_blnum),num))
				.setMessage(msg.get("content"))
				.setView(cbTrashMsg)
				.setPositiveButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent i = new Intent();
						i.putExtra("number", num);
						i.putExtra("trashMsg", cbTrashMsg.isChecked());
						i.putExtra("id", msg.get("id"));
						i.putExtra("from", msg.get("from"));
						i.putExtra("content", msg.get("content"));
						i.putExtra("time", msg.get("time"));
						setResult(RESULT_OK,i);
						finish();
					}
				})
				.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
				})
				.show();
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

            int idIdx = cursor.getColumnIndexOrThrow("_id");
            int fromIdx = cursor.getColumnIndexOrThrow("address");
            int bodyIdx = cursor.getColumnIndexOrThrow("body");
            int timeIdx = cursor.getColumnIndexOrThrow("date");
            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                String id = cursor.getString(idIdx);
                String from = cursor.getString(fromIdx);
                String body = cursor.getString(bodyIdx);
                long time = cursor.getLong(timeIdx);
        		HashMap<String,String> m = new HashMap<String,String>();
        		m.put("id", id);
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
