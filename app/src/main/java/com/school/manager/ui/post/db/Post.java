package com.school.manager.ui.post.db;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Post implements Serializable {
    private UserInfo writer;

    private List<Comment> comments;
    @PropertyName(Constants.attachment)
    private List<String> attachments;
    @PropertyName(Constants.dislike)
    private List<String> dislike;
    @PropertyName(Constants.like)
    private List<String> like;

    @PropertyName(Constants.content)
    private String content;
    @PropertyName(Constants.title)
    private String title;
    private String documentRef;
    @PropertyName(Constants.time)
    private Date timestamp;

    public void setWriter(UserInfo writer) {
        this.writer = writer;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDislike(List<String> dislike) {
        this.dislike = dislike;
    }

    public void setDocumentRef(String documentRef) {
        this.documentRef = documentRef;
    }

    public void setLike(List<String> like) {
        this.like = like;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public List<String> getDislike() {
        return dislike;
    }

    public List<String> getLike() {
        return like;
    }

    public String getDocumentRef() {
        return documentRef;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public UserInfo getWriter() {
        return writer;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @SuppressWarnings("unchecked")
    public static Post parseFrom(DocumentSnapshot snapshot) {
        Post post = new Post();
        UserInfo writer = new UserInfo();
        writer.setUserName(snapshot.getString(Constants.writerId));
        writer.setClassId(snapshot.getString(Constants.writerClass));

        post.writer = writer;
        post.content = snapshot.getString(Constants.content);
        post.title = snapshot.getString(Constants.title);
        post.timestamp = Objects.requireNonNull(snapshot.getTimestamp(Constants.time)).toDate();
        post.documentRef = snapshot.getReference().getPath();
        post.attachments = (List<String>) snapshot.get(Constants.attachment);
        post.like = (List<String>) snapshot.get(Constants.like);
        post.dislike = (List<String>) snapshot.get(Constants.dislike);
        post.comments = new ArrayList<>();

        List<Map<String, Object>> maps = (List<Map<String, Object>>) snapshot.get(Constants.comment);
        if(maps != null && maps.size() > 0) {
            for(Map<String, Object> map : maps) {
                Comment comment = Comment.parseFrom(map);
                post.comments.add(comment);
            }
        }

        return post;
    }
}
