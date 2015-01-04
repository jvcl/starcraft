package au.com.innovus.starcraft2calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by jorge on 3/01/15.
 */
public class PlaceholderFragment extends Fragment implements AdapterView.OnItemClickListener {

    Place mHolder;
    private static final String URL =
            "http://www.teamliquid.net/calendar/xml/calendar.xml";
    List<XmlParser.Entry> entries = null;



    public interface Place{

        public List<XmlParser.Entry> getArray();
    }

    public PlaceholderFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mHolder = (Place) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mHolder = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        new DownloadXmlTask().execute(URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "Connection error";
            } catch (XmlPullParserException e) {
                return "XML error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            loadIU();
        }
    }

    private String loadXmlFromNetwork(String urlString) throws  XmlPullParserException, IOException{

        XmlParser xmlParser = new XmlParser();

        InputStream stream = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");
        try {
            stream = downloadUrl(urlString);
            Log.d("MAIN", "stream donwloaded");
            entries = xmlParser.parse(stream);
            Log.d("ENTRIES SIZE", ""+entries.size());
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return "";
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    public void loadIU() {

        ListView list = (ListView) getActivity().findViewById(R.id.listView);

        EventAdapter adapter = new EventAdapter(getActivity().getApplicationContext(),R.layout.list_view_layout, entries);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    class EventAdapter extends ArrayAdapter<XmlParser.Entry> {

        List<XmlParser.Entry> entries;
        public EventAdapter(Context context, int resource, List<XmlParser.Entry> objects) {
            super(context, resource, objects);
            entries = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.list_view_layout, parent, false);
            TextView title = (TextView) v.findViewById(R.id.text_view_list_title);
            title.setText(entries.get(position).title);
            TextView time = (TextView) v.findViewById(R.id.text_view_list_time);
            time.setText(""+ entries.get(position).day);
            return v;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
