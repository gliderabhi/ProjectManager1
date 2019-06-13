package com.example.projectmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Materials extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_materials );
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
    }
}
