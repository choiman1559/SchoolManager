package com.school.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.manager.post.db.Constants;
import com.school.manager.post.db.UserInfo;

public class Application extends android.app.Application {
    public static UserInfo selfInfo;
    public static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
        selfInfo = new UserInfo();
    }
}

/*
TODO From now on (0.1.3 ~ )
1. Add post/comment delete method (O)
2. Login with other account/user (O)
3. Show profile icon for user
4. Implement comment-on-comment
 */