package com.example.projectmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanager.Classes.Constants;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.UserSite;
import com.example.projectmanager.Classes.ViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.Priority;
import static com.example.projectmanager.Classes.Constants.SiteID;
import static com.example.projectmanager.Classes.Constants.SiteNAme;
import static com.example.projectmanager.Classes.Constants.UserID;
import static com.example.projectmanager.Classes.Constants.ongoing;

public class AddSiteMembers extends AppCompatActivity {

    private ArrayList<String> members;
    private ArrayList<SIteMembers> sitesMembers;
    private Button add;
    private ArrayAdapter<String> adapter;
    private ValueEventListener value;
    private DatabaseReference newRef;
    private SharedPreferences sp;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<SIteMembers, ViewHolder> adapter1;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_site_members );

        sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );
        //membersList = findViewById( R.id.membersList );
        add = findViewById( R.id.addMember );

        //get user name from firebase and add
        members = new ArrayList<>();
        sitesMembers = new ArrayList<>();
        members.add( "user name " );

        if (members.size() == 0) {
            members.add( "NO Users in the site " );
        }
        populateList();
        if (!Objects.requireNonNull( sp.getString( Priority, "" ) ).matches( "High" )) {
            sp.getString( Priority, "" ).matches( "Medium" );
        }

        add.setOnClickListener( v -> startActivity( new Intent( getApplicationContext(), search.class ) ) );
    }

    private void populateList() {

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (300 * scale + 0.5f);

        members.clear();
        sitesMembers.clear();
        newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + sp.getString( SiteID, "" ) );
        Query query = newRef.limitToLast( 30 );

        recyclerView = findViewById( R.id.membersList );
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( linearLayoutManager );
        FirebaseRecyclerOptions<SIteMembers> options =
                new FirebaseRecyclerOptions.Builder<SIteMembers>()
                        .setQuery( query, SIteMembers.class )
                        .build();
        adapter1 = new FirebaseRecyclerAdapter<SIteMembers, ViewHolder>( options ) {

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull SIteMembers model) {
                Log.e("msg","entered here ");
                Log.e("msg",model.getDesignation());
                holder.setImg( model.getImgUrl() );
                  holder.setTxtDesc( model.getDesignation() );
                  holder.setTxtTitle( model.getName() );

                  holder.root.setOnClickListener( new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          createDIalog( model );
                      }
                  } );
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from( parent.getContext() )
                         .inflate( R.layout.list_tems, parent, false );

                return new ViewHolder( view );
            }


        };

        recyclerView.setAdapter( adapter1 );

    }
    private void createDIalog(SIteMembers members){
        AlertDialog.Builder builder=new AlertDialog.Builder( AddSiteMembers.this );
        builder.setTitle( "User deatils are as follows , do u want to change his priority? " )
                .setMessage( "Name: "+members.getName()+"\n" +
                        "Designation: "+ members.getDesignation() )
                .setCancelable(false)
                .setPositiveButton( " ok ", (dialog, id) -> {
                    SharedPreferences sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE);
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + sp.getString( SiteID, "" ) + members.getId() );



                    //chnage the priority some way after creating view for the same
                    SIteMembers sIteMembers=new SIteMembers( members.getId(),members.getName(),"Low",members.getDesignation(),sp.getString( SiteNAme,"" ),ongoing,members.getImgUrl());
                    newRef.setValue( sIteMembers ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to change the user details, please try again "+ e.getMessage(),Toast.LENGTH_LONG ).show() );



                    UserSite siteUser=new UserSite( sp.getString( SiteID, "") , ongoing,sp.getString( SiteNAme,"" ));
                    DatabaseReference siteRef=FirebaseDatabase.getInstance().getReference("/Users/"+members.getId()+"/Sites Added/"+sp.getString( UserID, "")+"/");
                    siteRef.setValue( siteUser ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to change the user details, please try again "+ e.getMessage(),Toast.LENGTH_LONG ).show()   );

                    startActivity( new Intent( getApplicationContext(),AddSiteMembers.class ) );
                } )
                .setNegativeButton( "cancel", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();
                } );
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Do you want to add this person in your project? ");
        alert.show();
    }
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddSiteMembers.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),mainMenu.class ) );
    }

    @Override
    protected void onStart() {
        adapter1.startListening();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter1.stopListening();
        newRef.removeEventListener( value );
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
}
