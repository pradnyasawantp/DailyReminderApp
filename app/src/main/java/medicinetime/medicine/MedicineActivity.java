package medicinetime.medicine;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.techmania.medicinereminderapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import medicinetime.Injection;
import medicinetime.notification.NotificationHelper;
import medicinetime.utils.ActivityUtils;

public class MedicineActivity extends AppCompatActivity {
    private CalendarView mCalendarView;
    private TextView datePickerTextView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabAddTask;
    private CoordinatorLayout coordinatorLayout;
    private View calendarContainer;
    private ImageView arrow;
    private MedicinePresenter presenter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.ENGLISH);
    private boolean isCalendarExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_medicine);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            View appBar = findViewById(R.id.app_bar_layout);
            appBar.setPadding(0, bars.top, 0, 0);
            fabAddTask.setTranslationY(-bars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.white));
        NotificationHelper.createNotificationChannel(this);
        requestExactAlarmPermissionIfNeeded();

        mCalendarView = findViewById(R.id.compactcalendar_view);
        datePickerTextView = findViewById(R.id.date_picker_text_view);
        appBarLayout = findViewById(R.id.app_bar_layout);
        fabAddTask = findViewById(R.id.fab_add_task);
        calendarContainer = findViewById(R.id.calendar_container);
        arrow = null;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        datePickerTextView.setOnClickListener(v -> toggleCalendar());
        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            setSubtitle(calendar.getTime());
            if (presenter != null) presenter.reload(calendar.get(Calendar.DAY_OF_WEEK));
            collapseCalendar();
        });

        fabAddTask.setOnClickListener(v -> {
            if (presenter != null) presenter.addNewMedicine();
        });

        setCurrentDate(new Date());

        MedicineFragment fragment = (MedicineFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = MedicineFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
        presenter = new MedicinePresenter(Injection.provideMedicineRepository(this), fragment);
        ViewCompat.requestApplyInsets(coordinatorLayout);
    }

    private void requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                try {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            android.net.Uri.parse("package:" + getPackageName())));
                } catch (Exception ignored) { }
            }
        }
    }

    public void setCurrentDate(Date date) {
        setSubtitle(date);
        mCalendarView.setDate(date.getTime(), true, true);
    }

    public void setSubtitle(Date date) {
        datePickerTextView.setText(dateFormat.format(date));
    }

    private void toggleCalendar() {
        isCalendarExpanded = !isCalendarExpanded;
        calendarContainer.setVisibility(isCalendarExpanded ? View.VISIBLE : View.GONE);
        if (isCalendarExpanded) {
            appBarLayout.setExpanded(true, true);
        }
    }

    private void collapseCalendar() {
        if (!isCalendarExpanded) return;
        isCalendarExpanded = false;
        calendarContainer.setVisibility(View.GONE);
    }
}
