package com.school.manager.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.school.manager.Application;
import com.school.manager.R;
import com.school.manager.auth.LoginActivity;
import com.school.manager.post.db.Constants;

import java.util.Locale;

public class OtherFragment extends Fragment {

    Activity mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) mContext = (Activity) context;
        else throw new RuntimeException("Can't get Activity instanceof Context!");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView writerText = view.findViewById(R.id.item_board_writer);
        TextView schoolText = view.findViewById(R.id.item_board_school);
        MaterialButton logoutButton = view.findViewById(R.id.item_logout);
        WebView menusWebView = view.findViewById(R.id.item_detail_web_view);

        writerText.setText(Application.selfInfo.getUserName());
        schoolText.setText(String.format(Locale.getDefault(), "%s", Application.selfInfo.getSchoolName().split("_")[0]));
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Application.prefs.edit().remove(Constants.UUID).apply();
            startActivity(new Intent(mContext, LoginActivity.class));
            mContext.finish();
        });

        menusWebView.loadUrl("file:///android_asset/board.html");
    }
}
