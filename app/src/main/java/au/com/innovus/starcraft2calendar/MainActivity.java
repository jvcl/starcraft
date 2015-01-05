package au.com.innovus.starcraft2calendar;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity implements PlaceholderFragment.PlaceHolderInterface, EventDetailFragment.OnFragmentInteractionListener {


    private PlaceholderFragment placeholderFragment;
    private EventDetailFragment eventDetailFragment;
    private XmlParser.Event currentSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            placeholderFragment = new PlaceholderFragment();
            eventDetailFragment = EventDetailFragment.newInstance();

            getFragmentManager().beginTransaction().add(R.id.container, placeholderFragment)
                    .commit();
        }
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

    //Replace the fragment
    @Override
    public void showDetails(XmlParser.Event event) {
        currentSelected = event;
        if (eventDetailFragment!= null && !eventDetailFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.container, eventDetailFragment).commit();
        }
    }


    @Override
    public XmlParser.Event getSelected() {
        return currentSelected;
    }
}
