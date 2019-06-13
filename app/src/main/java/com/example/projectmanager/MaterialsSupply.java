package com.example.projectmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MaterialsSupply extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_materials_supply );
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
    }
}
