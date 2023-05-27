package com.school.manager.ui.post;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.school.manager.R;
import com.school.manager.ui.post.action.PostViewActivity;
import com.school.manager.ui.post.db.Post;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> data;
    private final Context mContext;

    public PostAdapter(Context context, List<Post> data) {
        this.mContext = context;
        this.data = data;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post data = this.data.get(position);
        holder.title.setText(data.getTitle());
        holder.contents.setText(data.getContent().replace("\\n", " "));
        holder.writer.setText(data.getWriter().getUserName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        holder.timestamp.setText(dateFormat.format(data.getTimestamp()));
        holder.likeCount.setText(String.format(Locale.getDefault(), "%d", data.getLike().size() - data.getDislike().size()));
        holder.commentCount.setText(String.format(Locale.getDefault(), "%d", data.getComments().size()));
        holder.parent.setOnClickListener((v) -> mContext.startActivity(new Intent(mContext, PostViewActivity.class).putExtra("post", data)));
    }

    public void setItem(List<Post> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView parent;
        private final TextView title;
        private final TextView contents;
        private final TextView writer;
        private final TextView timestamp;
        private final TextView likeCount;
        private final TextView commentCount;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.item_board);
            title = itemView.findViewById(R.id.item_board_title);
            contents = itemView.findViewById(R.id.item_board_content);
            writer = itemView.findViewById(R.id.item_board_writer);
            timestamp = itemView.findViewById(R.id.item_board_time);
            likeCount = itemView.findViewById(R.id.item_board_like_count);
            commentCount = itemView.findViewById(R.id.item_board_comment_count);
        }
    }
}