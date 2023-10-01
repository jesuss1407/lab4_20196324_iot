package com.example.lab4_iot_20196324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ingresar = findViewById(R.id.ingresar);
        ingresar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppActivity.class);
            startActivity(intent);
        });
    }
}