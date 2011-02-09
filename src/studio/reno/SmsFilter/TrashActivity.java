package studio.reno.SmsFilter;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TrashActivity extends Activity {
	ListView listView;
	Trash trash;
    SmsListAdapter adapter;
    Resources res;
    View textEmpty; 
    Config config;
    final int PICK_CONTACT=999;

    BroadcastReceiver updateTrashReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			refresh();
		}    	
    };
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = Config.instance(this);
        res = getResources();
        setContentView(R.layout.trash);
        trash = Trash.instance(this);
        listView = (ListView) findViewById(R.id.spamlist);
        textEmpty = findViewById(R.id.trash_empty); 
        refresh();
		registerReceiver(updateTrashReceiver, new IntentFilter("UPDATE_TRASH"));		

		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {				
				CheckBox cbAddContactNum = new CheckBox(TrashActivity.this);
				cbAddContactNum.setText(res.getString(R.string.add_contact_num));
				cbAddContactNum.setChecked(config.get("addContactNum", true));
				cbAddContactNum.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton b, boolean checked) {
						config.put("addContactNum", checked).commit();
					}			
				});

				final HashMap<String,String> msg = trash.trashedMsgs.get(position);
				final String num = msg.get("from");
				new AlertDialog.Builder(TrashActivity.this)
				.setTitle(res.getString(R.string.send_from)+num)
				.setMessage(msg.get("content"))
				.setView(cbAddContactNum)
				.setPositiveButton(res.getString(R.string.delete), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						trash.delete(position);
						refresh();
					}
				})
				.setNeutralButton(res.getString(R.string.restore), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						restore(msg);
						trash.delete(position);
						refresh();
						if(config.get("addContactNum", true && SmsChecker.isContact(TrashActivity.this, num))){
							Intent intent = new Intent(Intent.ACTION_INSERT);
							intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
							intent.putExtra(ContactsContract.Intents.Insert.PHONE, num);
							startActivityForResult(intent, PICK_CONTACT);
						}
					}
				})
				.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {}
				})
				.show();
			}
		});
    }

	private void restore(HashMap<String, String> msg) {
		ContentValues values = new ContentValues();
		values.put("address", msg.get("from"));
		values.put("body", msg.get("content"));
		values.put("date", Long.parseLong(msg.get("time")));
		getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}
	
	private void refresh(){
        adapter = new SmsListAdapter(this);
		adapter.update(trash.all());
        listView.setAdapter(adapter);
        if (trash.all().isEmpty()){
        	listView.setVisibility(View.INVISIBLE);
        	textEmpty.setVisibility(View.VISIBLE);
        } else{
        	listView.setVisibility(View.VISIBLE);
        	textEmpty.setVisibility(View.INVISIBLE);
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(res.getString(R.string.clear_all)).setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				trash.clear();
				refresh();
				return false;
			}    		
    	});

		return true;
    }}
