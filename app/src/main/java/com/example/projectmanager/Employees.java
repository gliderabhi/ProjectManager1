package com.example.projectmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Employees extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_employees );
    }
    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
    }
}
