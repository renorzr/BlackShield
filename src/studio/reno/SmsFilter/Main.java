package studio.reno.SmsFilter;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Main extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, StatisticsActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        // Do the same for the other tabs
        intent = new Intent().setClass(this, SettingsActivity.class);
        spec = tabHost.newTabSpec("settings").setIndicator("Settings",
                          res.getDrawable(R.drawable.ic_tab_set))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, TrashActivity.class);
        spec = tabHost.newTabSpec("trash").setIndicator("Trash",
                          res.getDrawable(R.drawable.ic_tab_set))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, StatisticsActivity.class);
        spec = tabHost.newTabSpec("statistics").setIndicator("Statistics",
                res.getDrawable(R.drawable.ic_tab_stat))
            .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}