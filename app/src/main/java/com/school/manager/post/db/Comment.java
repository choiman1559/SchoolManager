package com.school.manager.post.db;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Comment implements Serializable {
    private UserInfo writer;
    private Date timestamp;
    private String content;

    public Date getTimestamp() {
        return timestamp;
    }

    public UserInfo getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setWriter(UserInfo writer) {
        this.writer = writer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Comment parseFrom(Map<String, Object> map) {
        Comment comment = new Comment();
        UserInfo writer = new UserInfo();
        writer.setUserName((String) map.get(Constants.writerId));
        writer.setUUID((String) map.get(Constants.UUID));

        comment.writer = writer;
        comment.timestamp = ((Timestamp) Objects.requireNonNull(map.get(Constants.time))).toDate();
        comment.setContent((String) map.get(Constants.content));

        return comment;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put(Constants.UUID, getWriter().getUUID());
        commentMap.put(Constants.writerId, getWriter().getUserName());
        commentMap.put(Constants.content, getContent());
        commentMap.put(Constants.time, new Timestamp(getTimestamp()));

        return commentMap;
    }
}