package com.school.manager.post.db;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class School implements Serializable {
    @PropertyName(Constants.name)
    private String name;

    @PropertyName(Constants.location)
    private String location;

    @PropertyName(Constants.uniqueId)
    private String uniqueId;

    @PropertyName(Constants.schoolType)
    private String schoolType;

    @PropertyName(Constants.boardList)
    private List<String> boardList;

    @PropertyName(Constants.adminBoard)
    private String adminBoard;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public void setBoardList(List<String> boardList) {
        this.boardList = boardList;
    }

    public void setAdminBoard(String adminBoard) {
        this.adminBoard = adminBoard;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public List<String> getBoardList() {
        return boardList;
    }

    public String getAdminBoard() {
        return adminBoard;
    }
}
