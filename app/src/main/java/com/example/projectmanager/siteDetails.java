package com.example.projectmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.projectmanager.Classes.Site;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.projectmanager.Classes.Constants.ID;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;

public class siteDetails extends AppCompatActivity {

    private SharedPreferences sp;
    private TextView site_name,siteLoc,sitestart,sitePriority;
    private DatabaseReference newRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_site_details );

        sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );
        //membersList = findViewById( R.id.membersList );
        //add = findViewById( R.id.addMember );
        site_name = findViewById( R.id.name );
        siteLoc = findViewById( R.id.Location );
        sitestart = findViewById( R.id.startDate );
        sitePriority = findViewById( R.id.Priority );

        //get the details of the site in the fields
        newRef = FirebaseDatabase.getInstance().getReference( "/Site Details/" + sp.getString( ID, "" ) + "/" );
        newRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Site site = dataSnapshot.getValue( Site.class );
                    site_name.setText( site.getName() );
                    siteLoc.setText( site.getSiteLoc() );
                    sitestart.setText( site.getStart() );
                    sitePriority.setText( site.getPriority() );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
