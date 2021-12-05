package com.example.him;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class ReservationInfoActivity extends AppCompatActivity {
    private static final String TAG = ReservationInfoActivity.class.getSimpleName();

    private LinearLayout layLoading, layReservation1, layReservation2;
    private EditText editUseTime;
    private TextView txtEndDateTime, txtUserName;

    private int id;                             // 운동기구 Id
    private String url;                         // 운동기구 동영상 url
    private InputMethodManager imm;             // 키보드를 숨기기 위해 필요함

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);

        // 부위 정보
        Intent intent = getIntent();
        this.id = intent.getIntExtra("id", 0);                  // 운동기구 Id
        this.url = intent.getStringExtra("url");                // 운동기구 동영상 url
        String title = intent.getStringExtra("name");           // 운동기구명

        // 제목 표시
        setTitle(title);

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 로딩 레이아웃
        this.layLoading = findViewById(R.id.layLoading);
        ((ProgressBar) findViewById(R.id.progressBar)).setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

        this.layReservation1 = findViewById(R.id.layReservation1);
        this.layReservation2 = findViewById(R.id.layReservation2);

        this.editUseTime = findViewById(R.id.editUseTime);
        this.editUseTime.setHint("사용시간(분)");
        this.editUseTime.setImeOptions(EditorInfo.IME_ACTION_DONE);

        this.txtEndDateTime = findViewById(R.id.txtEndDateTime);
        this.txtUserName = findViewById(R.id.txtUserName);

        findViewById(R.id.btnReservation).setOnClickListener(mClickListener);
        findViewById(R.id.txtExplanation).setOnClickListener(mClickListener);
        this.layLoading.setOnClickListener(mClickListener);

        this.layReservation1.setVisibility(View.GONE);
        this.layReservation2.setVisibility(View.GONE);

        // 예약정보 보기
        infoReservation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 예약정보 보기 */
    private void infoReservation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.FirestoreCollectionName.RESERVATION);

        // 운동기구 예약 종료일시 체크 (현재일시보다 종료일시가 남아있는 예약이 있는지 체크)
        Query query = reference.whereEqualTo("fitnessEquipmentId", this.id)
                .whereGreaterThanOrEqualTo("endDateTime", System.currentTimeMillis())
                .limit(1);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    if (task.getResult().size() == 0) {
                        // 예약가능
                        this.layReservation1.setVisibility(View.VISIBLE);
                        this.layReservation2.setVisibility(View.GONE);

                        // 키보드를 숨기기 위해 필요함
                        this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                        this.editUseTime.requestFocus();
                    } else {
                        // 사용중
                        this.layReservation1.setVisibility(View.GONE);
                        this.layReservation2.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);

                            // 예약 종료 시간
                            this.txtEndDateTime.setText(Utils.getDate("HH:mm", reservation.getEndDateTime()));

                            // 사용자 표시
                            displayUser(reservation.getMasterId());
                            break;
                        }
                    }
                }
            }
        });
    }

    /* 사용자 표시 */
    private void displayUser(String masterId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference reference = db.collection(Constants.FirestoreCollectionName.USER).document(masterId);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 성공
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        if (document.getData() != null) {
                            // 사용자
                            this.txtUserName.setText(document.getData().get("name").toString());
                        }
                    }
                }
            }
        });
    }

    /* 입력 데이터 체크 */
    private boolean checkData() {
        // 사용시간 입력 체크
        String useTime = this.editUseTime.getText().toString();
        if (TextUtils.isEmpty(useTime)) {
            Toast.makeText(this, R.string.msg_use_time_check_empty, Toast.LENGTH_SHORT).show();
            this.editUseTime.requestFocus();
            return false;
        }

        if (this.imm != null) {
            // 키보드 숨기기
            this.imm.hideSoftInputFromWindow(this.editUseTime.getWindowToken(), 0);
        }

        return true;
    }

    /* 예약하기 */
    private void reserve() {
        String useTime = this.editUseTime.getText().toString();

        long startDateTime = System.currentTimeMillis();                            // 예약시작시간
        long endDateTime = startDateTime + (Integer.parseInt(useTime) * 60 * 1000); // 예약종료시간

        Reservation reservation = new Reservation(GlobalVariable.documentId, this.id, startDateTime, endDateTime);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 예약 등록
        db.collection(Constants.FirestoreCollectionName.RESERVATION)
                .add(reservation)
                .addOnSuccessListener(documentReference -> {
                    // 성공
                    this.layLoading.setVisibility(View.GONE);

                    this.layReservation1.setVisibility(View.GONE);

                    // 예약정보 보기
                    infoReservation();

                    setResult(Activity.RESULT_OK);
                })
                .addOnFailureListener(e -> {
                    // 예약 실패
                    this.layLoading.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.msg_error, Toast.LENGTH_SHORT).show();
                });
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnReservation:
                    // 예약하기
                    if (checkData()) {
                        layLoading.setVisibility(View.VISIBLE);
                        // 로딩 레이아웃을 표시하기 위해 딜레이를 줌
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // 예약하기
                            reserve();
                        }, Constants.LoadingDelay.SHORT);
                    }
                    break;
                case R.id.txtExplanation:
                    // 운동기구에 대한 설명 보기
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(url);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    break;
                case R.id.layLoading:
                    // 로딩중 클릭 방지
                    break;
            }
        }
    };
}
