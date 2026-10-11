package medicinetime.addmedicine;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.techmania.medicinereminderapp.R;

import medicinetime.Injection;
import medicinetime.utils.ActivityUtils;

public class AddMedicineActivity extends AppCompatActivity {
    public static final int REQUEST_ADD_TASK = 1;
    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";
    public static final String EXTRA_TASK_ID = "task_extra_id";
    public static final String EXTRA_TASK_NAME = "task_extra_name";
    public static final String EXTRA_EDIT_ALARM_ID = "EXTRA_EDIT_ALARM_ID";
    public static final String EXTRA_EDIT_ALARM_NAME = "EXTRA_EDIT_ALARM_NAME";
    private AddMedicinePresenter presenter;
    private ActionBar actionBar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_add_medicine);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Toolbar tb = findViewById(R.id.toolbar); tb.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) { actionBar.setDisplayHomeAsUpEnabled(true); actionBar.setDisplayShowHomeEnabled(true); }

        AddMedicineFragment fragment = (AddMedicineFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        int medId = getIntent().getIntExtra(AddMedicineFragment.ARGUMENT_EDIT_MEDICINE_ID, 0);
        long editAlarmId = getIntent().getLongExtra(EXTRA_EDIT_ALARM_ID, -1L);
        String editAlarmName = getIntent().getStringExtra(EXTRA_EDIT_ALARM_NAME);
        if (fragment == null) {
            fragment = AddMedicineFragment.newInstance();
            if (editAlarmId > 0) {
                Bundle b = new Bundle(); b.putLong(EXTRA_EDIT_ALARM_ID, editAlarmId); b.putString(EXTRA_EDIT_ALARM_NAME, editAlarmName); fragment.setArguments(b);
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
        setToolbarTitle(editAlarmId > 0 ? editAlarmName : null);
        boolean load = savedInstanceState == null || savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, true);
        presenter = new AddMedicinePresenter(medId, Injection.provideMedicineRepository(getApplicationContext()), fragment, load);
        ViewCompat.requestApplyInsets(findViewById(R.id.drawer_layout));
    }
    public void setToolbarTitle(String name) { if (actionBar != null) actionBar.setTitle(name == null || name.trim().isEmpty() ? getString(R.string.new_medicine) : name); }
    @Override protected void onSaveInstanceState(Bundle outState) { if (presenter != null) outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, presenter.isDataMissing()); super.onSaveInstanceState(outState); }
    @Override public boolean onSupportNavigateUp() { onBackPressed(); return true; }
}
