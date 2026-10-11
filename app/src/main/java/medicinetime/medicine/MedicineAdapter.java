package medicinetime.medicine;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.techmania.medicinereminderapp.R;

import java.util.List;

import medicinetime.data.source.MedicineAlarm;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<MedicineAlarm> medicineAlarmList;
    private OnItemClickListener onItemClickListener;

    public MedicineAdapter(List<MedicineAlarm> medicineAlarmList) { this.medicineAlarmList = medicineAlarmList; }
    public void replaceData(List<MedicineAlarm> list) { medicineAlarmList = list; notifyDataSetChanged(); }
    public void setOnItemClickListener(OnItemClickListener listener) { onItemClickListener = listener; }

    @NonNull @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MedicineViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_medicine, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineAlarm alarm = medicineAlarmList.get(position);
        if (alarm == null) return;
        String title = alarm.getPillName() == null || alarm.getPillName().trim().isEmpty() ? "Reminder" : alarm.getPillName().trim();
        holder.tvMedicineName.setText(title);
        holder.tvMedTime.setText(alarm.getStringTime());
        holder.badge.setText(title.substring(0, 1).toUpperCase());
        holder.ivAlarmEdit.setOnClickListener(v -> { if (onItemClickListener != null) onItemClickListener.onMedicineEditClicked(alarm); });
        holder.ivAlarmDelete.setOnClickListener(v -> { if (onItemClickListener != null) onItemClickListener.onMedicineDeleteClicked(alarm); });
        holder.itemView.setOnClickListener(v -> { if (onItemClickListener != null) onItemClickListener.onMedicineEditClicked(alarm); });
    }

    @Override public int getItemCount() { return medicineAlarmList == null ? 0 : medicineAlarmList.size(); }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView badge, tvMedicineName, tvMedTime;
        ImageView ivMedicineAction, ivAlarmEdit, ivAlarmDelete;
        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.reminder_badge);
            tvMedTime = itemView.findViewById(R.id.tv_med_time);
            tvMedicineName = itemView.findViewById(R.id.tv_medicine_name);
            ivMedicineAction = itemView.findViewById(R.id.iv_medicine_action);
            ivAlarmEdit = itemView.findViewById(R.id.iv_alarm_edit);
            ivAlarmDelete = itemView.findViewById(R.id.iv_alarm_delete);
        }
    }
    interface OnItemClickListener {
        void onMedicineDeleteClicked(MedicineAlarm medicineAlarm);
        void onMedicineEditClicked(MedicineAlarm medicineAlarm);
    }
}
