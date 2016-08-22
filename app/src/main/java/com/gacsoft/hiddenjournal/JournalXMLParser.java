package com.gacsoft.hiddenjournal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gacsoft on 8/9/2016.
 */
public class JournalXMLParser {

    public static List<Entry> parse(InputStream file) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(file, null);
            parser.nextTag();
            return readXML(parser);
        } finally {
            file.close();
        }
    }

    private static List<Entry> readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();
        parser.require(XmlPullParser.START_TAG, null, "entries");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "entry");
        String title = "";
        Date date = new Date();
        List<String> tags = new ArrayList<String>();
        String password = "";
        String body = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                try {
                    title = readTitle(parser);
                }
                catch (XmlPullParserException e) {
                    //we'll try to use a default value
                }
            } else if (name.equals("date")) {
                try {
                    date = readDate(parser);
                }
                catch (XmlPullParserException e) {
                    //we'll try to use a default value
                }
            } else if (name.equals("tags")) {
                try {
                    tags = readTags(parser);
                }
                catch (XmlPullParserException e) {
                    //we'll try to use a default value
                 }
            } else if (name.equals("password")) {
                try {
                    password = readPassword(parser);
                }
                catch (XmlPullParserException e) {
                    //we'll try to use a default value
                }
            } else if (name.equals("body")) {
                try {
                    body = readBody(parser);
                }
                catch (XmlPullParserException e) {
                    //we'll try to use a default value
                }
            } else {
                skip(parser);
            }
        }
        return new Entry(title, date, tags, password, body);
    }

    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    private static Date readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "date");
        String dateText = readText(parser);
        if (dateText.equals("")) //this should not happen, new entries are assigned a date automatically
            return new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date;
        try {
            date = format.parse(dateText);
        } catch (ParseException e) {
            return new Date(); //this shouldn't happen either, we always save with the same format
        }
        parser.require(XmlPullParser.END_TAG, null, "date");
        return date;
    }

    private static List<String> readTags(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "tags");
        List<String> tags = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("tag"))
                tags.add(readTag(parser));
            else skip(parser);
        }
        parser.require(XmlPullParser.END_TAG, null, "tags");
        return tags;
    }

    private static String readTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "tag");
        String tag = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "tag");
        return tag;
    }


    private static String readPassword(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "password");
        String password = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "password");
        return password;
    }

    private static String readBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "body");
        String body = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "body");
        return body;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
}
