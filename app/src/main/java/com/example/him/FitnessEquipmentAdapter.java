package com.example.him;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class FitnessEquipmentAdapter extends RecyclerView.Adapter<FitnessEquipmentAdapter.ViewHolder> {
    private static final String TAG = FitnessEquipmentAdapter.class.getSimpleName();

    private IAdapterOnClickListener listener;
    private ArrayList<FitnessEquipmentItem> items;

    public FitnessEquipmentAdapter(IAdapterOnClickListener listener, ArrayList<FitnessEquipmentItem> items) {
        this.listener = listener;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_equipment, null);

        // Item 사이즈 조절
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        // ViewHolder 생성
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtFitnessEquipment.setText(this.items.get(position).name);      // 운동기구

        // 예약 확인
        checkReservation(this.items.get(position).id, holder.txtState);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    /* 예약 확인 */
    private void checkReservation(int id, final TextView txtState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.FirestoreCollectionName.RESERVATION);

        // 운동기구 예약 종료일시 체크 (현재일시보다 종료일시가 남아있는 예약이 있는지 체크)
        Query query = reference.whereEqualTo("fitnessEquipmentId", id)
                .whereGreaterThanOrEqualTo("endDateTime", System.currentTimeMillis())
                .limit(1);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    if (task.getResult().size() == 0) {
                        // 예약가능
                        txtState.setTextColor(ContextCompat.getColor(txtState.getContext(), R.color.blue_text_color));
                        txtState.setText("예약가능");
                    } else {
                        txtState.setTextColor(ContextCompat.getColor(txtState.getContext(), R.color.red_text_color));
                        txtState.setText("사용중");
                    }
                } else {
                    // 오류
                    txtState.setTextColor(ContextCompat.getColor(txtState.getContext(), R.color.default_text_color));
                    txtState.setText("오류");
                }
            } else {
                // 오류
                txtState.setTextColor(ContextCompat.getColor(txtState.getContext(), R.color.default_text_color));
                txtState.setText("오류");
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtFitnessEquipment, txtState;

        private ViewHolder(View view) {
            super(view);

            this.txtFitnessEquipment = view.findViewById(R.id.txtFitnessEquipment);
            this.txtState = view.findViewById(R.id.txtState);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // 운동기구 선택
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                listener.onItemClick(bundle, view.getId());
            }
        }
    }
}
