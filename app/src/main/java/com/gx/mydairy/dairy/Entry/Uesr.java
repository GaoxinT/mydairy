package com.gx.mydairy.dairy.Entry;

/**
 * Created by GX on 2017/1/17.
 */

public class Uesr {

    private static String userId;
    private static String userName;
    private static String userPhone;
    private static String userPwd;
    private static String userState;
    private static String userLastLogin;

    private Uesr(){}
    private static Uesr uesr;

    public static synchronized Uesr getInstance() {
        if (uesr == null) {
            return new Uesr();
        } else {
            return uesr;
        }

    }


    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserLastLogin() {
        return userLastLogin;
    }

    public void setUserLastLogin(String userLastLogin) {
        this.userLastLogin = userLastLogin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public void setNull() {
        userId = null;
        userName = null;
        userPhone = null;
        userPwd = null;
        userState = null;
        userLastLogin = null;
    }

}
