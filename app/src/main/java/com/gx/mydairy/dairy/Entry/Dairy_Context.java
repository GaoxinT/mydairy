package com.gx.mydairy.dairy.Entry;

/**
 * Created by GX on 2016/11/17.
 */

public class Dairy_Context {


    private String _id;
    private String mystate;
    private String date;
    private String title;
    private String context;

    public Dairy_Context(String _id, String mystate, String date, String title, String context) {
        this._id = _id;
        this.mystate = mystate;
        this.date = date;
        this.title = title;
        this.context = context;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMystate() {
        return mystate;
    }

    public void setMystate(String mystate) {
        this.mystate = mystate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Dairy_Context{" +
                "_id='" + _id + '\'' +
                ", mystate='" + mystate + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
