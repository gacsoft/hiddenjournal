package com.gacsoft.hiddenjournal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Journal journal;
    String passwordHash;
    Date fromDate;
    Date toDate;
    int dateBeingPicked; //0 when DatePicker fromDate activated; 1 when toDate

    EditText from;
    EditText to;
    EditText title;
    EditText tags;
    EditText body;

    CheckBox checkBoxFrom;
    CheckBox checkBoxTo;
    CheckBox checkBoxTitle;
    CheckBox checkBoxTags;
    CheckBox checkBoxBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.button_search);
        passwordHash = getIntent().getStringExtra("com.gacsoft.hiddenjournal.password");
        journal = ObjectPasser.getJournal();

        ListView results = (ListView) findViewById(R.id.results);
        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    Entry entry = journal.getFilteredEntries().get(position);
                    openBrowser(entry);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) { //just in case
                }
            }
        });

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                fieldsChanged();
            }
        };

        CompoundButton.OnCheckedChangeListener cl = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fieldsChanged();
            }
        };

        checkBoxFrom = (CheckBox)findViewById(R.id.checkboxFrom);
        checkBoxTo = (CheckBox)findViewById(R.id.checkboxTo);
        checkBoxTitle = (CheckBox)findViewById(R.id.checkboxTitle);
        checkBoxTags = (CheckBox)findViewById(R.id.checkboxTags);
        checkBoxBody = (CheckBox)findViewById(R.id.checkboxBody);

        checkBoxFrom.setOnCheckedChangeListener(cl);
        checkBoxTo.setOnCheckedChangeListener(cl);
        checkBoxTitle.setOnCheckedChangeListener(cl);
        checkBoxTags.setOnCheckedChangeListener(cl);
        checkBoxBody.setOnCheckedChangeListener(cl);

        from = (EditText)findViewById(R.id.searchFrom);
        to = (EditText)findViewById(R.id.searchTo);
        title = (EditText)findViewById(R.id.searchTitle);
        tags = (EditText)findViewById(R.id.searchTags);
        body = (EditText)findViewById(R.id.searchBody);

        from.addTextChangedListener(tw);
        to.addTextChangedListener(tw);
        title.addTextChangedListener(tw);
        tags.addTextChangedListener(tw);
        body.addTextChangedListener(tw);

        Date today = Calendar.getInstance().getTime();
        Date monthAgo = Calendar.getInstance().getTime();
        if (today.getMonth() == 1) monthAgo.setMonth(12);
        else monthAgo.setMonth(monthAgo.getMonth() - 1);
        toDate = today;
        fromDate = monthAgo;
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        from.setText(df.format(fromDate));
        to.setText(df.format(toDate));
    }

    public void changeFromDate(View view) {
        int year = 1900 + fromDate.getYear();
        int month = fromDate.getMonth();
        int day = fromDate.getDate();
        dateBeingPicked = 0;

        DatePickerDialog dp = new DatePickerDialog(this, this, year, month, day);
        dp.show();
    }

    public void changeToDate(View view) {
        int year = 1900 + toDate.getYear();
        int month = toDate.getMonth();
        int day = toDate.getDate();
        dateBeingPicked = 1;

        DatePickerDialog dp = new DatePickerDialog(this, this, year, month, day);
        dp.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        if (dateBeingPicked == 0) { //event came from fromDate picker
            fromDate = cal.getTime();
            fromDate = DateHelper.toMidnight(fromDate); //useless
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            from.setText(df.format(fromDate));
        }
        else { //event came from toDate picker
            toDate = cal.getTime();
            toDate = DateHelper.toMidnight(toDate);
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            to.setText(df.format(toDate));
        }
    }

    public void onResume() {
        super.onResume();
        fieldsChanged();
    }

    private void fieldsChanged() {
        journal.clearFilters();
        journal.setPasswordFilter(passwordHash);
        if (checkBoxFrom.isChecked()) {
            journal.setDateFromFilter(fromDate);
        }
        if (checkBoxTo.isChecked()) {
            journal.setDateToFilter(toDate);
        }
        if (checkBoxTitle.isChecked()) {
            journal.setTitleFilter(title.getText().toString());
        }
        if (checkBoxTags.isChecked()) {
            List<String> tagsList = Arrays.asList(tags.getText().toString().split("\\s*,\\s*"));
            journal.setTagsFilter(tagsList);
        }
        if (checkBoxBody.isChecked()) {
            journal.setBodyFilter(body.getText().toString());
        }

        ListView listView = (ListView)findViewById(R.id.results);
        ArrayList<String> listItems = new ArrayList<String>();

        for (Entry e: journal.getFilteredEntries()) {
            listItems.add(e.toString());
        }
        ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.entry_list_view, listItems);
        listView.setAdapter(listAdapter);
    }

    public void openBrowser(Entry entry) {
        Intent intent = new Intent(this, EntryEditorActivity.class);
        ObjectPasser.putEntry(entry);
        startActivity(intent);
    }
}
