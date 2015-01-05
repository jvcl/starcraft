package au.com.innovus.starcraft2calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {


    PlaceholderFragment placeholderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            placeholderFragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction().add(R.id.container, placeholderFragment)
                    .commit();
        }

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        Date sourceDate = null;
        try {
            sourceDate = sourceFormat.parse("2015-01-05 23:30:00"); //Date is in KST now
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Get Calendar instance and current timezone of the device
        Calendar cal = Calendar.getInstance();
        TimeZone currentTZ = cal.getTimeZone();

        //Format the date to the current time zone of the device
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        destFormat.setTimeZone(currentTZ);

        //int currentOffsetFromUTC = TimeZone.getTimeZone("Asia/Seoul").getRawOffset() + (TimeZone.getTimeZone("Asia/Seoul").inDaylightTime(sourceDate) ? TimeZone.getTimeZone("Asia/Seoul").getDSTSavings() : 0);
        //String result = destFormat.format(sourceDate.getTime() + currentOffsetFromUTC);

        //Log conversion
        String result = destFormat.format(sourceDate);
        Log.d("TIMEZONE:", result);
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
