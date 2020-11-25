package com.example.custom3dviewapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.custom3dviewapp.widget.Custom3DView2;

public class MainActivity extends AppCompatActivity {

    private Custom3DView2 custom3DView;
    private Button btnStart;
    private Button btnStop;
    private Button btnSpreadOut;
    private Button btnShrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom3DView.startAnimal();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom3DView.stopAnimal();
            }
        });
        btnSpreadOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom3DView.spreadOut(300);
            }
        });
        btnShrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom3DView.shrink();
            }
        });
    }

    @Override
    protected void onDestroy() {
        custom3DView.stopAnimal();
        super.onDestroy();
    }

    private void initView() {
        custom3DView = (Custom3DView2) findViewById(R.id.custom3DView);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnSpreadOut = (Button) findViewById(R.id.btn_spreadOut);
        btnShrink = (Button) findViewById(R.id.btn_shrink);
    }
}
