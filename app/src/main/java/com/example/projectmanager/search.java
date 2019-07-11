package com.example.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.UserDetails;
import com.example.projectmanager.Classes.UserSite;
import com.example.projectmanager.Classes.ViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteID;
import static com.example.projectmanager.Classes.Constants.SiteNAme;
import static com.example.projectmanager.Classes.Constants.hideSoftKeyboard;
import static com.example.projectmanager.Classes.Constants.ongoing;

public class search extends AppCompatActivity {

    private EditText searchText;
    private TextView resultText;
    private ArrayList<UserDetails> usr;
    private FirebaseRecyclerAdapter<UserDetails,ViewHolder> adapter1;
    private RecyclerView recyclerView;
    private SharedPreferences sp;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search );
        hideSoftKeyboard(this);

        searchText=findViewById( R.id.searchBar );
        resultText =findViewById( R.id.noResultText );
        resultText.setVisibility( View.GONE );
        usr=new ArrayList<>(  );

        sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE);

        generateList( "" );

        //dynamic searching of subwords to match in firebase
        searchText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String text = mEdit.toString();
                //add firebase query to the field to generate the result list
                generateList(text);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

getSiteName();
    }

    private Query query;
    private void generateList(String text) {
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");
           //check this query properly , not working to best effect , giving random results
            query =
                    users.orderByChild( "name" )
                            .startAt( text.toUpperCase() )
                            .endAt( text.toLowerCase()+ "\uf8ff" );

            if(adapter1!=null){
                adapter1.stopListening();
            }
            getList();
    }

    private void getList() {
        resultText.setVisibility( View.GONE );
        recyclerView= findViewById( R.id.recyclerSearchProfiles );
        recyclerView.setVisibility( View.VISIBLE );
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<UserDetails> options =
                new FirebaseRecyclerOptions.Builder<UserDetails>()
                        .setQuery( query, UserDetails.class)
                        .build();

        adapter1 = new FirebaseRecyclerAdapter<UserDetails, ViewHolder>( options ) {


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from( parent.getContext() )
                        .inflate( R.layout.list_tems, parent, false );

                return new ViewHolder( view );
            }


            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UserDetails usr) {

                    if (usr.getImageUrl() != null) {
                        holder.setImg( usr.getImageUrl() );
                        //Log.e("msg",usr.getImageUrl());
                    }
                    holder.setTxtDesc( usr.getTitle() );
                    holder.setTxtTitle( usr.getName() );

                    holder.img.setOnClickListener( v -> createDIalog( usr ) );
                    holder.root.setOnClickListener( v -> createDIalog( usr ) );

            }


        };

        recyclerView.setAdapter( adapter1 );
        adapter1.startListening();
    }


    //ask whether to add the user to the site
    private void createDIalog(UserDetails userDetails){
        AlertDialog.Builder builder=new AlertDialog.Builder( search.this );
        builder.setTitle( "User deatils are as follows , do u want to add him? " )
                .setMessage( "Name: "+userDetails.getName()+"\n" +
                        "Designation: "+ userDetails.getTitle() +"\n" +
                        userDetails.getId())
                .setCancelable(false)
                .setPositiveButton( " ok ", (dialog, id) -> {


                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + sp.getString( SiteID, "" ) +"/"+ userDetails.getId() );


                    //define the priority some way after creating view for the same
                    //adding the user as site member
                    SIteMembers sIteMembers=new SIteMembers( userDetails.getId(),userDetails.getName(),"Medium",userDetails.getTitle(),sp.getString( SiteNAme, "" ),ongoing,userDetails.getImageUrl());
                    newRef.setValue( sIteMembers ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to add the user please try again "+ e.getMessage(),Toast.LENGTH_LONG ).show() );



                    //add the site to the added persons database also
                    DatabaseReference addedRef = FirebaseDatabase.getInstance().getReference( "/Sites/" + userDetails.getId() +"/Sites Added/"+ sp.getString( SiteID,"" )+"/" );
                    UserSite userSite=new UserSite(sp.getString( SiteID, "" ) , ongoing, getSiteName());
                    addedRef.setValue( userSite );
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

    private String getSiteName(){

       String name = sp.getString( SiteID , "");
        int i= name.indexOf( "_" );
        String ret= name.substring( i +1);
        Log.e( "msg",ret );
        return  ret;
    }
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener( task -> {
                    Toast.makeText(search.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                    finish();
                } );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter( null);
        adapter1.stopListening();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter1.startListening();
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
