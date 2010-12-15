package studio.reno.SmsFilter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the statistics tab");
        setContentView(textview);
    }

}
