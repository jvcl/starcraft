package au.com.innovus.starcraft2calendar;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 3/01/15.
 */
public class XmlParser {
    private static final String ns = null;

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        Log.d("FOUND", "IN READENTRY");
        List<Entry> entries = new ArrayList<Entry>();
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
                    Log.d("PARSER", "sc2 found " + year + " " + month + " " + day + " " +hour+" "+minute+" "+ title);
                    entries.add(new Entry(

                            Integer.parseInt(year),
                            Integer.parseInt(month),
                            Integer.parseInt(day),
                            Integer.parseInt(hour),
                            Integer.parseInt(minute),
                            type,
                            title,
                            short_title,
                            description
                    ));

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
    public static class Entry {
        public final int year;
        public final int month;
        public final int day;
        public final int hour;
        public final int minutes;
        public final String type;
        public final String title;
        public final String short_title;
        public final String description;

        private Entry(int year, int month, int day, int hour, int minutes, String type, String title, String short_title, String description) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minutes = minutes;
            this.type = type;
            this.title = title;
            this.short_title = short_title;
            this.description = description;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
