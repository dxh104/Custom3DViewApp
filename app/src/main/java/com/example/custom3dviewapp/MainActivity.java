package com.example.custom3dviewapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.custom3dviewapp.widget.Custom3DView;

public class MainActivity extends AppCompatActivity {

    private Custom3DView custom3DView;
    private Button btnStart;
    private Button btnStop;

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
    }

    @Override
    protected void onDestroy() {
        custom3DView.stopAnimal();
        super.onDestroy();
    }

    private void initView() {
        custom3DView = (Custom3DView) findViewById(R.id.custom3DView);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
    }
}
