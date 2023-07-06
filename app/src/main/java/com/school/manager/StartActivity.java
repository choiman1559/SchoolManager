package com.school.manager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.manager.auth.LoginActivity;
import com.school.manager.auth.SchoolAuthActivity;
import com.school.manager.post.db.Constants;

import static com.school.manager.Application.selfInfo;
import static com.school.manager.Application.prefs;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String UUID = prefs.getString(Constants.UUID, "");

        if (UUID.isEmpty() || FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            selfInfo.setUUID(UUID);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            if(prefs.getString("schoolApi", "").isEmpty()) {
                firestore.document("/defaultData/api").get()
                        .addOnCompleteListener(task -> prefs.edit().putString("schoolApi", (String) task.getResult().get("schoolApi")).apply());
            }

            DocumentReference reference = firestore.document("/userInfo/" + selfInfo.getUUID());

            reference.get().addOnCompleteListener(task -> {
                Intent intent;
                DocumentSnapshot snapshot = task.getResult();

                if (snapshot.exists()) {
                    selfInfo.setAdmin(Boolean.TRUE.equals(snapshot.getBoolean(Constants.isAdmin)));
                    selfInfo.setBanned(Boolean.TRUE.equals(snapshot.getBoolean(Constants.isBanned)));
                    selfInfo.setActualName(snapshot.getString(Constants.userName));
                    selfInfo.setUserName(snapshot.getString(Constants.userId));
                    selfInfo.setLoginType(snapshot.getString(Constants.loginWith));
                    selfInfo.setSchoolName(snapshot.getString(Constants.school));
                    selfInfo.setSchoolType(snapshot.getString(Constants.schoolType));
                    intent = new Intent(this, MainActivity.class);
                } else {
                    intent = new Intent(this, SchoolAuthActivity.class);
                }

                startActivity(intent);
                finish();
            }).addOnFailureListener(Throwable::printStackTrace);
        }
    }
}
