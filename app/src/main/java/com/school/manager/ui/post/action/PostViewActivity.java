package com.school.manager.ui.post.action;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.school.manager.ui.post.db.Post;

public class PostViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Post post = (Post) getIntent().getSerializableExtra("post");


    }
}
