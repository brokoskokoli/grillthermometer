package com.example.stean1990.grillthermometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "Grillthermometer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        ((EditText) findViewById(R.id.nr_minTemp)).setText(Integer.toString(settings.getInt("min_temp_0",GrillThermometerSettings.min_temp_0)));
        ((EditText) findViewById(R.id.nr_maxTemp)).setText(Integer.toString(settings.getInt("min_temp_1",GrillThermometerSettings.min_temp_1)));
        ((EditText) findViewById(R.id.nr_minTemp1)).setText(Integer.toString(settings.getInt("max_temp_0", GrillThermometerSettings.max_temp_0)));
        ((EditText) findViewById(R.id.nr_maxTemp1)).setText(Integer.toString(settings.getInt("max_temp_1", GrillThermometerSettings.max_temp_1)));
        ((EditText) findViewById(R.id.txt_server)).setText(settings.getString("server_host", GrillThermometerSettings.server_host));

        Button button = (Button) findViewById(R.id.btn_start);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                GrillThermometerSettings.min_temp_0 = Integer.parseInt(((EditText) findViewById(R.id.nr_minTemp)).getText().toString());
                GrillThermometerSettings.max_temp_0 = Integer.parseInt(((EditText) findViewById(R.id.nr_maxTemp)).getText().toString());
                GrillThermometerSettings.min_temp_1 = Integer.parseInt(((EditText) findViewById(R.id.nr_minTemp1)).getText().toString());
                GrillThermometerSettings.max_temp_1 = Integer.parseInt(((EditText) findViewById(R.id.nr_maxTemp1)).getText().toString());
                String url = ((EditText) findViewById(R.id.txt_server)).getText().toString();
                GrillThermometerSettings.server_host =  url.trim();

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("min_temp_0", GrillThermometerSettings.min_temp_0);
                editor.putInt("min_temp_1", GrillThermometerSettings.min_temp_1);
                editor.putInt("max_temp_0", GrillThermometerSettings.max_temp_0);
                editor.putInt("max_temp_1", GrillThermometerSettings.max_temp_1);
                editor.putString("server_host", GrillThermometerSettings.server_host);

                // Commit the edits!
                editor.commit();

                startService();
            }
        });

        button = (Button) findViewById(R.id.btn_stop);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                stopService();
            }
        });



    }

    private void startService()
    {
        Context context = getApplicationContext();
        // use this to start and trigger a service
        Intent i= new Intent(context, PullService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        context.startService(i);
    }

    private void stopService()
    {
        Context context = getApplicationContext();
        // use this to start and trigger a service
        Intent i= new Intent(context, PullService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        context. stopService(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
