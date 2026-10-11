package medicinetime.alarm;

import android.app.NotificationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.techmania.medicinereminderapp.databinding.FragmentReminderBinding;

import medicinetime.data.source.MedicineAlarm;

public class ReminderFragment extends Fragment implements ReminderContract.View {

    public static final String EXTRA_ID = "extra_id";

    private FragmentReminderBinding binding;
    private MedicineAlarm reminder;
    private long id;
    private ReminderContract.Presenter presenter;

    static ReminderFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_ID, id);
        ReminderFragment fragment = new ReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(EXTRA_ID, -1L);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReminderBinding.inflate(inflater, container, false);
        binding.btnDismiss.setOnClickListener(v -> dismissReminder());
        return binding.getRoot();
    }

    @Override
    public void setPresenter(ReminderContract.Presenter presenter) {
        this.presenter = presenter;
    }


    public void showReminder(long reminderId) {
        id = reminderId;
        if (presenter != null) presenter.onStart(reminderId);
    }

    @Override
    public void showMedicine(MedicineAlarm reminder) {
        this.reminder = reminder;
        if (binding == null || reminder == null) return;
        binding.tvMedTime.setText(reminder.getStringTime());
        binding.tvMedicineName.setText(reminder.getPillName());
    }

    @Override
    public void showNoData() {
        if (binding != null) {
            binding.tvMedicineName.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null && id >= 0) {
            presenter.onStart(id);
        }
    }

    private void dismissReminder() {
        cancelNotification();
        if (presenter != null) {
            presenter.finishActivity();
        } else if (isAdded()) {
            requireActivity().finish();
        }
    }

    private void cancelNotification() {
        if (reminder == null || !isAdded()) return;
        NotificationManager manager =
                requireContext().getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.cancel(1000 + (int) reminder.getId());
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onFinish() {
        if (isAdded()) {
            requireActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
