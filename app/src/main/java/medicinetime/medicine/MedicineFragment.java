package medicinetime.medicine;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.techmania.medicinereminderapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import medicinetime.addmedicine.AddMedicineActivity;
import medicinetime.data.source.MedicineAlarm;

public class MedicineFragment extends Fragment implements MedicineContract.View, MedicineAdapter.OnItemClickListener {
    private RecyclerView rvMedList;
    private TextView noMedText, addMedNow;
    private View noMedView;
    private ProgressBar progressLoader;
    private MedicineContract.Presenter presenter;
    private MedicineAdapter medicineAdapter;

    public static MedicineFragment newInstance() { return new MedicineFragment(); }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) { super.onCreate(savedInstanceState); medicineAdapter = new MedicineAdapter(new ArrayList<>()); }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);
        rvMedList = view.findViewById(R.id.medicine_list);
        noMedText = view.findViewById(R.id.noMedText);
        addMedNow = view.findViewById(R.id.add_med_now);
        noMedView = view.findViewById(R.id.no_med_view);
        progressLoader = view.findViewById(R.id.progressLoader);
        addMedNow.setOnClickListener(v -> showAddMedicine());
        rvMedList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMedList.setAdapter(medicineAdapter);
        medicineAdapter.setOnItemClickListener(this);
        return view;
    }

    @Override public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> { if (presenter != null) presenter.addNewMedicine(); });
    }

    @Override public void onResume() {
        super.onResume();
        if (presenter != null) presenter.onStart(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    @Override public void setPresenter(MedicineContract.Presenter presenter) { this.presenter = presenter; }
    @Override public void showLoadingIndicator(boolean active) { if (getView() != null) progressLoader.setVisibility(active ? View.VISIBLE : View.GONE); }

    @Override public void showMedicineList(List<MedicineAlarm> list) {
        medicineAdapter.replaceData(list);
        rvMedList.setVisibility(View.VISIBLE); noMedView.setVisibility(View.GONE);
    }
    @Override public void showAddMedicine() { startActivityForResult(new Intent(getContext(), AddMedicineActivity.class), AddMedicineActivity.REQUEST_ADD_TASK); }
    @Override public void showMedicineDetails(long taskId, String medName) {
        Intent intent = new Intent(getContext(), AddMedicineActivity.class); intent.putExtra(AddMedicineActivity.EXTRA_TASK_ID, taskId); intent.putExtra(AddMedicineActivity.EXTRA_TASK_NAME, medName); startActivity(intent);
    }
    @Override public void showLoadingMedicineError() { showMessage(getString(R.string.loading_tasks_error)); }
    @Override public void showNoMedicine() { showNoTasksViews(getString(R.string.no_medicine_added)); }
    @Override public void showSuccessfullySavedMessage() { showMessage(getString(R.string.successfully_saved_me_message)); }
    @Override public void showMedicineDeletedSuccessfully() { showMessage(getString(R.string.successfully_deleted_message)); if (presenter != null) presenter.onStart(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)); }
    private void showMessage(String message) { if (getView() != null) Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show(); }
    @Override public boolean isActive() { return isAdded(); }
    private void showNoTasksViews(String mainText) { rvMedList.setVisibility(View.GONE); noMedView.setVisibility(View.VISIBLE); noMedText.setText(mainText); }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) { super.onActivityResult(requestCode, resultCode, data); if (presenter != null) presenter.result(requestCode, resultCode); }
    @Override public void onMedicineDeleteClicked(MedicineAlarm alarm) {
        if (alarm == null || presenter == null || !isAdded()) return;

        String title = alarm.getPillName() == null || alarm.getPillName().trim().isEmpty()
                ? getString(R.string.default_reminder_title)
                : alarm.getPillName().trim();

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_reminder_title)
                .setMessage(getString(R.string.delete_reminder_message, title))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (presenter != null && isAdded()) {
                        presenter.deleteMedicineAlarm(alarm, requireActivity());
                    }
                })
                .show();
    }
    @Override public void onMedicineEditClicked(MedicineAlarm alarm) {
        Intent intent = new Intent(getContext(), AddMedicineActivity.class);
        intent.putExtra(AddMedicineActivity.EXTRA_EDIT_ALARM_ID, alarm.getId());
        intent.putExtra(AddMedicineActivity.EXTRA_EDIT_ALARM_NAME, alarm.getPillName());
        startActivityForResult(intent, AddMedicineActivity.REQUEST_ADD_TASK);
    }
}
