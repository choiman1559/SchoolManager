package com.school;

import android.content.Context;
import android.content.SharedPreferences;

import com.school.manager.ui.post.db.UserInfo;

public class Application extends android.app.Application {
    public static UserInfo selfInfo;
    public static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);

        //TODO Fetch school & user data from Firestore
        selfInfo = new UserInfo();
        selfInfo.setAdmin(true);
        selfInfo.setBanned(false);
        selfInfo.setClassId("31122");
        selfInfo.setActualName("최유준");
        selfInfo.setUserName("Admin");
        selfInfo.setUUID("STUB");
        selfInfo.setLoginType("google");
        selfInfo.setSchoolName("부흥고등학교_B000012506");
        selfInfo.setSchoolType("고등학교");
    }
}
