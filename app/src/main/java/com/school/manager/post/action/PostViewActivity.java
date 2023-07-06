package com.school.manager.post.action;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.manager.Application;
import com.school.manager.R;
import com.school.manager.post.db.Comment;
import com.school.manager.post.db.Post;
import com.school.manager.util.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostViewActivity extends AppCompatActivity {

    static SimpleDateFormat dateFormat;
    static Post post;
    CommentListAdapter mAdapter;

    RecyclerView commentList;
    ProgressBar progressBar;
    ImageView profileImage;
    TextView writerText;
    TextView timeText;
    TextView titleText;
    TextView contentText;

    LinearLayoutCompat likeButton;
    LinearLayoutCompat dislikeButton;
    TextView likeCount;
    TextView dislikeCount;
    TextView commentCount;
    AppCompatImageView likeImageView;
    AppCompatImageView dislikeImageView;

    AppCompatEditText inputComment;
    AppCompatImageButton sendButton;
    AppCompatImageButton refreshButton;
    AppCompatImageButton deleteButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        post = (Post) getIntent().getSerializableExtra("post");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        progressBar = findViewById(R.id.progressBar);

        profileImage = findViewById(R.id.item_board_profile);
        writerText = findViewById(R.id.item_board_writer);
        timeText = findViewById(R.id.item_board_time);
        titleText = findViewById(R.id.item_board_title);
        contentText = findViewById(R.id.item_board_content);

        likeButton = findViewById(R.id.item_board_like_button);
        dislikeButton = findViewById(R.id.item_board_dislike_button);
        likeCount = findViewById(R.id.item_board_like_count);
        dislikeCount = findViewById(R.id.item_board_dislike_count);
        commentCount = findViewById(R.id.item_board_comment_count);
        likeImageView = findViewById(R.id.item_board_like_image);
        dislikeImageView = findViewById(R.id.item_board_dislike_image);

        inputComment = findViewById(R.id.item_board_comment_input);
        sendButton = findViewById(R.id.item_board_comment_send);
        refreshButton = findViewById(R.id.item_board_comment_refresh);
        deleteButton = findViewById(R.id.item_board_delete_post);
        commentList = findViewById(R.id.item_board_comment_list);

        refreshButton.setOnClickListener((v) -> {
            setProgress(true);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference reference = db.document(post.getDocumentRef());

            reference.get().addOnCompleteListener(task -> {
                post = Post.parseFrom(task.getResult());
                init(false);
            });
        });

        deleteButton.setVisibility(Application.selfInfo.isAdmin() || Application.selfInfo.getUUID().equals(post.getWriter().getUUID()) ? View.VISIBLE : View.GONE);
        deleteButton.setOnClickListener((v) -> {
            setProgress(true);
            post.deleteItself().addOnCompleteListener(task -> {
               if(task.isSuccessful()) {
                   finish();
               } else {
                   setProgress(false);
               }
            });
        });

        likeButton.setOnClickListener((v) -> {
            if (likeButton.isFocusable()) {
                post.postLike();
                likeImageView.setImageResource(com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_thumb_like_24_filled);
                likeButton.setFocusable(false);
            }
        });

        dislikeButton.setOnClickListener((v) -> {
            if (dislikeButton.isFocusable()) {
                post.postDislike();
                dislikeImageView.setImageResource(com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_thumb_dislike_24_filled);
                dislikeButton.setFocusable(false);
            }
        });

        sendButton.setOnClickListener((v) -> {
            if(Application.selfInfo.isBanned()) {
                ToastHelper.show(this, "관리자에 의해 활동이 차단됨", "확인",ToastHelper.LENGTH_SHORT);
                return;
            }

            Editable editableText = inputComment.getText();
            if (editableText != null) {
                String comment = editableText.toString();
                if(!comment.isEmpty()) {
                    Comment commentObj = new Comment();

                    commentObj.setWriter(Application.selfInfo);
                    commentObj.setContent(comment);
                    commentObj.setTimestamp(new Date(System.currentTimeMillis()));

                    post.postComment(commentObj);
                    inputComment.setText("");
                    post.getComments().add(commentObj);
                    mAdapter.setItem(post.getComments());
                    mAdapter.notifyItemInserted(post.getComments().size() - 1);
                }
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> this.finish());
        init(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init(boolean isFirstRun) {
        setProgress(false);

        writerText.setText(post.getWriter().getUserName());
        timeText.setText(dateFormat.format(post.getTimestamp()));
        titleText.setText(post.getTitle());
        contentText.setText(Html.fromHtml(post.getContent().replace("\\n", "<br>")).toString());

        likeCount.setText(String.format(Locale.getDefault(), "%d", post.getLike().size()));
        dislikeCount.setText(String.format(Locale.getDefault(), "%d", post.getDislike().size()));
        commentCount.setText(String.format(Locale.getDefault(), "%d", post.getComments().size()));

        if (post.getLike().contains(Application.selfInfo.getUserName())) {
            likeImageView.setImageResource(com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_thumb_like_24_filled);
            likeButton.setFocusable(false);
        }

        if (post.getDislike().contains(Application.selfInfo.getUserName())) {
            dislikeImageView.setImageResource(com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_thumb_dislike_24_filled);
            dislikeButton.setFocusable(false);
        }

        if(isFirstRun) {
            mAdapter = new CommentListAdapter(post.getComments());
            commentList.setAdapter(mAdapter);
            commentList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mAdapter.setItem(post.getComments());
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setProgress(boolean isProgress) {
        this.runOnUiThread(() -> progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE));
    }

    static class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {

        private List<Comment> comments;

        public CommentListAdapter(List<Comment> comments) {
            this.comments = comments;
        }

        public void setItem(List<Comment> comments) {
            this.comments = comments;
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_comment, parent, false));
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.timeText.setText(dateFormat.format(comment.getTimestamp()));
            holder.contentText.setText(comment.getContent());
            holder.writerText.setText(comment.getWriter().getUserName());
            holder.deleteButton.setVisibility(Application.selfInfo.isAdmin() || Application.selfInfo.getUUID().equals(comment.getWriter().getUUID()) ? View.VISIBLE : View.GONE);
            holder.deleteButton.setOnClickListener((v) -> {
                post.deleteComment(comment);
                notifyItemRemoved(position);
            });
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        static class CommentViewHolder extends RecyclerView.ViewHolder {

            AppCompatImageView profileImage;
            AppCompatImageButton deleteButton;
            TextView writerText;
            TextView contentText;
            TextView timeText;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                profileImage = itemView.findViewById(R.id.item_board_profile);
                deleteButton = itemView.findViewById(R.id.item_board_delete_comment);
                writerText = itemView.findViewById(R.id.item_board_writer);
                contentText = itemView.findViewById(R.id.item_board_content);
                timeText = itemView.findViewById(R.id.item_board_time);
            }
        }
    }
}
