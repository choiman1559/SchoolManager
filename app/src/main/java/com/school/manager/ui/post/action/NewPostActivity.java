package com.school.manager.ui.post.action;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.school.Application;
import com.school.manager.R;
import com.school.manager.ui.post.db.Post;

import java.util.ArrayList;

public class NewPostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Intent intent = getIntent();
        final String boardType = intent.getStringExtra("boardType");

        AppCompatEditText titleInput = findViewById(R.id.item_board_title);
        AppCompatEditText contentInput = findViewById(R.id.item_board_content);
        AppCompatImageButton sendButton = findViewById(R.id.item_board_post_send);

        sendButton.setOnClickListener((v) -> {
            Editable titleEdit = titleInput.getText();
            Editable contentEdit = contentInput.getText();

            if(titleEdit != null && contentEdit != null) {
                String title = titleEdit.toString();
                String content = contentEdit.toString();

                if(!title.isEmpty() && !content.isEmpty()) {
                    Post postObj = new Post();
                    postObj.setWriter(Application.selfInfo);
                    postObj.setContent(content);
                    postObj.setTitle(title);
                    postObj.setAttachments(new ArrayList<>());
                    postObj.postItself(boardType, true);
                    finish();
                }
            }
        });
    }
}
