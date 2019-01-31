package com.herba.sdk.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ads.agile.Agile;

public class SecondActivity extends AppCompatActivity {

    private Agile agile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        agile = new Agile(getApplicationContext(), this);

        agile.addTransaction("HomeActivity_launched","blank",false);

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agile.addTransaction("Go-To-HomeActivity","blank",true);
                finish();
            }
        });
    }
}