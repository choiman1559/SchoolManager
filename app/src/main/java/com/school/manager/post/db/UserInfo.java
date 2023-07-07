package com.school.manager.post.db;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserInfo implements Serializable {

    public interface OnInformationFetchCompleteListener {
        void onInformationFetchComplete(UserInfo result);
    }

    private OnInformationFetchCompleteListener listener;

    private String UUID;

    @PropertyName(Constants.userName)
    private String userName;
    @PropertyName(Constants.profileIcon)
    private String profileIconPath;
    @PropertyName(Constants.school)
    private String schoolName;
    @PropertyName(Constants.schoolType)
    private String schoolType;
    @PropertyName(Constants.userName)
    private String actualName;
    @PropertyName(Constants.loginWith)
    private String loginType;
    @PropertyName(Constants.isBanned)
    private boolean isBanned;
    @PropertyName(Constants.isAdmin)
    private boolean isAdmin;

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public void setProfileIconPath(String profileIconPath) {
        this.profileIconPath = profileIconPath;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setListener(OnInformationFetchCompleteListener listener) {
        this.listener = listener;
    }

    public String getActualName() {
        return actualName;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileIconPath() {
        return profileIconPath;
    }

    public String getUUID() {
        return UUID;
    }

    public String getLoginType() {
        return loginType;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void getCompleteInfo() {
        if(schoolName == null && schoolType == null) {
            FirebaseFirestore.getInstance().document("/userInfo/" + UUID).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    isAdmin = Boolean.TRUE.equals(snapshot.getBoolean(Constants.isAdmin));
                    isBanned = Boolean.TRUE.equals(snapshot.getBoolean(Constants.isBanned));
                    loginType = snapshot.getString(Constants.loginWith);
                    actualName = snapshot.getString(Constants.name);
                    profileIconPath = snapshot.getString(Constants.profileIcon);
                    schoolName = snapshot.getString(Constants.school);
                    schoolType = snapshot.getString(Constants.schoolType);

                    if(listener != null) {
                        listener.onInformationFetchComplete(this);
                    }
                }
            });
        } else listener.onInformationFetchComplete(this);
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.userId, getUserName());
        map.put(Constants.isAdmin, isAdmin());
        map.put(Constants.isBanned, isBanned());
        map.put(Constants.loginWith, getLoginType());
        map.put(Constants.name, getActualName());
        map.put(Constants.profileIcon, getProfileIconPath());
        map.put(Constants.school, getSchoolName());
        map.put(Constants.schoolType, getSchoolType());

        return map;
    }
}
