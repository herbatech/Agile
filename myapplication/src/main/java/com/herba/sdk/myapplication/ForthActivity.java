package com.herba.sdk.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ads.agile.AgileLog;
import com.ads.agile.AgileTransaction;

public class ForthActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private AgileTransaction agileTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forth);

        agileTransaction = new AgileTransaction(getApplicationContext(), this, "ag_transaction");
        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileTransaction.commitTransaction();
            }
        });

    }

}
