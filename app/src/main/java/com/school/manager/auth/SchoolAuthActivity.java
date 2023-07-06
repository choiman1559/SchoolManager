package com.school.manager.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.manager.MainActivity;
import com.school.manager.R;
import com.school.manager.post.db.Constants;
import com.school.manager.post.db.School;
import com.school.manager.util.ToastHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.school.manager.Application.selfInfo;
import static com.school.manager.Application.prefs;

public class SchoolAuthActivity extends AppCompatActivity {

    List<School> schoolList = new ArrayList<>();
    List<String> schoolNames = new ArrayList<>();

    static BitSet dontNeedEncoding;
    static final int caseDiff = ('a' - 'A');

    static {
        dontNeedEncoding = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set(' ');
        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('*');
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        AppCompatEditText nameInput = findViewById(R.id.item_name_input);
        AppCompatEditText nickInput = findViewById(R.id.item_nick_input);
        AppCompatEditText schoolInput = findViewById(R.id.item_school_input);

        ProgressBar progressBar = findViewById(R.id.item_progress_bar);
        AppCompatSpinner schoolSelect = findViewById(R.id.item_school_select);

        MaterialButton schoolSearch = findViewById(R.id.item_school_search);
        MaterialButton continueButton = findViewById(R.id.item_continue_button);

        progressBar.setVisibility(View.GONE);
        schoolSelect.setVisibility(View.GONE);
        continueButton.setEnabled(false);

        continueButton.setOnClickListener(v -> {
            String nameString = Objects.requireNonNull(nameInput.getText()).toString().strip();
            String nickString = Objects.requireNonNull(nickInput.getText()).toString().strip();
            boolean isGoodToProceed = true;

            if (nameString.isEmpty()) {
                isGoodToProceed = false;
                nameInput.setError("이름 입력");
            }

            if (nickString.isEmpty()) {
                isGoodToProceed = false;
                nickInput.setError("닉네임 입력");
            }

            if (isGoodToProceed) {
                selfInfo.setUUID(prefs.getString(Constants.UUID, ""));
                selfInfo.setAdmin(false);
                selfInfo.setBanned(false);
                selfInfo.setActualName(nameString);
                selfInfo.setUserName(nickString);
                selfInfo.setProfileIconPath("");
                selfInfo.setLoginType("google");

                School school = schoolList.get(schoolSelect.getSelectedItemPosition());
                selfInfo.setSchoolName(school.getName() + "_" + school.getUniqueId());
                selfInfo.setSchoolType(school.getSchoolType());

                Map<String, Object> map = new HashMap<>();
                map.put(Constants.userId, selfInfo.getUserName());
                map.put(Constants.isAdmin, selfInfo.isAdmin());
                map.put(Constants.isBanned, selfInfo.isBanned());
                map.put(Constants.loginWith, selfInfo.getLoginType());
                map.put(Constants.name, selfInfo.getActualName());
                map.put(Constants.profileIcon, selfInfo.getProfileIconPath());
                map.put(Constants.school, selfInfo.getSchoolName());
                map.put(Constants.schoolType, selfInfo.getSchoolType());

                FirebaseFirestore.getInstance()
                        .document("/userInfo/" + selfInfo.getUUID()).set(map)
                        .addOnCompleteListener(task -> createSchoolAndStart(school));
            }
        });

        schoolSearch.setOnClickListener(v -> {
            String schoolStr = Objects.requireNonNull(schoolInput.getText()).toString().strip();
            if (schoolStr.isEmpty()) {
                schoolInput.setError("학교 이름 입력");
            } else if (schoolStr.length() < 2) {
                schoolInput.setError("2글자 이상 입력");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                schoolSelect.setVisibility(View.GONE);
                continueButton.setEnabled(false);

                if (schoolStr.endsWith("고") || schoolStr.endsWith("초")) {
                    schoolStr += "등학교";
                }

                if (schoolStr.endsWith("중")) {
                    schoolStr += "학교";
                }

                final String finalSchoolStr = schoolStr;
                new Thread(() -> {
                    try {
                        String urlBuilder = "http://api.data.go.kr/openapi/tn_pubr_public_elesch_mskul_lc_api" + "?" + encode("serviceKey") + prefs.getString("schoolApi", "") +
                                "&" + encode("pageNo") + "=" + encode("1") +
                                "&" + encode("numOfRows") + "=" + encode("100") +
                                "&" + encode("type") + "=" + encode("json") +
                                "&" + encode("schoolNm") + "=" + encode(finalSchoolStr);
                        URL url = new URL(urlBuilder);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-type", "application/json");
                        Log.d("HTTPS", "Response code: " + conn.getResponseCode());

                        BufferedReader rd;
                        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        } else {
                            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        }

                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            sb.append(line);
                        }
                        rd.close();
                        conn.disconnect();

                        schoolList = new ArrayList<>();
                        schoolNames = new ArrayList<>();

                        JSONArray jsonArray = new JSONObject(sb.toString()).getJSONObject("response").getJSONObject("body").getJSONArray("items");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            School school = new School();
                            school.setName(jsonObject.getString("schoolNm"));
                            school.setLocation(jsonObject.getString("rdnmadr"));
                            school.setUniqueId(jsonObject.getString("schoolId"));
                            school.setSchoolType(jsonObject.getString("schoolSe"));

                            schoolList.add(school);
                            schoolNames.add(jsonObject.getString("rdnmadr"));
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, schoolNames);
                            schoolSelect.setAdapter(arrayAdapter);
                            progressBar.setVisibility(View.GONE);
                            schoolSelect.setVisibility(View.VISIBLE);
                            continueButton.setEnabled(true);
                        });
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            ToastHelper.show(this, "검색 결과 없음", "확인",ToastHelper.LENGTH_SHORT);
                            progressBar.setVisibility(View.GONE);
                            schoolSelect.setVisibility(View.GONE);
                            continueButton.setEnabled(false);
                        });
                    }
                }).start();
            }
        });
    }

    @SuppressWarnings("unchecked")
    void createSchoolAndStart(School school) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.document("/defaultData/board").get()
                .addOnCompleteListener(task1 -> {
                    DocumentSnapshot snapshot = task1.getResult();
                    List<String> listBoard = (List<String>) snapshot.get(school.getSchoolType().equals("초등학교") ? "boardList_elementary" : "boardList");
                    school.setBoardList(listBoard);
                    school.setAdminBoard((String) snapshot.get(school.getSchoolType().equals("초등학교") ? "admin_board_elementary" : "admin_board"));
                    firebaseFirestore.document(String.format("/%s/%s_%s", school.getSchoolType(), school.getName(), school.getUniqueId())).set(school)
                            .addOnCompleteListener(task0 -> {
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            });
                });
    }

    static String encode(String s) {
        final Charset charset = StandardCharsets.UTF_8;
        boolean needToChange = false;
        StringBuilder out = new StringBuilder(s.length());
        CharArrayWriter charArrayWriter = new CharArrayWriter();

        for (int i = 0; i < s.length(); ) {
            int c = (int) s.charAt(i);
            if (dontNeedEncoding.get(c)) {
                if (c == ' ') {
                    c = '+';
                    needToChange = true;
                }
                out.append((char) c);
                i++;
            } else {
                do {
                    charArrayWriter.write(c);
                    if (c >= 0xD800 && c <= 0xDBFF) {
                        if ((i + 1) < s.length()) {
                            int d = (int) s.charAt(i + 1);
                            if (d >= 0xDC00 && d <= 0xDFFF) {
                                charArrayWriter.write(d);
                                i++;
                            }
                        }
                    }
                    i++;
                } while (i < s.length() && !dontNeedEncoding.get((c = (int) s.charAt(i))));

                charArrayWriter.flush();
                String str = new String(charArrayWriter.toCharArray());
                byte[] ba = str.getBytes(charset);
                for (byte b : ba) {
                    out.append('%');
                    char ch = Character.forDigit((b >> 4) & 0xF, 16);
                    if (Character.isLetter(ch)) {
                        ch -= caseDiff;
                    }
                    out.append(ch);
                    ch = Character.forDigit(b & 0xF, 16);
                    if (Character.isLetter(ch)) {
                        ch -= caseDiff;
                    }
                    out.append(ch);
                }
                charArrayWriter.reset();
                needToChange = true;
            }
        }
        return (needToChange ? out.toString() : s);
    }
}
