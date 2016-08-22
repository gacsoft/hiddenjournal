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

    private static void OLDbackupIfNeeded( ) {
        File journal = new File(ObjectPasser.getContext().getFilesDir(), "journal.xml");
        if (!journal.exists())
            return;

        List<Date> backups = loadManifest();
        if (backups.size() == 0) {
            try {
                copy("journal.xml", "journal.1");
            } catch (java.io.IOException e) {}
        }
        else {
            if (DateHelper.isSameDay(Calendar.getInstance().getTime(), backups.get(0)))
                return; //if we already made a backup today

            if (backups.size() == MAX_BACKUPS) {
                backups.remove(backups.size() - 1);
            }
            for (int i = backups.size() + 1; i > 1; i--) {
                rename("journal" + Integer.toString(i - 1), "journal" + Integer.toString(i));
            }
            try {
            copy("journal.xml", "journal.1");
            } catch (java.io.IOException e) {}
        }
        backups.add(0, Calendar.getInstance().getTime());
        saveManifest(backups);
    }

    public static List<String> getBackupList() {

        return new ArrayList<>();
    }

    public static void loadBackup(int id) {

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

    private static List<Date> loadManifest() {
        List<Date> backups = new ArrayList<Date>();
        File backupManifest = new File(ObjectPasser.getContext().getFilesDir(), "backups.dat");
        if (backupManifest.exists()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                FileInputStream fis = new FileInputStream(backupManifest);
                DataInputStream dis = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(dis));
                String line = "";
                while ((line = br.readLine()) != null) {
                    backups.add(format.parse(line));
                }
            }
            catch (Exception e) {}
        }
        return backups;
    }

    private static void saveManifest(List<Date> backups) {
        File backupManifest = new File(ObjectPasser.getContext().getFilesDir(), "backups.dat");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            FileWriter fw = new FileWriter(backupManifest, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            for (Date item : backups) {
                out.println(format.format(item));
            }
            out.close();
        }
        catch (Exception e) {}

    }
}
