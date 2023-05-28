package com.school.manager.post.db;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.SetOptions;
import com.school.manager.Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        writer.setUUID(snapshot.getString(Constants.UUID));

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

    @SuppressWarnings("unchecked")
    public void postComment(Comment comment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.document(documentRef);

        reference.get().addOnCompleteListener(task -> {
            DocumentSnapshot snapshot = task.getResult();
            List<Map<String, Object>> maps = (List<Map<String, Object>>) snapshot.get(Constants.comment);
            if(maps == null) maps = new ArrayList<>();
            Map<String, Object> commentMap = new HashMap<>();
            maps.add(commentMap);

            commentMap.put(Constants.UUID, comment.getWriter().getUUID());
            commentMap.put(Constants.writerId, comment.getWriter().getUserName());
            commentMap.put(Constants.content, comment.getContent());
            commentMap.put(Constants.time, new Timestamp(comment.getTimestamp()));

            Map<String, Object> newMap = new HashMap<>();
            newMap.put(Constants.comment, maps);
            reference.set(newMap, SetOptions.merge());
        });
    }

    @SuppressWarnings("unchecked")
    public void deleteComment(int index) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.document(documentRef);

        reference.get().addOnCompleteListener(task -> {
            DocumentSnapshot snapshot = task.getResult();
            List<Map<String, Object>> maps = (List<Map<String, Object>>) snapshot.get(Constants.comment);
            if(maps != null && maps.size() > index) {
                maps.remove(index);
                comments.remove(index);
            }

            Map<String, Object> newMap = new HashMap<>();
            newMap.put(Constants.comment, maps);
            reference.set(newMap, SetOptions.merge());
        });
    }

    @SuppressWarnings("unchecked")
    public void postLike() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.document(documentRef);

        reference.get().addOnCompleteListener(task -> {
            DocumentSnapshot snapshot = task.getResult();
            List<String> list = (ArrayList<String>) snapshot.get(Constants.like);
            if(list == null) list = new ArrayList<>();
            list.add(Application.selfInfo.getUserName());

            Map<String, Object> newMap = new HashMap<>();
            newMap.put(Constants.like, list);
            reference.set(newMap, SetOptions.merge());
        });
    }

    @SuppressWarnings("unchecked")
    public void postDislike() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.document(documentRef);

        reference.get().addOnCompleteListener(task -> {
            DocumentSnapshot snapshot = task.getResult();
            List<String> list = (ArrayList<String>) snapshot.get(Constants.dislike);
            if(list == null) list = new ArrayList<>();
            list.add(Application.selfInfo.getUserName());

            Map<String, Object> newMap = new HashMap<>();
            newMap.put(Constants.dislike, list);
            reference.set(newMap, SetOptions.merge());
        });
    }

    public void postItself(String boardName, boolean isMerge) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference schoolType = db.collection(Application.selfInfo.getSchoolType());
        DocumentReference school = schoolType.document(Application.selfInfo.getSchoolName());
        CollectionReference board = school.collection(boardName);
        DocumentReference newDocRef = board.document();
        Map<String, Object> data = new HashMap<>();

        data.put(Constants.writerId, writer.getUserName());
        data.put(Constants.UUID, writer.getUUID());
        data.put(Constants.content, content);
        data.put(Constants.title, title);
        data.put(Constants.time, Timestamp.now());
        data.put(Constants.attachment, attachments);
        data.put(Constants.dislike, new ArrayList<>());
        data.put(Constants.like, new ArrayList<>());
        data.put(Constants.comment, new ArrayList<Map<String, Object>>());

        if(isMerge) newDocRef.set(data, SetOptions.merge());
        else newDocRef.set(data);
    }

    public Task<Void> deleteItself() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.document(documentRef);
        return reference.delete();
    }
}
