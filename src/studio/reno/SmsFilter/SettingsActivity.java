package studio.reno.SmsFilter;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsActivity extends Activity {
	Dialog dlg;
	List<String> blacklist = new LinkedList<String>();
	CheckBox cbFilterOn;
	RadioGroup rgFilterMode;
	RadioButton useContacts;
	RadioButton useBlacklist;
	Button btnAddNum;
	ListView blacklistView;
	Config config;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
				final EditText input = new EditText(SettingsActivity.this);
				input.setSingleLine();
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Add blacklist number")
				.setMessage("? and * matches single/multiple number(s)")
				.setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							addBlacklistNumber(input.getText().toString());
						}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {}
				})
				.show();
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
	
	private void updateEnables(){
		boolean filterOn = cbFilterOn.isChecked();
		boolean blacklistOn = filterOn && rgFilterMode.getCheckedRadioButtonId()==R.id.useBlacklist;
		useContacts.setEnabled(filterOn);
		useBlacklist.setEnabled(filterOn);
		btnAddNum.setEnabled(blacklistOn);
		blacklistView.setEnabled(blacklistOn);
	}

	private void initBlacklist() {
        blacklistView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Remove blacklist number")
				.setMessage(String.format("Remove \"%s\" from blacklist?", blacklist.get(position)))
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							removeBlacklistNumber(position);
					        updateBlacklist();
						}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
		String ret="";
		for (String i: list){
			ret+=("|"+"^"+i.replace("*", "[0-9]+").replace("?", "[0-9]")+"$");
		}
		return ret.substring(1);
	}

	private String join(List<String> list, String seperator) {
		String joined="";
		for (String i: list){
			if (i.length()>0) joined+=(seperator+i);
		}
		return joined.substring(seperator.length());
	}

	private void updateBlacklist(){
        blacklistView.setAdapter(new ArrayAdapter<String>(SettingsActivity.this, R.layout.listitem, blacklist));
	}
}
