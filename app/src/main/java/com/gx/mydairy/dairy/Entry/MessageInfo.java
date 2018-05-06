package com.gx.mydairy.dairy.Entry;

import android.graphics.Bitmap;

/**
 * Created by GX on 2017/1/18.
 */

public class MessageInfo {
    private String name;
    private Bitmap contactPhoto;
    private String smsContent;
    private String smsDate;

    public Bitmap getContactPhoto() {
        return contactPhoto;
    }

    public void setContactPhoto(Bitmap contactPhoto) {
        this.contactPhoto = contactPhoto;
    }

    public String getSmsDate() {
        return smsDate;
    }

    public void setSmsDate(String smsDate) {
        this.smsDate = smsDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "name='" + name + '\'' +
                ", smsContent='" + smsContent + '\'' +
                ", smsDate='" + smsDate + '\'' +
                '}';
    }
}
