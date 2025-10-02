package com.jwerba.checkin.activities;

import static com.jwerba.checkin.model.DayType.HOLIDAY_DAY;
import static com.jwerba.checkin.model.DayType.OFFICE_DAY;
import static com.jwerba.checkin.model.DayType.WFA_DAY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;
import com.google.android.material.navigation.NavigationView;
import com.jwerba.checkin.DetectionRunner;
import com.jwerba.checkin.geolocation.GeofenceBroadcastReceiver;
import com.jwerba.checkin.helpers.CustomNotificationBuilder;
import com.jwerba.checkin.storage.DataManager;
import com.jwerba.checkin.model.Day;
import com.jwerba.checkin.model.DayType;
import com.jwerba.checkin.R;
import com.jwerba.checkin.strategies.DetectionListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_DAY_TYPE_ACTIVITY = 1;
    private final String TAG = getClass().getTypeName();

    private CalendarView calendarView;
    private boolean initialized = false;
    private BroadcastReceiver broadcastReceiver;
    private boolean isReceiverRegistered = false;
    private String lastDetectedSSID = null;

    private final DetectionRunner runner = new DetectionRunner(this);
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DAY_TYPE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("dayType");
                long dateInMillis = data.getLongExtra("date", 0);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(dateInMillis);
                DayType r = Enum.valueOf(DayType.class, result);
                LocalDate d = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                Day day = new Day(d, r);
                DataManager.getInstance(this.getApplicationContext()).add(day);
                List<Day> days = DataManager.getInstance(this).getAll();
                initializeDisplayContent(days);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity.onCreate.start");
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);


        final Context ctx = this;
        runner.addListener(new DetectionListener() {
            @Override
            public void OnDetected(Class detectionStrategy, Map<String, String> context) {
                CustomNotificationBuilder notificationBuilder = new CustomNotificationBuilder(ctx);
                notificationBuilder.notify(this.getClass(), "bigText", "bigContentTitle", "summaryText","contentTitle", "contentText");
                notificationBuilder.noty();
            }
        });
        runner.start();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiateBroadcastReceiver();
        //startForegroundService(new Intent(this, MainService.class));

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });
        */
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeIfIsNot();
        List<Day> days = DataManager.getInstance(this).getAll();
        initializeDisplayContent(days);
        Log.i(TAG, "MainActivity.onCreate.end");
    }




    private void initializeIfIsNot() {
        if (!this.initialized) {
            this.calendarView = (CalendarView) findViewById(R.id.calendar);
            this.calendarView.setOnPreviousPageChangeListener(() -> updateNumbers());
            this.calendarView.setOnForwardPageChangeListener(() -> updateNumbers());
            this.calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
                @Override
                public void onClick(@NonNull CalendarDay calendarDay) {

                }
            });
            this.calendarView.setOnDayClickListener((EventDay eventDay) -> {
                DayType type = DayType.REGULAR_DAY;
                if (eventDay != null && CustomEventDay.class.isAssignableFrom(eventDay.getClass())) {
                    CustomEventDay ced = (CustomEventDay) eventDay;
                    type = ced.getType();
                }
                Log.i(TAG, String.valueOf(eventDay.getCalendar().get(Calendar.DAY_OF_MONTH)));
                Intent intent = new Intent(getApplicationContext(), DayTypeActivity.class);
                Bundle options = new Bundle();
                intent.putExtra("dayType", type.getCode());
                intent.putExtra("date", eventDay.getCalendar().getTimeInMillis());
                startActivityForResult(intent, REQUEST_CODE_DAY_TYPE_ACTIVITY, options);
            });
            Calendar c = null;
            c = Calendar.getInstance();
            try {
                this.calendarView.setDate(c);
            } catch (OutOfDateRangeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateNumbers() {
        Calendar c = this.calendarView.getCurrentPageDate();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        List<Day> days =  DataManager.getInstance(this.getApplicationContext()).get(year, month);
        Set<Day> holidays = getHolidays(days);
        Set<Day> office = getOfficeDays(days);
        Set<Day> wfa = getWFADays(days);
        Set<Day> nonWeekendHolidays = getMonWeekendHolidays(holidays.stream().collect(Collectors.toList()));
        Set<Day> nonWeekendWFA = getNonWeekendWFA(wfa.stream().collect(Collectors.toList()));
        int requiredDays = getWorkingDays(year, month);
        requiredDays = requiredDays - nonWeekendHolidays.size();

        showWifiName(lastDetectedSSID == null ? "Unknown" : lastDetectedSSID, Color.GRAY);

        TextView textView = null;

        textView = (TextView) findViewById(R.id.text_view_requiredDays);
        textView.setText("Required days: " + requiredDays);

        textView = (TextView) findViewById(R.id.text_view_holidays);
        textView.setText("Holidays: " + nonWeekendHolidays.size());

        textView = (TextView) findViewById(R.id.text_view_attendedDays);
        textView.setText("Office: " + (office.size() + wfa.size()));

        textView = (TextView) findViewById(R.id.text_view_daysToComplete);
        int remaining = requiredDays - (office.size() + nonWeekendWFA.size());
        textView.setText("Remaining: " + remaining);

        LocalDate now = LocalDate.now();
        textView = (TextView) findViewById(R.id.text_view_today);
        Optional<Day> found = office.stream().filter(d -> {
            return d.getDate().getYear() == now.getYear() && d.getDate().getMonthValue() == now.getMonthValue() && d.getDate().getDayOfMonth() == now.getDayOfMonth();
        }).findAny();
        textView.setText("Today: " + (found.isPresent() ? "DETECTED" : "NOT DETECTED YET"));

    }

    private void showWifiName(String ssid, int color) {
        TextView textView = (TextView) findViewById(R.id.text_view_currentWifi);
        textView.setText("Wifi: " + ssid);
    }

    private void setLoading(){
        TextView textView = (TextView) findViewById(R.id.text_view_requiredDays);
        textView.setText("Required days: Loading...");
        textView = (TextView) findViewById(R.id.text_view_holidays);
        textView.setText("Holidays: Loading...");
        textView = (TextView) findViewById(R.id.text_view_attendedDays);
        textView.setText("Office: Loading...");
        textView = (TextView) findViewById(R.id.text_view_daysToComplete);
        textView.setText("Remaining: Loading...");
        textView = (TextView) findViewById(R.id.text_view_today);
        textView.setText("Today: Loading...");
    }

    @SuppressLint("ResourceType")
    private void initializeDisplayContent(List<Day> days) {
        setLoading();
        try {
            Calendar c = this.calendarView.getCurrentPageDate();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            //this.calendarView.setEnabled(false);
            updateNumbers();

            Set<Day> holidays = getHolidays(days);
            Set<Day> office = getOfficeDays(days);
            Set<Day> wfa = getWFADays(days);

            List<EventDay> events = new ArrayList<>();
            for (Day d : holidays) {
                LocalDate date = d.getDate();
                Calendar cal = localDateToCalendar(date);
                events.add(new CustomEventDay(cal, HOLIDAY_DAY, R.drawable.nonworking));
            }
            for (Day d : office) {
                LocalDate date = d.getDate();
                Calendar cal = localDateToCalendar(date);
                events.add(new CustomEventDay(cal, OFFICE_DAY, R.drawable.office));
            }
            for (Day d : wfa) {
                LocalDate date = d.getDate();
                Calendar cal = localDateToCalendar(date);
                events.add(new CustomEventDay(cal, WFA_DAY, R.drawable.wfa));
            }

            CalendarView calendarView = findViewById(R.id.calendar);
            calendarView.setEvents(events);
            //calendarView.setEnabled(true);


            /*CompletableFuture.supplyAsync(() -> {

                return days;
            }).thenApply((days) -> {


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }

                    });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                return days;
            });*/
        } catch (Exception ex) {
            Log.e(TAG, "error on initializeDisplayContent", ex);
        }
    }

    private Set<Day> getNonWeekendWFA(List<Day> wfa) {
        return wfa.stream().filter(d -> d.getDate().getDayOfWeek() != DayOfWeek.SUNDAY && d.getDate().getDayOfWeek() != DayOfWeek.SATURDAY).collect(Collectors.toSet());
    }

    private Set<Day> getMonWeekendHolidays(List<Day> days) {
        return days.stream().filter(d -> d.getDate().getDayOfWeek() != DayOfWeek.SUNDAY && d.getDate().getDayOfWeek() != DayOfWeek.SATURDAY).collect(Collectors.toSet());
    }

    private Set<Day> getWFADays(List<Day> days) {
        return days.stream().filter(d -> d.getDayType() == WFA_DAY).collect(Collectors.toSet());
    }

    private Set<Day> getOfficeDays(List<Day> days) {
        return days.stream().filter(d -> d.getDayType() == OFFICE_DAY).collect(Collectors.toSet());
    }

    private Set<Day> getHolidays(List<Day> days) {
        return days.stream().filter(d -> d.getDayType() == HOLIDAY_DAY).collect(Collectors.toSet());
    }

    private Calendar localDateToCalendar(LocalDate date) {
        //Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        Calendar calendar = Calendar.getInstance();

        //cal.clear();
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        //cal.set(date.getYear(), date.getMonthValue() , date.getDayOfMonth());
        /*System.out.println("Current Calendar's Year: " + calendar.get(Calendar.YEAR));
        System.out.println("Current Calendar's Day: " + calendar.get(Calendar.MONTH));
        System.out.println("Current Calendar's Day: " + calendar.get(Calendar.DATE));
        System.out.println("Current Calendar's Day: " + calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println("Current MINUTE: " + calendar.get(Calendar.MINUTE));
        System.out.println("Current SECOND: " + calendar.get(Calendar.SECOND));*/
        return calendar;
    }

    private int getWorkingDays(int year, int month) {
        int workingDays = 0;
        LocalDate indexDate = LocalDate.of(year, month, 1);
        while (indexDate.getMonthValue() == month) {
            if (indexDate.getDayOfWeek() != DayOfWeek.SUNDAY && indexDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
                workingDays++;
            }
            indexDate = indexDate.plus(1, ChronoUnit.DAYS);
        }
        return (int) Math.round(workingDays * 0.6D);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //initializeIfIsNot();
        //initializeDisplayContent();
        //update settings values on side nav panel
        //updateSettingsValuesOnSideNav();
    }

    private void updateSettingsValuesOnSideNav() {
        //mNoteRecyclerAdapter.notifyDataSetChanged();
        //mCourseRecyclerAdapter.notifyDataSetChanged();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userDisplayNameTextView = headerView.findViewById(R.id.user_display_name);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);
        SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String userDisplayName = pref.getString("pref_user_display", null);
        String userEmail = pref.getString("pref_user_ssid", null);

        userDisplayNameTextView.setText(userDisplayName);
        userEmailTextView.setText(userEmail);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            //displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();
        } else if (id == R.id.nav_share) {
            handleEvent("Share");
        } else if (id == R.id.nav_send) {
            handleEvent("Send");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleEvent(String msg) {
        //View view = findViewById(R.id.recyclerView);
        //Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    private void displayCourses() {
        /*
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(layoutManager );

        recycler.setAdapter(mCourseRecyclerAdapter);
        */
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_courses).setChecked(true);
    }


    @Override
    protected void onPause() {
        if (isReceiverRegistered && this.broadcastReceiver != null) {
            this.unregisterReceiver(this.broadcastReceiver);
            isReceiverRegistered = false;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isReceiverRegistered && this.broadcastReceiver != null) {
            this.unregisterReceiver(this.broadcastReceiver);
            isReceiverRegistered = false;
        }
        super.onDestroy();
    }


    private class ActivityRunnable implements Runnable{

        Consumer<MainActivity> function;
        MainActivity target;
        public ActivityRunnable(MainActivity target, Consumer<MainActivity> function){
            this.function = function;
            this.target = target;
        }

        @Override
        public void run() {
            this.function.accept(this.target);
        }
    }




     /*
    private void instantiateBroadcastReceiver() {
        //Thread thread = new Thread(new ActivityRunnable(this, (MainActivity activity)->{}));
        //thread.start();
        doInstantiateBroadcastReceiver(this);
    }

    private void doInstantiateBroadcastReceiver(MainActivity activity){
        if (activity.broadcastReceiver == null) {
            activity.broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(String.valueOf(Action.WIFI_DETECTED))) {
                        try {
                            String ssid = intent.getStringExtra("com.jwerba.attendance.SSID");
                            OnReceiveWifiDetected(ssid);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getCause().getMessage());
                        }
                    }
                }
            };
        }
        if (!activity.isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(String.valueOf(Action.WIFI_DETECTED));
            activity.registerReceiver(activity.broadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }


    protected void OnReceiveWifiDetected(String ssid) {
        lastDetectedSSID = ssid;
        initializeDisplayContent();
    }*/

}



