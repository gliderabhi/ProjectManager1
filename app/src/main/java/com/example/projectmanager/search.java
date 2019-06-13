package com.example.projectmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanager.Classes.Constants;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.UserDetails;
import com.example.projectmanager.Classes.UserSite;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import static com.example.projectmanager.Classes.Constants.ID;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteNAme;
import static com.example.projectmanager.Classes.Constants.ongoing;

public class search extends AppCompatActivity {

    private ListView result;
    private ArrayList<String> results;
    private EditText searchText;
    private String member;
    private ArrayAdapter<String > adapter;
    private ArrayList<UserDetails> usr;
    private FirebaseRecyclerAdapter<UserDetails,ViewHolder> adapter1;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search );

        result=findViewById( R.id. searchResult);
        searchText=findViewById( R.id.searchBar );
        result.setVisibility( View.VISIBLE );
        results=new ArrayList<>(  );
        usr=new ArrayList<>(  );
        results.add( "" );

        generateList( "" );
         adapter =new ArrayAdapter< >( getApplicationContext(),android.R.layout.simple_list_item_1,results );
        result.setAdapter( adapter );

        //dynamic searching of subwords to match in firebase
        searchText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String text = mEdit.toString();
                //add firebase query to the field to generate the result list
                results.clear();
                generateList(text);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

//after list generatd and on click event
        result.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDIalog(usr.get( position ));

            }
        } );

    }

    private Query query;
    private void generateList(String text) {
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");
        query=
               users.orderByChild("name")
               .startAt(text)
               .endAt(text+"\uf8ff");

        getList();
        //query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getList() {
        recyclerView= findViewById( R.id.recyclerSearchProfiles );
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<UserDetails> options =
                new FirebaseRecyclerOptions.Builder<UserDetails>()
                        .setQuery( query, /*new SnapshotParser<UserDetails>() {
                            @NonNull
                            @Override
                            public UserDetails parseSnapshot( DataSnapshot snapshot) {
                                if (snapshot != null) {

                                    //Log.e( "msg","items here " );
                                    return new UserDetails( snapshot.child( "name" ).getValue().toString(),
                                            snapshot.child( "title" ).getValue().toString(),
                                            snapshot.child( "imageUrl" ).getValue().toString() );
                                }
                                else{
                                    return null;
                                }
                            }
                        } */     UserDetails.class)
                        .build();
        adapter1 = new FirebaseRecyclerAdapter<UserDetails, ViewHolder>( options ) {


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from( parent.getContext() )
                        .inflate( R.layout.list_tems, parent, false );

                return new ViewHolder( view );
            }


            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, UserDetails usr) {
                holder.setImg( usr.getImageUrl() );
                holder.setTxtDesc( usr.getTitle() );
                holder.setTxtTitle( usr.getName() );

                holder.img.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createDIalog( usr );
                    }
                } );
                holder.root.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createDIalog( usr );
                    }
                } );
            }


        };

        recyclerView.setAdapter( adapter1 );

    }

    //setting image of profile as circular
    public class ViewHolder  extends RecyclerView.ViewHolder  {
        RelativeLayout root;
        TextView txtTitle;
        TextView txtDesc;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById( R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
            img=itemView.findViewById( R.id.image );
            //Log.e( "msg","initation" );

        }

        public void setImg(String url){

            int pxw = (int) (80 * Resources.getSystem().getDisplayMetrics().density);
            int pxh = (int) (80 * Resources.getSystem().getDisplayMetrics().density);

            Picasso.get().load(url ).resize(pxw,pxh).transform(new Constants.CircleTransform()).into( img, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            } );

        }
        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }


        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
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
                    SharedPreferences sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE);
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + sp.getString( ID, "" ) +"/"+ userDetails.getId() );
                    //define the priority some way after creating view for the same
                    //adding the user as site member
                    SIteMembers sIteMembers=new SIteMembers( userDetails.getId(),userDetails.getName(),"Medium",userDetails.getTitle(),sp.getString( SiteNAme, "" ),ongoing,userDetails.getImageUrl());
                    newRef.setValue( sIteMembers ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to add the user please try again "+ e.getMessage().toString(),Toast.LENGTH_LONG ).show() );

                    //add the site to the added persons database also
                    DatabaseReference addedRef = FirebaseDatabase.getInstance().getReference( "/Users/" + userDetails.getId() +"/Sites Added/"+ sp.getString( ID,"" )+"/"+userDetails.getId() );
                    UserSite userSite=new UserSite( userDetails.getId(), ongoing,userDetails.getName() );
                    addedRef.setValue( userSite );
                    startActivity( new Intent( getApplicationContext(),AddSiteMembers.class ) );
                } )
                .setNegativeButton( "cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
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
                        Toast.makeText(search.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
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
