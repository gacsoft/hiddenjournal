package com.gacsoft.hiddenjournal;

import android.graphics.Color;

import com.github.sundeepk.compactcalendarview.domain.Event;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gacsoft on 8/8/2016.
 */
public class Journal {
    private List<Entry> entries;
    private List<Entry> filteredEntries;

    public Journal() {
        entries = new ArrayList<Entry>();
        filteredEntries = new ArrayList<Entry>();
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
        clearFilters();
    }

    public void removeEntry(Entry entry) {
        entries.remove(entry);
        clearFilters();
    }

    public void clearFilters() {
        filteredEntries = entries;
    }

    public void setTitleFilter(String title) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            if (e.getTitle().contains(title))
                tmp.add(e);
        }
        filteredEntries = tmp;
    }

    public void setPasswordFilter(String password) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            if (e.getPassword().equals("")
                    || e.getPassword().equals(password))
                tmp.add(e);
        }
        filteredEntries = tmp;
    }

    public void setDateFilter(Date date) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            Date entryDate = e.getDate();
            if (entryDate.getYear() == date.getYear()
                    && entryDate.getMonth() == date.getMonth()
                    && entryDate.getDate() == date.getDate())
                tmp.add(e);
        }
        filteredEntries = tmp;
    }

    public void setDateFromFilter(Date date) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            Date entryDate = e.getDate();
            if (!entryDate.before(date))
                tmp.add(e);
        }
        filteredEntries = tmp;
    }

    public void setDateToFilter(Date date) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            Date entryDate = e.getDate();
            if (!entryDate.after(date))
                tmp.add(e);
        }
        filteredEntries = tmp;
    }

    public void setTagsFilter(List<String> tags) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            for (String tag : tags) {
                if (e.getTags().contains(tag)) {
                    tmp.add(e);
                    break; //don't add the same Entry twice if two tags match
                }
            }
        }
        filteredEntries = tmp;
    }

    public void setBodyFilter(String body) {
        List<Entry> tmp = new ArrayList<Entry>();
        for (Entry e : filteredEntries) {
            if (e.getBody().contains(body))
                tmp.add(e);
        }
        filteredEntries = tmp;
    }

    public List<Entry> getFilteredEntries() {
        return filteredEntries;
    }

    public void load(FileInputStream file) {
        try {
            entries = JournalXMLParser.parse(file);
        }
        catch (XmlPullParserException e) {
            //TODO inform user that db is corrupt or something
            System.out.println(e.toString());
        }
        catch (java.io.IOException e) {}
        filteredEntries = entries;
    }

    public List<Event> getMarkers(String password) {
        List<Event> markers = new ArrayList<Event>();
        for (Entry e : entries) {
            if (e.getPassword().equals("") || e.getPassword().equals(password))
            markers.add(new Event(Color.BLUE, e.getDate().getTime()));
        }
        return markers;
    }

    public void loadTest() {
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("abc");

        entries.add(new Entry("Title", Calendar.getInstance().getTime(), tags, "", "texttexttext"));
        entries.add(new Entry("Title2", Calendar.getInstance().getTime(), tags, "", "texttexttext2"));
        clearFilters();
    }

    public void save(FileOutputStream file) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        StringBuilder s = new StringBuilder();
        s.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        s.append("<entries>");
        for (Entry e : entries) {
            s.append("<entry>");
            s.append("<title>");
            s.append(e.getTitle());
            s.append("</title>");
            s.append("<date>");
            s.append(format.format(e.getDate()));
            s.append("</date>");
            s.append("<password>");
            s.append(e.getPassword());
            s.append("</password>");
            s.append("<tags>");
            for (String tag : e.getTags()) {
                s.append("<tag>");
                s.append(tag);
                s.append("</tag>");
            }
            s.append("</tags>");
            s.append("<body>" );
            s.append(e.getBody());
            s.append("</body>");
            s.append("</entry>");
        }
        s.append("</entries>");
        try {
            file.write(s.toString().getBytes());
        }
        catch (java.io.IOException e) {}
        finally {
            try {
                file.close();
            }
            catch (java.io.IOException e) {}
        }
    }


}
