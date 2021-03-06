package com.herba.sdk.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ads.agile.AgileEventType;
import com.ads.agile.AgileTransaction;

public class FirstActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private AgileTransaction agileTransaction;
    EditText event_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        event_type=findViewById(R.id.event_type);

        agileTransaction = new AgileTransaction(getApplicationContext(), this, AgileEventType.AGILE_EVENT_TRANSACTION);
        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                agileTransaction.set("buyer_name", event_type.getText().toString());
                Intent i=new Intent(FirstActivity.this,ThirdActivity.class);
                startActivity(i);

            }
        });


        findViewById(R.id.indexOutOfBound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Double num1 = Double.parseDouble(editText.getText().toString());
                Object x[] = new String[3];
                x[0] = new Integer(0);
            }
        });
    }
}
