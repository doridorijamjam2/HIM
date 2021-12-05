package com.example.him;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BackPressHandler backPressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 제목 표시
        setTitle(getString(R.string.app_name));

        findViewById(R.id.layPart1).setOnClickListener(mClickListener);
        findViewById(R.id.layPart2).setOnClickListener(mClickListener);
        findViewById(R.id.layPart3).setOnClickListener(mClickListener);
        findViewById(R.id.layPart4).setOnClickListener(mClickListener);

        // 종료 핸들러
        this.backPressHandler = new BackPressHandler(this);
    }

    @Override
    public void onBackPressed() {
        this.backPressHandler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // main 메뉴 생성
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            // 로그아웃
            new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                        // 로그아웃
                        logout();
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_title_logout)
                    .setMessage(R.string.dialog_msg_logout)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 로그아웃 */
    private void logout() {
        // Document Id 값 clear
        SharedPreferencesUtils.getInstance(this).put(Constants.SharedPreferencesName.USER_DOCUMENT_ID, "");

        // 로그인화면으로 이동
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private final View.OnClickListener mClickListener = view -> {
        int partKind = -1;
        String partName = "";

        switch (view.getId()) {
            case R.id.layPart1:
                // 가슴
                partKind = Constants.PartKind.CHEST;
                partName = "가슴";
                break;
            case R.id.layPart2:
                // 하체
                partKind = Constants.PartKind.LOWER_BODY;
                partName = "하체";
                break;
            case R.id.layPart3:
                // 등
                partKind = Constants.PartKind.BACK;
                partName = "등";
                break;
            case R.id.layPart4:
                // 어깨
                partKind = Constants.PartKind.SHOULDER;
                partName = "어깨";
                break;
        }

        if (partKind != -1) {
            // 부위별 운동기구 화면 호출
            Intent intent = new Intent(this, FitnessEquipmentListActivity.class);
            intent.putExtra("part_kind", partKind);
            intent.putExtra("part_name", partName);
            startActivity(intent);
        }
    };

    /* Back Press Class */
    private class BackPressHandler {
        private final Context context;
        private Toast toast;

        private long backPressedTime = 0;

        public BackPressHandler(Context context) {
            this.context = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > this.backPressedTime + (Constants.LoadingDelay.LONG * 2)) {
                this.backPressedTime = System.currentTimeMillis();

                this.toast = Toast.makeText(this.context, R.string.msg_back_press_end, Toast.LENGTH_SHORT);
                this.toast.show();
                return;
            }

            if (System.currentTimeMillis() <= this.backPressedTime + (Constants.LoadingDelay.LONG * 2)) {
                // 종료
                moveTaskToBack(true);
                finish();
                this.toast.cancel();
            }
        }
    }
}