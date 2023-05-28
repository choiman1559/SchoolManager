package com.school.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.school.manager.post.db.UserInfo;

public class Application extends android.app.Application {
    public static UserInfo selfInfo;
    public static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);

        //TODO: Fetch school & user data from Firestore
        selfInfo = new UserInfo();
        selfInfo.setAdmin(true);
        selfInfo.setBanned(false);
        selfInfo.setUUID("{UUID}");
        selfInfo.setActualName("최유준");
        selfInfo.setUserName("Admin");
        selfInfo.setLoginType("google");
        selfInfo.setSchoolName("부흥고등학교_B000012506");
        selfInfo.setSchoolType("고등학교");
    }
}

/*
TODO From now on (0.1.3 ~ )
1. Add post/comment delete method (O)
2. Login with other account/user
3. Show profile icon for user
4. Implement comment-on-comment
 */