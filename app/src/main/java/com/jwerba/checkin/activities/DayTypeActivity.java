package com.jwerba.checkin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.jwerba.checkin.model.DayType;
import com.jwerba.checkin.R;

public class DayTypeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_day_type);


        String dayTypeString = this.getIntent().getStringExtra("dayType");
        DayType dayType = Enum.valueOf(DayType.class, dayTypeString);
        setupEvents(dayType);
    }

    private void setupEvents(DayType dayType) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("dayType", dayType.getCode());
        returnIntent.putExtra("date", this.getIntent().getLongExtra("date", 0));
        setResult(Activity.RESULT_CANCELED, returnIntent);

        ImageButton imageButtonHoliday = (ImageButton)findViewById(R.id.imageButtonHoliday);
        imageButtonHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntent.putExtra("dayType", DayType.HOLIDAY_DAY.getCode());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        ImageButton imageButtonNone = (ImageButton)findViewById(R.id.imageButtonNone);
        imageButtonNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntent.putExtra("dayType", DayType.REGULAR_DAY.getCode());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        ImageButton imageButtonWFA = (ImageButton)findViewById(R.id.imageButtonWFA);
        imageButtonWFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntent.putExtra("dayType", DayType.WFA_DAY.getCode());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        ImageButton imageButtonOffice = (ImageButton)findViewById(R.id.imageButtonOffice);
        imageButtonOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntent.putExtra("dayType", DayType.OFFICE_DAY.getCode());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        ImageButton imageButtonOfficeVerified = (ImageButton)findViewById(R.id.imageButtonOfficeVerified);
        imageButtonOfficeVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntent.putExtra("dayType", DayType.VERIFIED_OFFICE_DAY.getCode());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        switch (dayType){
            case HOLIDAY_DAY:
                imageButtonHoliday.setPressed(true);
                break;
            case OFFICE_DAY:
                imageButtonOffice.setPressed(true);
                break;
            case REGULAR_DAY:
                imageButtonNone.setPressed(true);
                break;
            case WFA_DAY:
                imageButtonWFA.setPressed(true);
                break;
            case VERIFIED_OFFICE_DAY:
                imageButtonOfficeVerified.setPressed(true);
                break;
        }
    }
}