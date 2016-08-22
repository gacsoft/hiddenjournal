package com.gacsoft.hiddenjournal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Gacsoft on 8/8/2016.
 */
public class Entry {
    private String title = "";
    private Date date;
    private List<String> tags;
    private String password = "";
    private String body = "";

    public Entry() {
        tags = new ArrayList<String>();
        date = Calendar.getInstance().getTime();
    }

    public Entry(String title, Date date, List<String> tags, String password, String body) {
        this.title = title;
        this.date = date;
        this.tags = tags;
        this.password = password;
        this.body = body;
    }

    /**
     * Returns title if exists, otherwise body
     * @return title if exists, otherwise body
     */
    public String toString() {
        if (!title.equals("")) return title;
        else return body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
