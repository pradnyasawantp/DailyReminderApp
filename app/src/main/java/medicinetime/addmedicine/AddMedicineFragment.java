package medicinetime.addmedicine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.techmania.medicinereminderapp.R;
import com.techmania.medicinereminderapp.databinding.FragmentAddMedicineBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import medicinetime.alarm.AlarmScheduler;
import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.Pills;
import medicinetime.views.DayViewCheckBox;

public class AddMedicineFragment extends Fragment implements AddMedicineContract.View {
    public static final String ARGUMENT_EDIT_MEDICINE_ID = "ARGUMENT_EDIT_MEDICINE_ID";
    public static final String ARGUMENT_EDIT_MEDICINE_NAME = "ARGUMENT_EDIT_MEDICINE_NAME";
    private boolean[] dayOfWeekList = new boolean[7];
    private int hour, minute;
    private AddMedicineContract.Presenter mPresenter;
    private FragmentAddMedicineBinding binding;
    private boolean editMode;
    private long editAlarmDbId = -1L;
    private String editPillName;
    private int editAlarmGroupId;
    private boolean savingReminder = false;
    private static final int MAX_TITLE_LENGTH = 80;

    static AddMedicineFragment newInstance() { return new AddMedicineFragment(); }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(v -> saveReminder());
        Bundle args = getArguments();
        if (args != null && args.containsKey(AddMedicineActivity.EXTRA_EDIT_ALARM_ID)) {
            editMode = true;
            editAlarmDbId = args.getLong(AddMedicineActivity.EXTRA_EDIT_ALARM_ID, -1L);
            editPillName = args.getString(AddMedicineActivity.EXTRA_EDIT_ALARM_NAME);
            loadExistingAlarmForEdit();
        } else {
            dayOfWeekList[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1] = true;
            syncDayViews();
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddMedicineBinding.inflate(inflater, container, false);
        setCurrentTime();
        binding.tvMedicineTime.setOnClickListener(v -> showTimePicker());
        binding.everyDay.setOnClickListener(this::onCheckboxClicked);
        binding.dvMonday.setOnClickListener(this::onCheckboxClicked); binding.dvTuesday.setOnClickListener(this::onCheckboxClicked);
        binding.dvWednesday.setOnClickListener(this::onCheckboxClicked); binding.dvThursday.setOnClickListener(this::onCheckboxClicked);
        binding.dvFriday.setOnClickListener(this::onCheckboxClicked); binding.dvSaturday.setOnClickListener(this::onCheckboxClicked);
        binding.dvSunday.setOnClickListener(this::onCheckboxClicked);
        return binding.getRoot();
    }

    @Override public void setPresenter(AddMedicineContract.Presenter presenter) { mPresenter = presenter; }
    @Override public void showEmptyMedicineError() { if (isAdded()) Toast.makeText(requireContext(), "Could not load that reminder", Toast.LENGTH_SHORT).show(); }
    @Override public void showMedicineList() { requireActivity().setResult(Activity.RESULT_OK); requireActivity().finish(); }
    @Override public boolean isActive() { return isAdded(); }
    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }

    private void loadExistingAlarmForEdit() {
        if (!editMode || mPresenter == null || binding == null) return;
        MedicineAlarm selected = mPresenter.getMedicineAlarmById(editAlarmDbId);
        if (selected == null) {
            if (isAdded()) Toast.makeText(requireContext(), "This reminder could not be found. Please go back and try again.", Toast.LENGTH_LONG).show();
            return;
        }
        editPillName = selected.getPillName(); editAlarmGroupId = selected.getAlarmId();
        binding.editMedName.setText(selected.getPillName()); hour = selected.getHour(); minute = selected.getMinute();
        binding.tvMedicineTime.setText(formatTime(hour, minute));
        List<MedicineAlarm> group = mPresenter.getAlarmGroup(editPillName, editAlarmGroupId);
        dayOfWeekList = new boolean[7];
        for (MedicineAlarm alarm : group) { int day = alarm.getDayOfWeekNumber(); if (day >= 1 && day <= 7) dayOfWeekList[day - 1] = true; }
        syncDayViews();
    }

    private void syncDayViews() {
        if (binding == null) return;
        binding.dvSunday.setChecked(dayOfWeekList[0]); binding.dvMonday.setChecked(dayOfWeekList[1]); binding.dvTuesday.setChecked(dayOfWeekList[2]);
        binding.dvWednesday.setChecked(dayOfWeekList[3]); binding.dvThursday.setChecked(dayOfWeekList[4]); binding.dvFriday.setChecked(dayOfWeekList[5]); binding.dvSaturday.setChecked(dayOfWeekList[6]);
        boolean all = true; for (boolean b : dayOfWeekList) if (!b) { all = false; break; } binding.everyDay.setChecked(all);
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked(); int id = view.getId();
        if (id == R.id.every_day) {
            for (int i = 0; i < binding.checkboxLayout.getChildCount(); i++) {
                View child = binding.checkboxLayout.getChildAt(i); if (child instanceof DayViewCheckBox) ((DayViewCheckBox) child).setChecked(checked);
            }
            for (int i = 0; i < 7; i++) dayOfWeekList[i] = checked;
            return;
        }
        int index = id == R.id.dv_sunday ? 0 : id == R.id.dv_monday ? 1 : id == R.id.dv_tuesday ? 2 : id == R.id.dv_wednesday ? 3 : id == R.id.dv_thursday ? 4 : id == R.id.dv_friday ? 5 : 6;
        dayOfWeekList[index] = checked;
        if (!checked) binding.everyDay.setChecked(false); else { boolean all = true; for (boolean b : dayOfWeekList) if (!b) { all = false; break; } binding.everyDay.setChecked(all); }
    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(requireContext(), (TimePicker view, int h, int m) -> { hour = h; minute = m; binding.tvMedicineTime.setText(formatTime(h, m)); }, hour, minute, false);
        dialog.setTitle(getString(R.string.choose_time)); dialog.show();
    }
    private void setCurrentTime() { Calendar now = Calendar.getInstance(); hour = now.get(Calendar.HOUR_OF_DAY); minute = now.get(Calendar.MINUTE); binding.tvMedicineTime.setText(formatTime(hour, minute)); }
    private String formatTime(int h, int m) { int h12 = h % 12; if (h12 == 0) h12 = 12; return String.format(Locale.getDefault(), "%d:%02d %s", h12, m, h < 12 ? "AM" : "PM"); }

    private void saveReminder() {
        if (binding == null || mPresenter == null || savingReminder) return;

        String title = binding.editMedName.getText() == null ? "" : binding.editMedName.getText().toString().trim();
        binding.editMedName.setError(null);
        if (title.isEmpty()) {
            binding.editMedName.setError("Enter a reminder title");
            binding.editMedName.requestFocus();
            return;
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            binding.editMedName.setError("Title must be 80 characters or fewer");
            binding.editMedName.requestFocus();
            return;
        }
        if (title.replaceAll("[\\p{Z}\\s]", "").isEmpty()) {
            binding.editMedName.setError("Title must contain letters or numbers");
            binding.editMedName.requestFocus();
            return;
        }
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            Toast.makeText(requireContext(), "Choose a valid reminder time", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daySelected = false;
        for (boolean selected : dayOfWeekList) if (selected) { daySelected = true; break; }
        if (!daySelected) {
            Toast.makeText(requireContext(), "Select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editMode && (editAlarmDbId <= 0 || editPillName == null || editPillName.trim().isEmpty())) {
            Toast.makeText(requireContext(), "Unable to edit this reminder. Please reopen it and try again.", Toast.LENGTH_LONG).show();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) requireContext().getSystemService(android.content.Context.ALARM_SERVICE);
            if (alarmManager == null || !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Allow exact alarms in Settings before saving reminders", Toast.LENGTH_LONG).show();
                return;
            }
        }

        savingReminder = true;
        View saveButton = requireActivity().findViewById(R.id.fab_edit_task_done);
        if (saveButton != null) saveButton.setEnabled(false);
        try {
            if (editMode) {
                mPresenter.updateAlarmGroup(editPillName, editAlarmGroupId, title, hour, minute, "", "", dayOfWeekList, requireContext());
                rescheduleUpdatedGroup(title, editAlarmGroupId);
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.reminder_updated_title)
                        .setMessage(getString(R.string.reminder_updated_message, title))
                        .setPositiveButton(R.string.ok, (dialog, which) -> showMedicineList())
                        .setOnCancelListener(dialog -> showMedicineList())
                        .show();
                return;
            }

            String dateString = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
            int alarmId = 100000000 + new Random().nextInt(900000000);
            MedicineAlarm alarm = new MedicineAlarm();
            alarm.setDateString(dateString); alarm.setHour(hour); alarm.setMinute(minute);
            alarm.setPillName(title); alarm.setDayOfWeek(dayOfWeekList);
            alarm.setDoseQuantity(""); alarm.setDoseUnit(""); alarm.setAlarmId(alarmId);
            Pills pill;
            if (!mPresenter.isMedicineExits(title)) {
                pill = new Pills();
                pill.setPillName(title);
                long pillId = mPresenter.addPills(pill);
                if (pillId <= 0) throw new IllegalStateException("Could not create reminder title");
                pill.setPillId(pillId);
            } else {
                pill = mPresenter.getPillsByName(title);
                if (pill == null) throw new IllegalStateException("Could not load the existing reminder title");
            }
            pill.addAlarm(alarm);
            mPresenter.saveMedicine(alarm, pill);
            List<MedicineAlarm> rows = mPresenter.getAlarmGroup(title, alarmId);
            if (rows != null) {
                for (MedicineAlarm row : rows) AlarmScheduler.scheduleAlarm(requireContext(), row.getId(), alarmId + row.getDayOfWeekNumber(), hour, minute, row.getDayOfWeekNumber());
            }
            Toast.makeText(requireContext(), String.format(Locale.getDefault(), getString(R.string.reminder_saved_format), title), Toast.LENGTH_SHORT).show();
            showMedicineList();
        } catch (Exception e) {
            savingReminder = false;
            if (saveButton != null) saveButton.setEnabled(true);
            if (isAdded()) Toast.makeText(requireContext(), "Could not save the reminder. Please check the details and try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void rescheduleUpdatedGroup(String title, int alarmId) {
        List<MedicineAlarm> rows = mPresenter.getAlarmGroup(title, alarmId);
        for (MedicineAlarm row : rows) AlarmScheduler.scheduleAlarm(requireContext(), row.getId(), alarmId + row.getDayOfWeekNumber(), row.getHour(), row.getMinute(), row.getDayOfWeekNumber());
    }
}
