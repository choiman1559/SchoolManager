package com.school.manager.post.db;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class School implements Serializable {
    @PropertyName(Constants.name)
    private String name;

    @PropertyName(Constants.location)
    private String location;

    @PropertyName(Constants.uniqueId)
    private String uniqueId;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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
}
