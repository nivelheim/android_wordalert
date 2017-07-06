package com.comp3617.alarmapp.alarmapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<QuestionFragment> fragments;
    private SharedPreferences pref;
    private int qCount;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private int index;
    private QuestionFragment frag;
    private Button setButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        setButton = (Button) findViewById(R.id.noButton);

        if (pref.contains(MainActivity.PREF)) {
            qCount = pref.getInt(MainActivity.SAVED_QNUM, 0);
            String temp2 = pref.getString(MainActivity.SAVED_RVAL, "");

            fragments = new ArrayList<>();

            for (int i = 0; i<qCount; i++) {
                frag = new QuestionFragment();
                fragments.add(frag);
            }

            index = 0;
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.qFragment, fragments.get(index), "fragment");
            ft.commit();

            if (index == 0) {
                setButton.setText("Deactivate");
            }
            else {
                setButton.setText("Next");
            }
        }
    }
    public void onClickStop(View v) {
        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
        intent.addCategory(AlarmService.TAG);
        stopService(intent);
    }

    public void onClickNext(View v) {
        QuestionFragment temp = (QuestionFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        if (index == (qCount-2)) {
            setButton.setText("Deactivate");
        }

        if (index < qCount-1) {
            if (temp.getStatus() == true) {
                index++;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.qFragment, fragments.get(index), "fragment");
                ft.commit();
            }

            else {
                Toast.makeText(AlarmActivity.this, "Please complete the question", Toast.LENGTH_LONG).show();
            }
        }

        else {
            if (temp.getStatus() == true) {
                /*
                Button button = (Button) findViewById(R.id.button3);
                button.setText("TURN OFF");
                */
                Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                intent.addCategory(AlarmService.TAG);
                stopService(intent);
            }

            else {
                Toast.makeText(AlarmActivity.this, "Complete question to turn off alarm", Toast.LENGTH_LONG).show();
            }

        }
    }

}
