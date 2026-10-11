package medicinetime.alarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.techmania.medicinereminderapp.R;
import com.techmania.medicinereminderapp.databinding.ActivityReminderActvityBinding;

import medicinetime.Injection;
import medicinetime.utils.ActivityUtils;

public class ReminderActivity extends AppCompatActivity {
    private ActivityReminderActvityBinding binding;
    private ReminderPresenter presenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true); setTurnScreenOn(true);
            android.app.KeyguardManager km = (android.app.KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km != null) km.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityReminderActvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.toolbar.setPadding(0, bars.top, 0, 0);
            binding.contentFrame.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) { bar.setDisplayHomeAsUpEnabled(true); bar.setDisplayShowTitleEnabled(true); bar.setTitle("Reminder"); }
        Intent intent = getIntent();
        long id = intent.getLongExtra(ReminderFragment.EXTRA_ID, -1L);
        if (id < 0) { finish(); return; }
        ReminderFragment fragment = (ReminderFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = ReminderFragment.newInstance(id);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
        presenter = new ReminderPresenter(Injection.provideMedicineRepository(this), fragment);
        ViewCompat.requestApplyInsets(binding.getRoot());
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        long id = intent.getLongExtra(ReminderFragment.EXTRA_ID, -1L);
        ReminderFragment fragment = (ReminderFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (id >= 0 && fragment != null) fragment.showReminder(id);
    }
    @Override protected void onResume() { super.onResume(); if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) { setShowWhenLocked(true); setTurnScreenOn(true); } }
    @Override public boolean onOptionsItemSelected(MenuItem item) { if (item.getItemId() == android.R.id.home) { finish(); return true; } return super.onOptionsItemSelected(item); }
    @Override public void onBackPressed() { finish(); }
}
