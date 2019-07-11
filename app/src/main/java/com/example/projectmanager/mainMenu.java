package com.example.projectmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class mainMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.site_options );


    }

    public void openService(View v){
        switch(v.getId()){
            case R.id.img1: startActivity( new Intent( getApplicationContext(), siteDetails.class ) ); break;
            case R.id.img2: startActivity( new Intent( getApplicationContext(), Attendance.class ) ); break;
            case R.id.img3: startActivity( new Intent( getApplicationContext(), MaterialsSupply.class ) ); break;
            case R.id.img4: startActivity( new Intent( getApplicationContext(), Materials.class ) ); break;
            case R.id.img5: startActivity( new Intent( getApplicationContext(), AddSiteMembers.class ) ); break;
            case R.id.img6: startActivity( new Intent( getApplicationContext(), Drawings.class ) ); break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent( getApplicationContext(), SitesList.class ) );
    }
}
