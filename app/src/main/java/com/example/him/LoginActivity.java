package com.example.him;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private LinearLayout layLoading;

    private EditText editUserId, editPassword;

    private InputMethodManager imm;                 // 키보드를 숨기기 위해 필요함

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 제목 표시
        setTitle(getString(R.string.activity_title_login));

        // 로딩 레이아웃
        this.layLoading = findViewById(R.id.layLoading);
        ((ProgressBar) findViewById(R.id.progressBar)).setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

        this.editUserId = findViewById(R.id.editUserId);
        this.editUserId.setHint("아이디");

        this.editPassword = findViewById(R.id.editPassword);
        this.editPassword.setHint("비밀번호");

        findViewById(R.id.btnLogin).setOnClickListener(mClickListener);
        this.layLoading.setOnClickListener(mClickListener);

        // 키보드를 숨기기 위해 필요함
        this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        this.editUserId.requestFocus();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
    }

    /* 입력 데이터 체크 */
    private boolean checkData() {
        // 아이디 입력 체크
        String userId = this.editUserId.getText().toString();
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, R.string.msg_user_id_check_empty, Toast.LENGTH_SHORT).show();
            this.editUserId.requestFocus();
            return false;
        }

        // 비밀번호 입력 체크
        String password = this.editPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.msg_password_check_empty, Toast.LENGTH_SHORT).show();
            this.editPassword.requestFocus();
            return false;
        }

        // 키보드 숨기기
        this.imm.hideSoftInputFromWindow(this.editPassword.getWindowToken(), 0);

        return true;
    }

    /* 로그인 */
    private void login() {
        String userId = this.editUserId.getText().toString();
        final String password = this.editPassword.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.FirestoreCollectionName.USER);

        // 로그인
        Query query = reference.whereEqualTo("userId", userId).limit(1);
        query.get().addOnCompleteListener(task -> {
            layLoading.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    if (task.getResult().size() == 0) {
                        // 로그인 실패 (회원이 아님)
                        Toast.makeText(this, R.string.msg_login_user_none, Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            User user = document.toObject(User.class);
                            if (user.getPassword().equals(password)) {
                                // 로그인 성공

                                // Document Id 저장
                                GlobalVariable.documentId = document.getId();

                                // 사용자 객체 생성
                                GlobalVariable.user = user;

                                // SharedPreferences 에 록그인 정보 저장 (자동 로그인 기능)
                                SharedPreferencesUtils.getInstance(LoginActivity.this)
                                        .put(Constants.SharedPreferencesName.USER_DOCUMENT_ID, GlobalVariable.documentId);

                                // 메인 화면으로 이동
                                goMain();
                            } else {
                                // 로그인 실패 (비밀번호 틀림)
                                Toast.makeText(this, R.string.msg_login_password_wrong, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                } else {
                    // 오류
                    Toast.makeText(this, R.string.msg_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                // 오류
                Toast.makeText(this, R.string.msg_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* 메인화면으로 이동 */
    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLogin:
                    // 로그인
                    if (checkData()) {
                        layLoading.setVisibility(View.VISIBLE);
                        // 로딩 레이아웃을 표시하기 위해 딜레이를 줌
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // 로그인
                            login();
                        }, Constants.LoadingDelay.SHORT);
                    }
                    break;
                case R.id.layLoading:
                    // 로딩중 클릭 방지
                    break;
            }
        }
    };
}