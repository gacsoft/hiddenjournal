package com.gacsoft.hiddenjournal;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryEditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private Entry entry;
    private Date entryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);
        setTitle(R.string.edit);

        loadEntry();
    }

    public void save(View view) {
        saveChanges();
    }

    public void reset(View view) {
        loadEntry();
    }

    public void delete(View view) {
        ObjectPasser.getJournal().removeEntry(entry);
        finish();
    }

    public void changeDate(View view) {
        int year = 1900 + entryDate.getYear();
        int month = entryDate.getMonth();
        int day = entryDate.getDate();

        DatePickerDialog dp = new DatePickerDialog(this, this, year, month, day);
        dp.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        entryDate = cal.getTime();
        TextView dateView = (TextView)findViewById(R.id.dateView);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        dateView.setText(df.format(entryDate));
    }

    private void loadEntry() {
        entry = ObjectPasser.getEntry();

        EditText title = (EditText)findViewById(R.id.entry_title);
        title.setText(entry.getTitle());

        EditText tags = (EditText)findViewById(R.id.entry_tags);
        StringBuilder sb = new StringBuilder();
        int numTags = entry.getTags().size();
        for (int i = 0; i < numTags; i++) {
            sb.append(entry.getTags().get(i));
            if (i < numTags - 1)
                sb.append(", ");
        }
        tags.setText(sb.toString());

        TextView dateView = (TextView)findViewById(R.id.dateView);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        entryDate = entry.getDate();
        dateView.setText(df.format(entryDate));

        EditText body = (EditText)findViewById(R.id.entry_body);
        body.setText(entry.getBody());
    }

    public void saveChanges() {
        Entry entry = ObjectPasser.getEntry();
        EditText title = (EditText)findViewById(R.id.entry_title);
        entry.setTitle(title.getText().toString());
        EditText tags = (EditText)findViewById(R.id.entry_tags);
        List<String> tagsList = Arrays.asList(tags.getText().toString().split("\\s*,\\s*"));
        entry.setTags(tagsList);
        entry.setDate(entryDate);
        EditText body = (EditText)findViewById(R.id.entry_body);
        entry.setBody(body.getText().toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveChanges();
    }
}
