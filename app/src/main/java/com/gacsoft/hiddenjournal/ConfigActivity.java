package com.gacsoft.hiddenjournal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConfigActivity extends AppCompatActivity {
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setTitle(R.string.options);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                id = position + 1; // ListView position starts from 0, backup id starts from 1
                itemClicked();
            }
        });
    }

    public void onResume() {
        super.onResume();

        ListView listView = (ListView)findViewById(R.id.list_view);
        ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.entry_list_view, BackupManager.getBackupList());
        listView.setAdapter(listAdapter);
    }

    private void itemClicked() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.loadBackup)
                .setMessage(R.string.confirmBackup)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        loadBackup();
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void loadBackup() {
        try {
            System.out.println(id);
            BackupManager.loadBackup(id);
            Toast.makeText(ConfigActivity.this, R.string.doneBackup, Toast.LENGTH_SHORT).show();
        }
        catch (java.io.IOException e) {
            Toast.makeText(ConfigActivity.this, R.string.failedBackup, Toast.LENGTH_SHORT).show();
        }
    }
}
