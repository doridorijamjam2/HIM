package com.example.him;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class FitnessEquipmentListActivity extends AppCompatActivity {
    private static final String TAG = FitnessEquipmentListActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList<FitnessEquipmentItem> items;

    private int partKind;                       // 부위종류

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_equipment_list);

        // 부위 정보
        Intent intent = getIntent();
        this.partKind = intent.getIntExtra("part_kind", Constants.PartKind.CHEST);  // 부위종류
        String title = intent.getStringExtra("part_name");                          // 부위명

        // 제목 표시
        setTitle(title);

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 리사이클러뷰
        this.recyclerView = findViewById(R.id.recyclerView);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 운동기구 목록
        listFitnessEquipment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCode.RESERVATION) {
                // 예약 이후 리스트 새로구성
                listFitnessEquipment();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 운동기구 목록 */
    private void listFitnessEquipment() {
        // 부위에 맞는 운동기구 array
        this.items = FitnessEquipmentData.getInstance().getItems(this.partKind);

        // 리스트에 어뎁터 설정
        FitnessEquipmentAdapter adapter = new FitnessEquipmentAdapter(mAdapterListener, this.items);
        this.recyclerView.setAdapter(adapter);
    }

    /* 운동기구  클릭 리스너 */
    private final IAdapterOnClickListener mAdapterListener = (bundle, id) -> {
        // 운동기구 선택
        int position = bundle.getInt("position");

        // 예약화면으로 이동
        Intent intent = new Intent(this, ReservationInfoActivity.class);
        intent.putExtra("id", items.get(position).id);
        intent.putExtra("name", items.get(position).name);
        intent.putExtra("url", items.get(position).url);
        startActivityForResult(intent, Constants.RequestCode.RESERVATION);
    };
}
