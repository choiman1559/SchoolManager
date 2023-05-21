package com.school.manager.ui.post.db;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String UUID;
    @PropertyName(Constants.classId)
    private String classId;
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

    public void setClassId(String classId) {
        this.classId = classId;
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

    public String getClassId() {
        return classId;
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
}
