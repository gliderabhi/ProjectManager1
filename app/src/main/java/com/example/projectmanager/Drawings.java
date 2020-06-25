package com.example.projectmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanager.Classes.DrawingsDetails;
import com.example.projectmanager.Classes.ViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteID;
import static com.example.projectmanager.Classes.Constants.pdfLink;

public class Drawings extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sp;
    private FirebaseRecyclerAdapter<DrawingsDetails, ViewHolder> adapter1;
    ProgressDialog progress;
    boolean doubleBackToExitPressedOnce = false;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_drawings );
        sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );
         progress = new ProgressDialog( this);
        FloatingActionButton fab = findViewById(R.id.floatBtn);
        fab.setOnClickListener( view -> startActivity( new Intent( getApplicationContext(), addImage.class ) ) );

        recyclerView = findViewById( R.id.drawingList );
        pdfView= findViewById( R.id.pdfView );

        getList();
    }

    private void getList(){
        linearLayoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( linearLayoutManager );
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/SiteDrawings/" + sp.getString( SiteID, "" ) );

        Query query = newRef.orderByChild( "title" );


        FirebaseRecyclerOptions<DrawingsDetails> options = new FirebaseRecyclerOptions.Builder<DrawingsDetails>()
                .setQuery( query, DrawingsDetails.class)
                .build();

        adapter1 = new FirebaseRecyclerAdapter<DrawingsDetails, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DrawingsDetails drawingsDetails) {
                if (drawingsDetails.getPicUrl() != null) {
                    if (drawingsDetails.getType().matches( "application/pdf" )) {
                        holder.setImg( pdfLink );
                    } else {
                        holder.setImg( drawingsDetails.getPicUrl() );
                        //Log.e("msg",usr.getImageUrl());
                    }
                }
                holder.setTxtDesc( drawingsDetails.getTitle() + "\n"+ drawingsDetails.getType() );
                holder.setTxtTitle( drawingsDetails.getRemarks() );

                holder.root.setOnClickListener( v -> {
                    String type = drawingsDetails.getType();
                    if (type.matches( "application/pdf" )) {
                        progress.setMessage( "Wait Loading" );
                        progress.setProgressStyle( ProgressDialog.STYLE_SPINNER );
                        progress.setIndeterminate( true );
                        progress.show();
                        new RetrievePdfStream().execute(drawingsDetails.getPicUrl());

                      } else {
                        if (drawingsDetails.getPicUrl() != null) {
                            if (!drawingsDetails.getPicUrl().matches( "" )) {
                                //Picasso.get().load( drawingsDetails.getPicUrl() ).fit().into( drawwings );
                                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( drawingsDetails.getPicUrl() ) ) );
                                /* replace with your own uri */

                            }
                        }

                    }
                });

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
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener( task -> {
                    Toast.makeText(Drawings.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                    finish();
                } );
    }

    @Override
    public void onBackPressed() {
        pdfView.setVisibility( View.GONE );

        if (doubleBackToExitPressedOnce) {
            startActivity( new Intent( getApplicationContext(),mainMenu.class ) );
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed( () -> doubleBackToExitPressedOnce=false, 2000);
    }

    class RetrievePdfStream extends AsyncTask<String, Void, InputStream>{

        @Override
        protected InputStream doInBackground(String... strings) {

            InputStream inputStream =null;
            try{
                URL url= new URL( strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection() ;
                if( urlConnection.getResponseCode() == 200){
                    inputStream= new BufferedInputStream( urlConnection.getInputStream() );
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
           return  inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.setVisibility( View.VISIBLE );
            progress.dismiss();
            pdfView.fromStream( inputStream ).load();
        }
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
