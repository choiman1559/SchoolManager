package com.school.manager.post;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.school.manager.Application;
import com.school.manager.R;
import com.school.manager.post.action.NewPostActivity;
import com.school.manager.post.db.Constants;
import com.school.manager.post.db.Post;
import com.school.manager.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostFragment extends Fragment {

    Activity mContext;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    PostAdapter mPostAdapter;
    String[] adminBoardList;
    List<String> boards = new ArrayList<>();
    public static final String Default_Board = "자유게시판";
    private static String lastSelectedBoard = Default_Board;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) mContext = (Activity) context;
        else throw new RuntimeException("Can't get Activity instanceof Context!");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = view.findViewById(R.id.postRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        AppCompatSpinner boardSelectSpinner = view.findViewById(R.id.boardSelectSpinner);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference schoolType = db.collection(Application.selfInfo.getSchoolType());
        DocumentReference school = schoolType.document(Application.selfInfo.getSchoolName());

        AppCompatImageButton addNewPostButton = view.findViewById(R.id.addNewPostButton);
        AppCompatImageButton refreshButton = view.findViewById(R.id.refreshButton);

        addNewPostButton.setOnClickListener((v) -> {
            if(Application.selfInfo.isBanned()) {
                ToastHelper.show(mContext, "관리자에 의해 활동이 차단됨", "확인",ToastHelper.LENGTH_SHORT);
                return;
            }

            if(!Application.selfInfo.isAdmin()) {
                int selected = boards.indexOf(lastSelectedBoard);
                for (String s : adminBoardList) {
                    if (Objects.equals(s, selected + "")) {
                        ToastHelper.show(mContext, "관리자 전용 게시판 입니다!", "확인",ToastHelper.LENGTH_SHORT);
                        return;
                    }
                }
            }

           Intent intent = new Intent(mContext, NewPostActivity.class);
           intent.putExtra("boardType", lastSelectedBoard);
           mContext.startActivity(intent);
        });

        refreshButton.setOnClickListener((v) -> {
            setProgress(true);
            getPosts(school, lastSelectedBoard, false);
        });

        setProgress(true);
        school.get().addOnCompleteListener(boardTask -> {
            DocumentSnapshot snapshot = boardTask.getResult();
            this.adminBoardList = ((String) Objects.requireNonNull(snapshot.get(Constants.adminBoard))).split(",");
            this.boards = (List<String>) boardTask.getResult().get(Constants.boardList);
            if(boards != null && boards.size() > 0) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_dropdown_item, boards);
                boardSelectSpinner.setAdapter(arrayAdapter);
            }
        });

        boardSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) boardSelectSpinner.getItemAtPosition(position);
                if(!lastSelectedBoard.equals(selected)) {
                    setProgress(true);
                    getPosts(school, selected, false);
                    lastSelectedBoard = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        getPosts(school, Default_Board, true);

        CollectionReference board = school.collection(lastSelectedBoard);
        board.addSnapshotListener((value, error) -> {
            if(mPostAdapter != null && value != null) {
                postFetchDB(value, false);
            }
        });
    }

    private void getPosts(DocumentReference school, String boardName, boolean isInitializing) {
        CollectionReference board = school.collection(boardName);
        board.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                postFetchDB(snapshot, isInitializing);
            } else {
                ToastHelper.show(mContext, "게시판 목록 가져 오기 실패!", ToastHelper.LENGTH_SHORT);
            }

            setProgress(false);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void postFetchDB(QuerySnapshot snapshot, boolean isInitializing) {
        if(snapshot.isEmpty()) {
            //TODO Show board empty message here
            List<Post> data = new ArrayList<>();
            if(isInitializing) {
                mPostAdapter = new PostAdapter(mContext, data);
                recyclerView.setAdapter(mPostAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            } else {
                mPostAdapter.setItem(data);
                mPostAdapter.notifyDataSetChanged();
            }
        } else {
            List<DocumentSnapshot> posts = snapshot.getDocuments();
            List<Post> data = new ArrayList<>();

            for(DocumentSnapshot post : posts) {
                Post postObj = Post.parseFrom(post);
                data.add(postObj);
            }

            if(isInitializing) {
                mPostAdapter = new PostAdapter(mContext, data);
                recyclerView.setAdapter(mPostAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            } else {
                mPostAdapter.setItem(data);
                mPostAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setProgress(boolean isProgress) {
        mContext.runOnUiThread(() -> progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE));
    }
}