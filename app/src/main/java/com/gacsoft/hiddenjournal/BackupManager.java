package com.gacsoft.hiddenjournal;

import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gacsoft on 8/12/2016.
 */
public class BackupManager {
    static final private int MAX_BACKUPS = 10;

    public static void backupIfNeeded( ) {
        File journal = new File(ObjectPasser.getContext().getFilesDir(), "journal.xml");
        if (!journal.exists()) //if we don't have anything to backup, return
            return;

        File firstBackup = new File(ObjectPasser.getContext().getFilesDir(), "journal.1");
        if (!firstBackup.exists()) { //if we don't have a backup yet, create one
            try {
                copy("journal.xml", "journal.1");
                return;
            } catch (java.io.IOException e) {e.printStackTrace();} //TODO do something here
        }

        if (DateHelper.isSameDay(new Date(firstBackup.lastModified()), Calendar.getInstance().getTime()))
            return; //if we already made a backup today, return
        for (int i = MAX_BACKUPS; i > 1; i--) //shift all daily backups by one
        {
            File from = new File(ObjectPasser.getContext().getFilesDir(), "journal." + Integer.toString(i - 1));
            if (!from.exists()) continue;
            File to = new File(ObjectPasser.getContext().getFilesDir(), "journal." + Integer.toString(i));
            long created = from.lastModified();
            from.renameTo(to);
            to.setLastModified(created); //carry over the date of the backup
        }
        try {
            copy("journal.xml", "journal.1");
        } catch (java.io.IOException e) {e.printStackTrace();} //TODO do something here
    }

    public static List<String> getBackupList() {
        List<String> dates = new ArrayList<String>();
        for (int i = 1; i < MAX_BACKUPS; i++) {
            File backup = new File(ObjectPasser.getContext().getFilesDir(), "journal." + Integer.toString(i));
            if (backup.exists()) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                dates.add(df.format(backup.lastModified()));
            }
        }
        return dates;
    }

    public static void loadBackup(int id) throws java.io.FileNotFoundException, java.io.IOException {
        copy("journal." + Integer.toString(id), "journal.xml");
        ObjectPasser.getJournal().load(new FileInputStream(new File(ObjectPasser.getContext().getFilesDir(), "journal.xml")));
    }

    private static void copy(String from, String to) throws java.io.FileNotFoundException, java.io.IOException {
        File dir = ObjectPasser.getContext().getFilesDir();
        InputStream in = new FileInputStream(new File (dir, from));
        OutputStream out = new FileOutputStream(new File (dir, to));

        byte[] buf = new byte[1024];
        int len;
        try{
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        finally {
            in.close();
            out.close();
        }
    }

    private static void rename(String from, String to) {
        File dir = ObjectPasser.getContext().getFilesDir();
        if(dir.exists()){
            File fromFile = new File(dir,from);
            File toFile = new File(dir,to);
            if(fromFile.exists())
                fromFile.renameTo(toFile);
        }
    }
}
