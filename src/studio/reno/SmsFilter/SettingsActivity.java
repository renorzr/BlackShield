package studio.reno.SmsFilter;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsActivity extends Activity {
	protected static final int ADDBLNUM = 0;
	Dialog dlg;
	List<String> blacklist = new LinkedList<String>();
	CheckBox cbFilterOn;
	RadioGroup rgFilterMode;
	RadioButton useContacts;
	RadioButton useBlacklist;
	Button btnAddNum;
	Button btnAddNumFromInbox;
	ListView blacklistView;
	Config config;
	Resources res;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.settings);
    
        config = Config.instance(this);

        initUI();
        initBlacklist();
        updateBlacklist();
    }

	private void initUI() {
		boolean filterOn = config.get("filterOn", true);
		boolean blacklistOn = config.get("useBlacklist", true);
		
		btnAddNum = (Button) findViewById(R.id.btnAddNum);
		btnAddNumFromInbox = (Button) findViewById(R.id.btnAddNumFromInbox);
		cbFilterOn = (CheckBox)findViewById(R.id.cbFilterOn);
		rgFilterMode = (RadioGroup)findViewById(R.id.rgFilterMode);
		useContacts = (RadioButton)findViewById(R.id.useContacts);
		useBlacklist = (RadioButton)findViewById(R.id.useBlacklist);
		blacklistView = (ListView) findViewById(R.id.listFilterNumbers);
		
		cbFilterOn.setChecked(filterOn);
		(blacklistOn?useBlacklist:useContacts).setChecked(true);
		updateEnables();
		
		btnAddNum.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				final EditText numInput = getNumInput();
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(R.string.add_blacklist)
				.setMessage(R.string.add_blacklist_tip)
				.setView(numInput)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							addBlacklistNumber(numInput.getText().toString());
						}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {}
				})
				.show();
			}
        });
		
		btnAddNumFromInbox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(SettingsActivity.this, AddBlacklistNumActivity.class);
				startActivityForResult(i, ADDBLNUM);
			}			
		});
        
        cbFilterOn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				config.put("filterOn", isChecked).commit();
				updateEnables();
			}
        });
        
        rgFilterMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				config.put("useBlacklist", rgFilterMode.getCheckedRadioButtonId()==R.id.useBlacklist).commit();
				updateEnables();
			}
        });
	}
	
	private EditText getNumInput(){
		EditText v = new EditText(this);
		v.setKeyListener(new DigitsKeyListener(){
			@Override
			protected char[] getAcceptedChars() {
				return new char[]{'0','1','2','3','4','5','6','7','8','9','+','?','*'};
			}
		});
		v.setSingleLine();

		return v;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		switch (requestCode){
		case ADDBLNUM:
			if (resultCode == RESULT_OK){
				String num = data.getExtras().getString("number");
				addBlacklistNumber(num);
				Toast.makeText(this, String.format(res.getString(R.string.num_added), num), Toast.LENGTH_SHORT).show();
			}
			break;
		}
    }
	
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	menu.add(res.getString(R.string.about)).setOnMenuItemClickListener(new OnMenuItemClickListener(){
//			@Override
//			public boolean onMenuItemClick(MenuItem mi) {
//				TextView message = new TextView(SettingsActivity.this);
//				SpannableString s = new SpannableString(getText(R.string.about_body));
//				Linkify.addLinks(s, Linkify.WEB_URLS);
//				message.setText(s);
//				message.setMovementMethod(LinkMovementMethod.getInstance());
//
//				new AlertDialog.Builder(SettingsActivity.this)
//				.setTitle(R.string.app_name)
//				.setView(message)
//				//.setMessage(R.string.about_body)
//				.setPositiveButton(R.string.ok, null)
//				.show();
//				return false;
//			}    		
//    	});
//    	
//    	return true;
//    }
    
	private void updateEnables(){
		boolean filterOn = cbFilterOn.isChecked();
		boolean blacklistOn = filterOn && rgFilterMode.getCheckedRadioButtonId()==R.id.useBlacklist;
		useContacts.setEnabled(filterOn);
		useBlacklist.setEnabled(filterOn);
		btnAddNum.setEnabled(blacklistOn);
		btnAddNumFromInbox.setEnabled(blacklistOn);
		blacklistView.setEnabled(blacklistOn);
	}

	private void initBlacklist() {
        blacklistView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
				final EditText numInput = getNumInput();
				numInput.setText(blacklist.get(position));
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(R.string.remove_blacklist)
//				.setMessage(String.format(res.getString(R.string.num_remove), blacklist.get(position)))
				.setView(numInput)
				.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface arg0, int arg1) {
						blacklist.remove(position);
						blacklist.add(position, numInput.getText().toString());
						saveBlacklist();
						updateBlacklist();
					}
				})
				.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							removeBlacklistNumber(position);
					        updateBlacklist();
						}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {}
				})
				.show();
			}        	
        });
        loadBlacklist();
	}

    private void saveBlacklist(){
//		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//		prefs.edit()
//			.putString("blacklist", join(blacklist,":"))
//			.putString("compiledblacklist", compile(blacklist))
//		.commit();    	
    	config
			.put("blacklist", join(blacklist,":"))
			.put("compiledblacklist", compile(blacklist))
		.commit();
    }
    
    private void loadBlacklist(){
//		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		blacklist.clear();
		for (String i: config.get("blacklist", "").split(":")){
			blacklist.add(i);
		}
    }
    
	private void addBlacklistNumber(String num) {
//		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		blacklist.add(num);
		saveBlacklist();
		updateBlacklist();
	}

	private void removeBlacklistNumber(int position) {
		blacklist.remove(position);
		saveBlacklist();
	}
	
	private String compile(List<String> list) {
		String ret="^$";
		for (String i: list){
			ret+=("|"+"^"+i.replace("*", "[0-9]+").replace("?", "[0-9]")+"$");
		}
		return ret;
	}

	private String join(List<String> list, String seperator) {
		String joined="";
		for (String i: list){
			if (i.length()>0) joined+=(seperator+i);
		}
		if (joined.length()>seperator.length()) 
			joined = joined.substring(seperator.length());
		return joined;
	}

	private void updateBlacklist(){
        blacklistView.setAdapter(new ArrayAdapter<String>(SettingsActivity.this, R.layout.blacklistitem, blacklist));
	}
}
