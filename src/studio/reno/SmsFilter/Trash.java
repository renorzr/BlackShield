package studio.reno.SmsFilter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

public class Trash {
	private static final String TRASH_FILENAME = "smsfilter.trash";
	private static Trash instance;
	private Context context;
	List<HashMap<String,String>> trashedMsgs;
	public static Trash instance(Context context){
		if (instance==null){
			instance = new Trash(context);
		}
		return instance;
	}
	
	private Trash(Context context){
		this.context = context;
	}
	
	public List<HashMap<String,String>> all(){
		load();
		return trashedMsgs;
	}
	
	public void add(HashMap<String,String> msg){
		load();
		trashedMsgs.add(msg);
		save();
	}
	
	public void add(SmsMessage[] msgs){
		load();
		for (SmsMessage msg: msgs){
			trashedMsgs.add(0, createTrashedMessage(msg));
		}
		save();		
	}
	
	public void delete(int pos){
		load();
		trashedMsgs.remove(pos);
		save();
	}
	
	public void clear(){
		trashedMsgs.clear();
		save();
	}
	
	public void save(List<HashMap<String,String>> msgs){
		trashedMsgs = msgs;
		save();
	}

	private void save(){
		try{
			Log.d("T","save "+trashedMsgs.size()+ " message(s)");
			FileOutputStream fos = context.openFileOutput(TRASH_FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(trashedMsgs);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void load() {
		Log.d("T","load");
		try {
			FileInputStream fis = context.openFileInput(TRASH_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			trashedMsgs = (List<HashMap<String,String>>) ois.readObject();
			ois.close();
			Log.d("T",trashedMsgs.size()+" message(s) loaded.");
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		trashedMsgs = new LinkedList<HashMap<String,String>>();
	}
	
	private HashMap<String,String> createTrashedMessage(SmsMessage msg) {
		HashMap<String,String> m = new HashMap<String,String>();
		m.put("from", msg.getOriginatingAddress());
		m.put("content", msg.getMessageBody());
		m.put("time", ""+msg.getTimestampMillis());
		return m;
	}
}
