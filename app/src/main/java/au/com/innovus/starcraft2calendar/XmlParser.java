package au.com.innovus.starcraft2calendar;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by jorge on 3/01/15.
 */


public class XmlParser {
    private static final String ns = null;

    public List<Event> parse(InputStream in) throws XmlPullParserException, IOException {
        try {

            Log.d("FOUND", "IN PARSE");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Event> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        Log.d("FOUND", "IN READENTRY");
        List<Event> entries = new ArrayList<Event>();
        parser.require(XmlPullParser.START_TAG, ns, "calendar");

        String year = "";
        String month = "";
        String day = "";
        String hour = "";
        String minute = "";
        String type = "";

        String title = "";
        String short_title = "";
        String description = "";
        Boolean isStarcraft = true;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("event-id")) {
                if (isStarcraft) {
                    entries.add(newEvent(year,month,day,hour,minute,type,title,short_title,description));
                }
                isStarcraft = true;
                continue;
            } else if (!isStarcraft) {
                continue;
            }
            //Log.d("PARSING", parser.getName());

            if (name.equals("month")) {
                year = parser.getAttributeValue("", "year");
                month = parser.getAttributeValue("", "num");

            }
            if (name.equals("day")) {
                day = parser.getAttributeValue("", "num");
            }
            if (name.equals("event")) {
                hour = parser.getAttributeValue("", "hour");
                minute = parser.getAttributeValue("", "minute");
            }
            if (name.equals("type")) {
                String text = parser.nextText();
                if (text.equalsIgnoreCase("StarCraft 2")) {
                    type = text;
                } else {

                    isStarcraft = false;
                }
            }
            if (name.equals("title")) {
                title = parser.nextText();
                //Log.d("PARSER", "sc2 found " + year + " " + month + " " + day + " " + title);
            }
            if (name.equals("short-title")) {
                short_title = parser.nextText();
            }
            if (name.equals("description")) {
                description = parser.nextText();
            }
        }
        return entries;
    }

    private Event newEvent(String year, String month, String day, String hour,
                           String minute, String type, String title, String short_title,
                           String description){
        Log.d("PARSER", "sc2 found " + year + " " + month + " " + day + " " +hour+" "+minute+" "+ title);
        return new Event(

                year,
                month,
                day,
                hour,
                minute,
                type,
                title,
                short_title,
                description
        );
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class Event {
        public final String year;
        public String month;
        public String day;
        public final String hour;
        public final String minutes;
        public final String type;
        public final String title;
        public final String short_title;
        public final String description;
        public String date;
        private Date localTime = null;

        private Event(String year, String month, String day, String hour, String minutes, String type, String title, String short_title, String description) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minutes = minutes;
            this.type = type;
            this.title = title;
            this.short_title = short_title;
            this.description = description;

            if(this.month.length()==1)
                this.month = "0"+this.month;
            if(this.day.length()==1)
                this.day = "0"+this.day;
            date = year+"-"+month+"-"+day+" "+hour+":"+minutes+":"+"00";
            date = formatDate(date);
        }

        @Override
        public String toString() {
            return title;
        }

        private String formatDate(String date){

            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sourceFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            Date sourceDate = null;
            try {
                sourceDate = sourceFormat.parse(date); //Date is in KST now
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

            //Save a Date object with the local time to later compare
            try {
                localTime = destFormat.parse(result); //Date is in KST now
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
        public boolean isOlderEvent(){

            Date now = new Date();
            return localTime.compareTo(now) < 0;

        }
    }
}
