package com.gx.mydairy.dairy.Util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.MessageInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GX on 2016/10/10.
 */

public class UtilToos {

    /**
     * ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * 8 位 UCS 转换格式
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 中文超大字符集
     */
    public static final String GBK = "GBK";

    /**
     * 将字符编码转换成ISO-8859-1码
     */
    public static String toISO_8859_1(String str) {
        return changeCharset(str, ISO_8859_1);
    }

    /**
     * 将字符编码转换成UTF-8码
     */
    public static String toUTF_8(String str) {
        return changeCharset(str, UTF_8);
    }

    /**
     * 将字符编码转换成GBK码
     */
    public static String toGBK(String str) {
        return changeCharset(str, GBK);
    }

    private final String SMS_URI_INBOX = "content://sms/inbox";// 收信箱

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换编码的字符串
     * @param newCharset 目标编码
     * @return
     * @thr
     */
    public static String changeCharset(String str, String newCharset) {
        if (str != null) {
            // 用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            // 用新的字符编码生成字符串
            try {
                return new String(bs, newCharset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 验证手机号
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 获取当前时间
     */
    public static String getTimeToString(int type) {


        if (type == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String ly_time1 = sdf.format(new java.util.Date());
            //System.out.println("现在时间是:"+ly_time1);

            //结果：现在时间是:2008-11-28 14:19:49
            return ly_time1;
        }
        if (type == 2) {
            String ly_time2 = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            ly_time2 = ly_time2.replaceAll("-", "/");
            //System.out.println("现在时间是:" + ly_time2);

            //结果：现在时间是:2008-11-28 14:19:49
            return ly_time2;
        }
        if (type == 3) {
            Date now = new Date();
            DateFormat d = DateFormat.getDateInstance();
            String ly_time3 = d.format(now);
            //System.out.println("Today is " + ly_time3);

            //时间2008年04月14日
            return ly_time3;
        }
        if (type == 4) {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        return "";
    }

    /**
     * 获取当前时间
     */
    public static String getTimeID() {
        return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    }

    /**
     * @return
     * @author sichard
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     */
    public static final boolean ping() {
        String result = null;
        try {
            String ip = "119.75.217.109";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址1次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            }
        } catch (Exception e) {
            result = e.getMessage();
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }


    // --------------------------------收到的短息信息----------------------------------
    public List<MessageInfo> getSmsInfos(Context context) {

        MessageInfo messageInfo = null;
        List<MessageInfo> list = new ArrayList<>();

        try {
            ContentResolver cr = context.getContentResolver();
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Uri uri = Uri.parse(SMS_URI_INBOX);
            Cursor cursor = cr.query(uri, projection, null, null, "date desc");
            while (cursor.moveToNext()) {
                messageInfo = new MessageInfo();
                // -----------------------信息----------------
                int nameColumn = cursor.getColumnIndex("person");// 联系人姓名列表序号
                int phoneNumberColumn = cursor.getColumnIndex("address");// 手机号
                int smsbodyColumn = cursor.getColumnIndex("body");// 短信内容
                int dateColumn = cursor.getColumnIndex("date");// 日期
                int typeColumn = cursor.getColumnIndex("type");// 收发类型 1表示接受 2表示发送
                String nameId = cursor.getString(nameColumn);
                String phoneNumber = cursor.getString(phoneNumberColumn);
                String smsbody = cursor.getString(smsbodyColumn);
                Date d = new Date(Long.parseLong(cursor.getString(dateColumn)));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd " + "\n" + "hh:mm:ss");
                String date = dateFormat.format(d);

                // --------------------------匹配联系人名字--------------------------

                Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);
                Cursor localCursor = cr.query(personUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_ID, ContactsContract.PhoneLookup._ID}, null, null, null);

                System.out.println(localCursor.getCount());
                System.out.println("之前----" + localCursor);
                if (localCursor.getCount() != 0) {
                    localCursor.moveToFirst();
                    System.out.println("之后----" + localCursor);
                    String name = localCursor.getString(localCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    long photoid = localCursor.getLong(localCursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_ID));
                    long contactid = localCursor.getLong(localCursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    messageInfo.setName(name);
                    // 如果photoid 大于0 表示联系人有头像 ，如果没有给此人设置头像则给他一个默认的
                    if (photoid > 0) {
                        Uri uri1 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri1);
                        messageInfo.setContactPhoto(BitmapFactory.decodeStream(input));
                    } else {
                        messageInfo.setContactPhoto(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                    }
                } else {
                    messageInfo.setName(phoneNumber);
                    messageInfo.setContactPhoto(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                }

                localCursor.close();
                //Log.v("手机短信", messageInfo.toString());
                messageInfo.setSmsContent(smsbody);
                messageInfo.setSmsDate(date);
                list.add(messageInfo);
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<HashMap<String, String>> readContact(Context context) {
        Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        // 从raw_contacts中读取联系人的id("contact_id")
        Cursor rawContactsCursor = context.getContentResolver().query(rawContactsUri,
                new String[]{"contact_id"}, null, null, null);
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);
                try {
                    // System.out.println(contactId);
                    // 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
                    Cursor dataCursor = context.getContentResolver().query(dataUri,
                            new String[]{"data1", "mimetype"}, "contact_id=?",
                            new String[]{contactId}, null);
                    if (dataCursor != null) {
                        HashMap<String, String> map = new HashMap<>();
                        while (dataCursor.moveToNext()) {
                            String data1 = dataCursor.getString(0);
                            String mimetype = dataCursor.getString(1);
                            if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                                map.put("phone", data1);
                            } else if ("vnd.android.cursor.item/name"
                                    .equals(mimetype)) {
                                map.put("name", data1);
                            }
                        }
                        //Log.v("手机联系人", map.toString());
                        list.add(map);
                        dataCursor.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            rawContactsCursor.close();
        }
        return list;
    }

}
