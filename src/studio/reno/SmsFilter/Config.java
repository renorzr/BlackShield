package studio.reno.SmsFilter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class Config {
	private static final String CONFIG_FILENAME = "smsfilter.cfg";
	private static Config config = null;
	private Map<String,String> map = new HashMap<String,String>();
	private Context context;
	public static Config instance(Context context){
		if (config==null){
			config = new Config(context.getApplicationContext());
		}
		return config;
	}
	
	@SuppressWarnings("unchecked")
	private Config(Context c){
		context = c;
		try {
			FileInputStream fis = context.openFileInput(CONFIG_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (Map<String,String>) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Config commit(){
		try {
			FileOutputStream fos = context.openFileOutput(CONFIG_FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(map);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public Config put(String key, String value){
		map.put(key, value);
		return config;
	}

	public Config put(String key, boolean value) {
		return put(key,value?-1:0);
	}
	
	public Config put(String key, int value){
		return put(key, String.format("%d", value));
	}
	
	public String get(String key, String def){
		return map.containsKey(key)?map.get(key):def;
	}
	
	public boolean get(String key, boolean def){
		return get(key, 0)!=0;
	}
	
	public int get(String key, int def){
		return Integer.parseInt(get(key, String.format("%d",def)));
	}
}
