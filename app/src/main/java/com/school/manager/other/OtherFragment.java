package com.school.manager.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.school.manager.Application;
import com.school.manager.R;
import com.school.manager.auth.LoginActivity;
import com.school.manager.post.db.Constants;

import java.util.Locale;
import java.util.Objects;

public class OtherFragment extends Fragment {

    Activity mContext;
    AppCompatImageButton profileIcon;

    ActivityResultLauncher<CropImageContractOptions> cropImageRequest = registerForActivityResult(new CropImageContract(), o -> {
        if(o.isSuccessful()) {
            Uri uri = o.getOriginalUri();
            if(uri != null) {
                String[] argsForExt = Objects.requireNonNull(o.getUriFilePath(mContext, false)).split("\\.");
                String location = "/userInfo/profileIcon/" + Application.selfInfo.getUUID() + "." + argsForExt[argsForExt.length - 1];

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://highschoolmanager-3bf92.appspot.com/");
                storage.getReference(location).putFile(uri).addOnCompleteListener(task -> {
                    profileIcon.setImageURI(uri);
                    Application.selfInfo.setProfileIconPath(location);
                    FirebaseFirestore.getInstance().document("/userInfo/" + Application.selfInfo.getUUID()).set(Application.selfInfo.getMap());
                });
            }
        }
    });

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    CropImageOptions options = new CropImageOptions();
                    options.guidelines = CropImageView.Guidelines.ON;
                    options.cropMenuCropButtonTitle = "자르기";

                    cropImageRequest.launch(new CropImageContractOptions(uri, options));
                }
            });

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
        profileIcon = view.findViewById(R.id.item_board_profile);

        writerText.setText(Application.selfInfo.getUserName());
        schoolText.setText(String.format(Locale.getDefault(), "%s", Application.selfInfo.getSchoolName().split("_")[0]));
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Application.prefs.edit().remove(Constants.UUID).apply();
            startActivity(new Intent(mContext, LoginActivity.class));
            mContext.finish();
        });

        profileIcon.setOnClickListener(v -> {
            ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(mediaType)
                    .build());
        });

        if(Application.selfInfo.getProfileIconPath() != null && !Application.selfInfo.getProfileIconPath().isBlank()) {
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://highschoolmanager-3bf92.appspot.com/");
            Glide.with((Context) mContext).load(storage.getReference(Application.selfInfo.getProfileIconPath())).into(profileIcon);
        }

        menusWebView.loadUrl("file:///android_asset/board.html");
    }
}
