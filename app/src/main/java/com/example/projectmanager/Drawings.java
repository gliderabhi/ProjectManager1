package com.example.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.projectmanager.Classes.DrawingsDetails;
import com.example.projectmanager.Classes.ViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import static com.example.projectmanager.Classes.Constants.ID;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;

public class Drawings extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView drawwings;
    private TextView title,desc;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sp;
    private FirebaseRecyclerAdapter<DrawingsDetails, ViewHolder> adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_drawings );
        sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );

        FloatingActionButton fab = findViewById(R.id.floatBtn);
        fab.setOnClickListener( view -> startActivity( new Intent( getApplicationContext(), addImage.class ) ) );

        title = findViewById( R.id.titleText );
        desc = findViewById( R.id.DescriptionText );
        recyclerView = findViewById( R.id.drawingList );
        drawwings= findViewById( R.id.drawingImg );

        getList();
    }

    private void getList(){
        linearLayoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( linearLayoutManager );
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/SiteDrawings/" + sp.getString( ID, "" ) );

        Query query = newRef.orderByChild( "title" );


        FirebaseRecyclerOptions<DrawingsDetails> options = new FirebaseRecyclerOptions.Builder<DrawingsDetails>()
                .setQuery( query, DrawingsDetails.class)
                .build();

        adapter1 = new FirebaseRecyclerAdapter<DrawingsDetails, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DrawingsDetails drawingsDetails) {
                if (drawingsDetails.getPicUrl() != null) {
                    holder.setImg( drawingsDetails.getPicUrl() );
                    //Log.e("msg",usr.getImageUrl());
                }
                holder.setTxtDesc( drawingsDetails.getTitle() + "."+ drawingsDetails.getType() );
                holder.setTxtTitle( drawingsDetails.getRemarks() );

                holder.root.setOnClickListener( v -> {
                    if(drawingsDetails.getPicUrl()!=null){
                        if(!drawingsDetails.getPicUrl().matches( "" )){
                            Picasso.get().load( drawingsDetails.getPicUrl() ).fit().into( drawwings );
                        }
                    }
                    title.setText( drawingsDetails.getTitle() );
                    desc.setText( drawingsDetails.getRemarks() );

                    drawwings.setVisibility( View.VISIBLE );
                    title.setVisibility( View.VISIBLE );
                    desc.setVisibility( View.VISIBLE );
                } );

                holder.root.setOnLongClickListener( v -> false );
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from( viewGroup.getContext() )
                        .inflate( R.layout.list_tems, viewGroup, false );

                return new ViewHolder( view );
            }
        };
      recyclerView.setAdapter( adapter1 );
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener( task -> {
                    Toast.makeText(Drawings.this, "User Signed Out", Toast.LENGTH_SHORT).show();
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
