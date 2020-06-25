package com.example.projectmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectmanager.Classes.Constants;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.Site;
import com.example.projectmanager.Classes.UserDetails;
import com.example.projectmanager.Classes.UserSite;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import static com.example.projectmanager.Classes.Constants.Creator;
import static com.example.projectmanager.Classes.Constants.KEY_LATITUDE;
import static com.example.projectmanager.Classes.Constants.KEY_LONGITUDE;
import static com.example.projectmanager.Classes.Constants.Locale;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteID;
import static com.example.projectmanager.Classes.Constants.SiteNAme;
import static com.example.projectmanager.Classes.Constants.hideSoftKeyboard;
import static com.example.projectmanager.Classes.Constants.mapStr1;
import static com.example.projectmanager.Classes.Constants.mapStr2;
import static com.example.projectmanager.Classes.Constants.ongoing;
import static com.example.projectmanager.Classes.Constants.priority;

public class NewSite extends AppCompatActivity {

    private EditText siteName,siteCLient;
    private Spinner sitePriority;
    private ImageView proceed,cancel;
    private String startDate,site_name,site_loc,site_client,sitePrior;
    private CalendarView calendarView;
    private FirebaseUser user;
    private DatabaseReference dataRef,SiteRef;
    private String id;
    private EditText loc;
    private TextView siteLoc, dateset;
    private ImageView mapButton;
    private String imgurl;
    private SharedPreferences pref;
    private Button openCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_site );

        initialize();


        pref = getApplicationContext().getSharedPreferences( Constants.Pref, 0); // 0 - for private mode
        site_loc=pref.getString( KEY_LATITUDE,"0" ) + "/"+ pref.getString( KEY_LONGITUDE,"0" );
        siteLoc.setText( pref.getString( KEY_LATITUDE,"0" ) + "/"+ pref.getString( KEY_LONGITUDE,"0" ) );
        if(!pref.getString( Locale,"" ).matches( "" )){
            siteLoc.setText( pref.getString( Locale,"" ) );
        }
        user= FirebaseAuth.getInstance().getCurrentUser();
        dataRef= FirebaseDatabase.getInstance().getReference();
        SiteRef = dataRef.child( "/Users/" );
        Query phoneQuery = SiteRef.orderByChild( "id" ).equalTo( user.getUid() );
        phoneQuery.addListenerForSingleValueEvent( new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                                           if (dataSnapshot != null) {
                                                               for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                                                   UserDetails usr = singleSnapshot.getValue( UserDetails.class );

                                                                   if (usr != null) {

                                                                      // siteCLient.setText( usr.getName() );
                                                                   }
                                                               }
                                                           }
                                                       }

                                                       @Override
                                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                                       }
                                                   });



        long date=calendarView.getDate();

        ArrayAdapter<String > adapter=new ArrayAdapter<String>( getApplicationContext(),android.R.layout.simple_spinner_item, priority );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        sitePriority.setAdapter( adapter );

        sitePriority.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sitePrior=priority[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               sitePrior="";
            }
        } );
        cancel.setOnClickListener( v -> startActivity( new Intent( getApplicationContext(),SitesList.class ) ) );
        proceed.setOnClickListener( v -> {

            if(startDate!=null ){
                if(!sitePrior.matches( "" ) || sitePrior!=null){

                    site_name=siteName.getText().toString().trim();
                    //site_loc=siteLoc.getText().toString().trim();
                    site_client=siteCLient.getText().toString().trim();
                    id= startDate+"_"+site_name;
                    Log.e( "msg",site_loc );
                    if(site_loc.matches( "00" )){
                        AlertDialog.Builder builder=new AlertDialog.Builder( NewSite.this );
                        builder.setMessage( "Please add the site location " )
                                .setPositiveButton( "Yes", (dialog, which) -> {
                                    startActivity( new Intent( getApplicationContext(),MapsActivity.class ) );
                                    site_loc=pref.getString( KEY_LATITUDE,"0" ) + "/"+ pref.getString( KEY_LONGITUDE,"0" );
                                } )
                                .setNegativeButton( "No", (dialog, which) -> {
                                    site_loc=site_loc;
                                } );
                        builder.create();
                        builder.show();
                    }
                    Site site=new Site( site_name,site_client,site_loc,sitePrior,startDate,"End date can be specified its optional", site_client, Constants.ongoing ,id);

                    SiteRef = dataRef.child("/Site Details/"+id+"/");
                    SiteRef.setValue( site ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    } );
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + id+"/" + user.getUid() );
                    SIteMembers members=new SIteMembers( user.getUid(),site_client,"High" ,Creator,site_name,ongoing,getImageUrl(id));
                    newRef.setValue( members ).addOnFailureListener( e -> e.printStackTrace() );

                    UserSite siteUser=new UserSite(id ,ongoing,site_name);
                    DatabaseReference siteRef=FirebaseDatabase.getInstance().getReference("/Sites/"+members.getId()+"/Sites Added/"+id+"/");
                    siteRef.setValue( siteUser ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to change the user details, please try again "+ e.getMessage(),Toast.LENGTH_LONG ).show()   );

                    startActivity(new Intent( getApplicationContext(),AddSiteMembers.class ) );
                }else{
                    createDIalog();
                }
            }else {
                createDIalog();
            }

            SharedPreferences sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString( SiteNAme,site_name  );
            editor.putString( SiteID,id );
            editor.apply();
        } );

    }

    private void initialize() {
        hideSoftKeyboard(this);
        siteCLient=findViewById( R.id. client_name);
        siteLoc=findViewById( R.id.locationSite );
        mapButton=findViewById( R.id.mapSite );
        siteName=findViewById( R.id. site_name);
        sitePriority=findViewById( R.id.prioritySpinner );
        calendarView=findViewById( R.id. calender);
        proceed=findViewById( R.id.proceed );
        cancel=findViewById( R.id.cancel );
        loc=findViewById( R.id.locationSites );
        openCal = findViewById(R.id.openCal);
        calendarView.setVisibility(View.GONE);
        dateset = findViewById(R.id.DateSet);
        dateset.setVisibility(View.GONE);

        openCal.setOnClickListener(view -> {
            calendarView.setVisibility(View.VISIBLE);
            Log.e("msg", "tried opening calender");
        });
        mapButton.setOnClickListener(v -> startActivity( new Intent( getApplicationContext(), MapsActivity.class ) ));
        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {
                    startDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                    calendarView.setVisibility(View.GONE);
                    openCal.setVisibility(View.GONE);
                    dateset.setText(startDate);
                    dateset.setVisibility(View.VISIBLE);
                });

        loc.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input=s.toString();
                String link = mapStr1+input+mapStr2;
                Log.e("msg",link);

            }
        } );
    }

    private String getImageUrl(String Id) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users/");
        Query q= ref.orderByChild( "id" ).equalTo(Id);
        q.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            UserDetails s = dataSnapshot1.getValue( UserDetails.class );
                            assert s != null;
                            if (s.getImageUrl() != null) {
                                imgurl = s.getImageUrl();
                            }else{
                                imgurl="";
                            }
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                imgurl="";

            }
        } );
        return  imgurl;
    }

    private void createDIalog(){
        AlertDialog.Builder builder=new AlertDialog.Builder( NewSite.this );
        builder.setMessage( "Please select the date and priority " )
                .setCancelable(false)
                .setPositiveButton( " ok ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton( "cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Set Date and priority");
        alert.show();
    }
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(NewSite.this, "User Signed Out", Toast.LENGTH_SHORT).show();
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
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor=pref.edit();
        editor.putString( KEY_LATITUDE,"0" );
        editor.putString( KEY_LONGITUDE,"0" );
        editor.apply();
        super.onDestroy();
    }
}
