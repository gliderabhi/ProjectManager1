package com.example.projectmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanager.Classes.Constants;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.Site;
import com.example.projectmanager.Classes.UserSite;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.projectmanager.Classes.Constants.ID;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.Priority;
import static com.example.projectmanager.Classes.Constants.SiteNAme;
import static com.example.projectmanager.Classes.Constants.ongoing;

public class AddSiteMembers extends AppCompatActivity {

    private ListView membersList;
    private ArrayList<String> members;
    private ArrayList<SIteMembers> sitesMembers;
    private Button add;
    private TextView site_name, siteLoc, sitestart, sitePriority;
    private ArrayAdapter<String> adapter;
    private ValueEventListener value;
    private DatabaseReference newRef;
    private SharedPreferences sp;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<SIteMembers, SearchHolders> adapter1;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_site_members );

        sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );
        //membersList = findViewById( R.id.membersList );
        add = findViewById( R.id.addMember );
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
        adapter = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_list_item_1, members );
        //membersList.setAdapter( adapter );

        /*membersList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDIalog( sitesMembers.get( position ) );
            }
        } );*/
        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), search.class ) );

            }
        } );
    }

    private void populateList() {

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (300 * scale + 0.5f);

        members.clear();
        sitesMembers.clear();
        newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + sp.getString( ID, "" ) );
        Query query = newRef.limitToLast( 30 );

        recyclerView = findViewById( R.id.membersList );
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( linearLayoutManager );
        FirebaseRecyclerOptions<SIteMembers> options =
                new FirebaseRecyclerOptions.Builder<SIteMembers>()
                        .setQuery( query, /*new SnapshotParser<SIteMembers>() {
                            @NonNull
                            @Override
                            public SIteMembers parseSnapshot( DataSnapshot snapshot) {
                                if (snapshot != null) {

                                    Log.e( "msg",snapshot.child( "name" ).getValue().toString());
                                    return new SIteMembers( snapshot.child( "name" ).getValue().toString(),
                                            snapshot.child( "designation" ).getValue().toString(),
                                            snapshot.child( "imgUrl" ).getValue().toString() );
                                }
                                else{
                                    return null;
                                }
                            }
                        } */SIteMembers.class )
                        .build();
        adapter1 = new FirebaseRecyclerAdapter<SIteMembers, SearchHolders>( options ) {

            @Override
            protected void onBindViewHolder(@NonNull SearchHolders holder, int position, @NonNull SIteMembers model) {
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
            public SearchHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from( parent.getContext() )
                        .inflate( R.layout.list_tems, parent, false );

                return new SearchHolders( view );
            }


        };

        recyclerView.setAdapter( adapter1 );



         /*   value= newRef.addValueEventListener( new ValueEventListener() {
             @Override
             public void onDataChange( DataSnapshot dataSnapshot) {
                 if (dataSnapshot != null) {
                     for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         SIteMembers site = snapshot.getValue( SIteMembers.class );
                         String memebr = "Name: " + site.getName() + "\n" +
                                 "Priority: " + site.getPriority();
                         members.add( memebr );
                         sitesMembers.add( site );

                         adapter.notifyDataSetChanged();
                     }
                     ListAdapter listadp = membersList.getAdapter();
                     if (listadp != null) {
                         int totalHeight = 0;
                         for (int i = 0; i < listadp.getCount(); i++) {
                             View listItem = listadp.getView( i, null, membersList );
                             listItem.measure( 0, 0 );
                             totalHeight += listItem.getMeasuredHeight();
                         }
                         ViewGroup.LayoutParams params = membersList.getLayoutParams();
                         params.height = totalHeight + (membersList.getDividerHeight() * (listadp.getCount() - 1));

                         if (totalHeight < pixels) {
                             membersList.setLayoutParams( params );
                             membersList.requestLayout();
                         } else {
                             params.height = pixels;
                             membersList.setLayoutParams( params );
                             membersList.requestLayout();

                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         } );*/


    }
    public class SearchHolders  extends RecyclerView.ViewHolder  {
        RelativeLayout root;
        TextView txtTitle;
        TextView txtDesc;
        ImageView img;

        public SearchHolders(View itemView) {
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
    private void createDIalog(SIteMembers members){
        AlertDialog.Builder builder=new AlertDialog.Builder( AddSiteMembers.this );
        builder.setTitle( "User deatils are as follows , do u want to change his priority? " )
                .setMessage( "Name: "+members.getName()+"\n" +
                        "Designation: "+ members.getDesignation() )
                .setCancelable(false)
                .setPositiveButton( " ok ", (dialog, id) -> {
                    SharedPreferences sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE);
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/Site Members/" + sp.getString( ID, "" ) + members.getId() );

                    //chnage the priority some way after creating view for the same
                    SIteMembers sIteMembers=new SIteMembers( members.getId(),members.getName(),"Low",members.getDesignation(),sp.getString( SiteNAme,"" ),ongoing,members.getImgUrl());
                    newRef.setValue( sIteMembers ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to change the user details, please try again "+ e.getMessage().toString(),Toast.LENGTH_LONG ).show() );

                    UserSite siteUser=new UserSite( sp.getString( Constants.ID, "") , ongoing,sp.getString( SiteNAme,"" ));
                    DatabaseReference siteRef=FirebaseDatabase.getInstance().getReference("/Users/"+members.getId()+"/Sites Added/"+sp.getString( Constants.ID, "")+"/");
                    siteRef.setValue( siteUser ).addOnFailureListener( e ->
                            Toast.makeText( getApplicationContext(),"Unable to change the user details, please try again "+ e.getMessage().toString(),Toast.LENGTH_LONG ).show()   );

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
                        Toast.makeText(AddSiteMembers.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
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
