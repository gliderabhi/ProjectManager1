package com.example.projectmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanager.Classes.Constants;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.Site;
import com.example.projectmanager.Classes.Suppliers;
import com.example.projectmanager.Classes.UserSite;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.projectmanager.Classes.Constants.ID;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.Priority;
import static com.example.projectmanager.Classes.Constants.SiteNAme;

public class SitesList extends Activity {

    private ListView ongoing,completed;
    private Button adder;
    private FirebaseUser user;
    ArrayAdapter<String> adapter1,adapter2;
    private DatabaseReference dataRef,SiteRef;
    private ArrayList<String> onGoing,complted;
    private TextView one,two;
    private String id;
    private int height1,height2;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sites_list );
        SharedPreferences sp=getSharedPreferences( PACKAGE_NAME,Context.MODE_PRIVATE );
        SharedPreferences.Editor editor=sp.edit();

        user= FirebaseAuth.getInstance().getCurrentUser();
        dataRef= FirebaseDatabase.getInstance().getReference();
        SiteRef = dataRef.child("/Site Details/"+ user.getUid());

        ongoing=findViewById( R.id.onGoingList );
        completed=findViewById( R.id. completedList);
        one=findViewById( R.id.infoOnItemsGoing );
        two=findViewById( R.id.infoOnNOItemComplted );
        adder=findViewById( R.id.newSiteAdder );

        adder.setOnClickListener( v -> startActivity( new Intent( getApplicationContext(),NewSite.class ) ) );

        onGoing=new ArrayList<>(  );
        complted=new ArrayList<>(  );

        adapter1 = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_list_item_1, onGoing );
        adapter2 = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_list_item_1, complted );

        ongoing.setAdapter( adapter1 );
        completed.setAdapter( adapter2 );


        ongoing.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Toast.makeText( getApplicationContext(),value,Toast.LENGTH_SHORT ).show();
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
            }
        });

        populateLists();
    }

    private ProgressDialog progress;
    private void populateLists() {
        progress = new ProgressDialog(this);
        progress.setMessage("Updating List");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (300 * scale + 0.5f);
        onGoing.clear();
        complted.clear();
        height1=0;
        height2=0;

        SiteRef = dataRef.child( "/Users/" + user.getUid() + "/Sites Added/" );
        SiteRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserSite usr = dataSnapshot1.getValue( UserSite.class );
                    String site_name = usr.getName();
                    id = usr.getId();
                    String prgs = usr.getProgress();

                    //Log.e( "msg", prgs + " " + site_name );
                    if (prgs.matches( Constants.ongoing )) {
                        onGoing.add( onGoing.size()+1+": " + site_name );
                    } else if (prgs.matches( Constants.Completed )) {
                        complted.add( complted.size()+1+": " + site_name );
                    }
                }
                if (onGoing.size() != 0) {

                    ListAdapter listadp = ongoing.getAdapter();
                    if (listadp != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < listadp.getCount(); i++) {
                            View listItem = listadp.getView( i, null, ongoing );
                            listItem.measure( 0, 0 );
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = ongoing.getLayoutParams();
                        params.height = totalHeight + (ongoing.getDividerHeight() * (listadp.getCount() - 1));

                        if (totalHeight < pixels) {
                            ongoing.setLayoutParams( params );
                            ongoing.requestLayout();
                        } else {
                            params.height = pixels;
                            ongoing.setLayoutParams( params );
                            ongoing.requestLayout();

                        }
                        ongoing.setVisibility( View.VISIBLE );
                        one.setVisibility( View.GONE );
                        ongoing.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        } );
                    }
                }else {
                        ongoing.setVisibility( View.GONE );
                        one.setVisibility( View.VISIBLE );
                    }

                    if (complted.size() != 0) {
                       ListAdapter  listadp = completed.getAdapter();
                        if (listadp != null) {
                            int totalHeight = 0;
                            for (int i = 0; i < listadp.getCount(); i++) {
                                View listItem = listadp.getView( i, null, completed );
                                listItem.measure( 0, 0 );
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = completed.getLayoutParams();
                            params.height = totalHeight + (completed.getDividerHeight() * (listadp.getCount() - 1));

                            if (totalHeight < pixels) {
                                completed.setLayoutParams( params );
                                completed.requestLayout();
                            } else {
                                params.height = pixels;

                                completed.setLayoutParams( params );
                                completed.requestLayout();
                            }

                            completed.setVisibility( View.VISIBLE );
                            two.setVisibility( View.GONE );
                            //what to do when item is clicked yet to know

                        }
                    }else {
                        completed.setVisibility( View.GONE );
                        two.setVisibility( View.VISIBLE );
                    }

                    progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText( getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG ).show();
                progress.dismiss();
            }
        } );

        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        progress.dismiss();
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
    }



    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SitesList.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       // query.removeEventListener( val );
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