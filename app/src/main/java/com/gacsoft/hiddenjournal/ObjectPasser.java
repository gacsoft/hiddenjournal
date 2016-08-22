package com.gacsoft.hiddenjournal;

import android.content.Context;

/**
 * Holds various objects for passing between Activities
 */
public class ObjectPasser {
    private static Entry heldEntry = new Entry();
    private static Journal heldJournal;
    private static Context context;

    private ObjectPasser() {}

    /**
     * Sets reference to Entry
     */
    public static void putEntry(Entry entry) {
        heldEntry = entry;
    }

    /**
     * Gets the Entry being held
     */
    public static Entry getEntry() { return heldEntry; }

    public static void putJournal(Journal journal) {heldJournal = journal; }

    public static Journal getJournal() { return heldJournal; }

    public static void setContext(Context con) {context = con;}

    public static Context getContext() {return context;}
}
