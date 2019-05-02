package com.herba.sdk.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ads.agile.AgileEventType;
import com.ads.agile.AgileTransaction;

public class ForthActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private AgileTransaction agileTransaction;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forth);

        agileTransaction = new AgileTransaction(getApplicationContext(), this, AgileEventType.AGILE_EVENT_TRANSACTION);
        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileTransaction.commitTransaction();
            }
        });

        findViewById(R.id.indexOutOfBound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double num1 = Double.parseDouble(editText.getText().toString());
               /* Object x[] = new String[3];
                x[0] = new Integer(0);*/
            }
        });

    }

}
