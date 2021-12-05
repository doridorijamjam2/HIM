package com.example.him;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 툴바 안보이게 하기 위함
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);

        // 인트로 화면을 1초동안 보여줌
        new Handler(Looper.getMainLooper()).postDelayed((Runnable) () -> {
            // 초기화
            init();
        }, Constants.LoadingDelay.LONG);
    }

    @Override
    public void onBackPressed() {
        // 백키 눌려도 종료 안되게 하기 위함
        //super.onBackPressed();
    }

    /* 초기화 */
    private void init() {
        // 사용자 등록 Doc ID
        String id = SharedPreferencesUtils.getInstance(this).get(Constants.SharedPreferencesName.USER_DOCUMENT_ID);

        Log.d(TAG, "id: " + id);

        if (!TextUtils.isEmpty(id)) {
            // 사용자 Doc ID 가 있으면 자동 로그인
            login(id);
        } else {
            // 로그인 화면으로 이동
            goLogin();
        }
    }

    /* 로그인 */
    private void login(final String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection(Constants.FirestoreCollectionName.USER).document(id);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 성공
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    // 사용자 객체 생성
                    User user = document.toObject(User.class);

                    GlobalVariable.documentId = document.getId();
                    GlobalVariable.user = user;

                    // 메인으로 이동
                    goMain();
                } else {
                    // 로그인 화면으로 이동
                    goLogin();
                }
            } else {
                // 로그인 화면으로 이동
                goLogin();
            }
        });
    }

    /* 로그인화면으로 이동 */
    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }

    /* 메인화면으로 이동 */
    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}
