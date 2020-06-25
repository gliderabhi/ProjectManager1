package com.example.projectmanager;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectmanager.Classes.Suppliers;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.projectmanager.Classes.Constants.activities;

public class SelectionPanel extends AppCompatActivity {

    ListView list;
    private boolean doubleBackToExitPressedOnce= false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_selection_panel );
       list=findViewById( R.id.listActivities );

       list.setAdapter( new ArrayAdapter<String>( this,android.R.layout.simple_list_item_1,activities ) );
       list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               switch(position){
                   case 0: startActivity(new Intent(getApplicationContext(),SitesList.class));break;
                   case 1: startActivity(new Intent(getApplicationContext(),Employees.class));break;
                   case 2: startActivity(new Intent(getApplicationContext(),Attendance.class));break;
                   case 3: startActivity(new Intent(getApplicationContext(),Materials.class));break;
                   case 4: startActivity(new Intent(getApplicationContext(), Suppliers.class));break;
                   case 5: startActivity(new Intent(getApplicationContext(),MaterialsSupply.class));break;
               }
           }
       } );
    }
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SelectionPanel.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        startActivity( new Intent( getApplicationContext(),MainActivity.class ) );
                        finish();
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent( getApplicationContext(),Profile.class ));
                return true;
            case R.id.settings:
                //add the settings to file
                return true;
            case R.id.signout:
                signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finishAndRemoveTask();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed( () -> doubleBackToExitPressedOnce=false, 2000);
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
        super.onBackPressed();
    }
}
