package medicinetime.data.source;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MedicineAlarm implements Comparable<MedicineAlarm> {

    private long id;

    // AlarmManager request code
    private int alarmId;

    private int hour;
    private int minute;

    private String pillName;

    private String doseQuantity;
    private String doseUnit;

    private String dateString;

    // Sunday=1 ... Saturday=7
    private int dayOfWeekNumber;

    private final List<Long> ids = new ArrayList<>();

    private boolean[] dayOfWeek = new boolean[7];

    public MedicineAlarm() {
    }

    public MedicineAlarm(long id,
                         int hour,
                         int minute,
                         String pillName,
                         String doseQuantity,
                         String doseUnit,
                         int alarmId) {

        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.pillName = pillName;
        this.doseQuantity = doseQuantity;
        this.doseUnit = doseUnit;
        this.alarmId = alarmId;
    }

    // ----------------------------------------------------
    // Database ID
    // ----------------------------------------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // ----------------------------------------------------
    // AlarmManager ID
    // ----------------------------------------------------

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    // ----------------------------------------------------
    // Time
    // ----------------------------------------------------

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    // ----------------------------------------------------
    // Medicine
    // ----------------------------------------------------

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public String getDoseQuantity() {
        return doseQuantity;
    }

    public void setDoseQuantity(String doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    // ----------------------------------------------------
    // Date
    // ----------------------------------------------------

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    // ----------------------------------------------------
    // Day
    // ----------------------------------------------------

    public void setDayOfWeek(boolean[] days) {

        if (days == null || days.length != 7)
            return;

        dayOfWeek = days.clone();

        // Save first selected day for restoring alarms
        for (int i = 0; i < 7; i++) {

            if (dayOfWeek[i]) {

                dayOfWeekNumber = i + 1;
                break;
            }
        }
    }

    public boolean[] getDayOfWeek() {
        return dayOfWeek.clone();
    }

    public int getDayOfWeekNumber() {
        return dayOfWeekNumber;
    }

    public void setDayOfWeekNumber(int day) {
        this.dayOfWeekNumber = day;
    }

    // ----------------------------------------------------
    // Database alarm ids
    // ----------------------------------------------------

    public void addId(long id) {

        if (!ids.contains(id))
            ids.add(id);
    }

    public List<Long> getIds() {
        return Collections.unmodifiableList(ids);
    }

    // ----------------------------------------------------
    // Formatting
    // ----------------------------------------------------

    public String getStringTime() {

        int h = hour % 12;

        if (h == 0)
            h = 12;

        return String.format(
                Locale.getDefault(),
                "%d:%02d %s",
                h,
                minute,
                hour < 12 ? "AM" : "PM");
    }

    public String getFormattedDose() {

        if (doseQuantity == null)
            doseQuantity = "";

        if (doseUnit == null)
            doseUnit = "";

        return doseQuantity + " " + doseUnit;
    }

    // ----------------------------------------------------

    @Override
    public int compareTo(@NonNull MedicineAlarm o) {

        if (hour != o.hour)
            return Integer.compare(hour, o.hour);

        return Integer.compare(minute, o.minute);
    }
}