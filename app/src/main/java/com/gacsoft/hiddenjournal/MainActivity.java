package com.gacsoft.hiddenjournal;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DateFormat;
import android.view.inputmethod.InputMethodManager;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    CompactCalendarView calendar;
    Date selectedDay;
    ListView listView;
    ArrayAdapter listAdapter;
    String passwordHash = "";
    Journal journal;

    public static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ObjectPasser.setContext(getApplicationContext());

        //prevent on-screen keyboard from popping up every time this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    Entry entry = journal.getFilteredEntries().get(position);
                    openBrowser(entry);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) { //just in case
                }
            }
        });

        calendar = (CompactCalendarView) findViewById(R.id.calendar);
        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDay = dateClicked;
                setCurrentDay(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                selectedDay = firstDayOfNewMonth;
                setCurrentMonth(firstDayOfNewMonth);
                setCurrentDay(firstDayOfNewMonth);
            }
        });

        journal = new Journal();
        ObjectPasser.putJournal(journal);
        loadJournal();
        BackupManager.backupIfNeeded();
        //journal.setDateFilter(DateHelper.todayMidnight());
        selectedDay = Calendar.getInstance().getTime();
        journal.setDateFilter(selectedDay);
        journal.setPasswordFilter(passwordHash);

        Date current = Calendar.getInstance().getTime();
        setCurrentMonth(current);
        setCurrentDay(current);
    }

//    public void onRestart() {
//        super.onRestart();
//        loadJournal();
//    }

    public void onResume() {
        super.onResume();
        if (DEBUG) System.out.println("resuming");
        BackupManager.backupIfNeeded();
        journal.clearFilters();
        journal.setDateFilter(selectedDay);
        journal.setPasswordFilter(passwordHash);
        System.out.println(selectedDay);
        populateList();
        populateMarkers();
    }

    private void populateMarkers() {
        calendar.removeAllEvents();
        calendar.addEvents(journal.getMarkers(passwordHash));
    }

    public void onStop() {
        super.onStop();
        saveJournal();
    }

    private void loadJournal() {
        if (DEBUG) System.out.println("loading...");
        try {
            journal.load(openFileInput("journal.xml"));
        }
        catch (java.io.FileNotFoundException e) {
            //do nothing, journal will be created on next save
            if (DEBUG) System.out.println("journal file not found!");
        }
        if (DEBUG) System.out.println("loading done");
    }

    private void saveJournal() {
        BackupManager.backupIfNeeded();
        if (DEBUG) System.out.println("saving...");
        try {
            journal.save(openFileOutput("journal.xml", Context.MODE_PRIVATE));
        }
        catch (java.io.FileNotFoundException e) {
            if (DEBUG) System.out.println("error while saving journal!");
            e.printStackTrace();
        }
        if (DEBUG) System.out.println("saving done");
    }

    public void newEntry(View view) {
        Entry blankEntry = new Entry();
        blankEntry.setPassword(passwordHash);
        blankEntry.setDate(Calendar.getInstance().getTime());
        journal.addEntry(blankEntry);
        ObjectPasser.putEntry(blankEntry);
        Intent intent = new Intent(this, EntryEditorActivity.class);
        startActivity(intent);
    }

    public void openBrowser(Entry entry) {
        Intent intent = new Intent(this, EntryEditorActivity.class);
        ObjectPasser.putEntry(entry);
        startActivity(intent);
    }

    public void openSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("com.gacsoft.hiddenjournal.password", passwordHash);
        startActivity(intent);
    }

    public void openConfig(View view) {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    public void populateList() {
        listView = (ListView)findViewById(R.id.list_view);
        ArrayList<String> listItems = new ArrayList<String>();

        for (Entry e: journal.getFilteredEntries()) {
            listItems.add(e.toString());
        }
        listAdapter = new ArrayAdapter<String>(this, R.layout.entry_list_view, listItems);
        listView.setAdapter(listAdapter);
    }

    public void enterPassword(View view) {
        //hide keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ?
                null :
                getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String enteredPassword = ((EditText)findViewById(R.id.edit_password)).getText().toString();
        if (enteredPassword.equals("")) passwordHash = "";
        else passwordHash = PasswordHashHelper.getHash(enteredPassword);
        if (DEBUG) System.out.println("Hash: " + passwordHash);
        journal.clearFilters();
        journal.setDateFilter(selectedDay);
        journal.setPasswordFilter(passwordHash);
        populateList();
        populateMarkers();
    }

    void setCurrentMonth(Date date) {
        TextView year_month = (TextView) findViewById(R.id.year_month);
        SimpleDateFormat sdf = new SimpleDateFormat("LLLL", Locale.getDefault());
        year_month.setText(sdf.format(date) + " " + (date.getYear() + 1900));
    }

    void setCurrentDay(Date date) {
        TextView selected = (TextView) findViewById(R.id.selected_day);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        selected.setText(String.format(getResources().getString(R.string.entries), df.format(date)));
        journal.clearFilters();
        journal.setDateFilter(date);
        journal.setPasswordFilter(passwordHash);
        populateList();
        populateMarkers();
    }

}
