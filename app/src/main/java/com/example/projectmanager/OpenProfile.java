package com.example.projectmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.projectmanager.Classes.UserDetails;
import com.example.projectmanager.Classes.Constants;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

public class OpenProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profilePic;
    private EditText name,address,mobileNO;
    private Spinner orglist,desigList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String desigTit;
    private int fillCount;
    private ProgressDialog progress;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private Uri mImageUri;
    private String picUrl;
    private String mobile_nO,nme,addres;
    private UserDetails usr;

    private DatabaseReference mDatabaseRef,newRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_details );
        initialise();

        ArrayAdapter<String> adapter=new ArrayAdapter<>( this,android.R.layout.simple_spinner_item, Constants.org );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        orglist.setAdapter( adapter );

        orglist.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putString( Constants.Org,Constants.org[position] );
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editor.putString( Constants.Designation, "None" );
                editor.apply();
            }
        } );
        ArrayAdapter<String> adapter2=new ArrayAdapter<>( this,android.R.layout.simple_spinner_item,Constants.desig);
        adapter2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        desigList.setAdapter( adapter2 );

        desigList.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                desigTit=Constants.desig[position];
                editor.putString( Constants.Designation,Constants.desig[position] );
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editor.putString( Constants.Org,"No" );
                editor.apply();
            }
        } );

        //action buttons
        ImageView cancel = findViewById( R.id.cancel );
        ImageView upload = findViewById( R.id.proceed );

        cancel.setOnClickListener( v -> {

        } );

        upload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fillCount==1){
                    startActivity( new Intent( getApplicationContext(),search.class ) );

                }else {
                    uploadImage();
                }
            }
        } );
    }

    private void uploadImage() {
        mStorageRef = mStorageRef.child( "Users/ProfilePics/" + user.getUid() );
        progress = new ProgressDialog( this );
        progress.setMessage( "Uploading Image" );
        progress.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        progress.setIndeterminate( true );
        progress.show();
        if (mImageUri != null) {
            new Thread( () -> mStorageRef.putFile( mImageUri )
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e( "pic url", uri.toString() );
                                    picUrl = uri.toString();
                                    getData();
                                    progress.dismiss();
                                    progress.cancel();

                                }
                            } );
                        }
                    } )
                    .addOnFailureListener( exception -> {
                        progress.dismiss();
                        progress.cancel();
                        exception.printStackTrace();
                    } ) ).start();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder( OpenProfile.this );
            builder.setMessage( "Do you want to add a profile pic " )
                    .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setType( "image/*" );
                            intent.setAction( Intent.ACTION_GET_CONTENT );
                            startActivityForResult( intent, PICK_IMAGE_REQUEST );
                        }
                    } )
                    .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mImageUri = null;
                            picUrl = "";
                        }
                    } );
            builder.create();
            builder.show();
        }
    }

    private void getData(){
        //user data
        mobile_nO=mobileNO.getText().toString().trim();
        boolean check;
        if(!Pattern.matches("[a-zA-Z]+", mobile_nO)) {
            if(mobile_nO.length() < 6 || mobile_nO.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
                name.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        if(check) {
            nme = name.getText().toString();
            if (!nme.matches( "" )) {
                addres = address.getText().toString().trim();
                if (!addres.matches( "" )) {
                    Log.e( "pic url",picUrl );
                    usr=new UserDetails( nme,addres,mobile_nO,desigTit,picUrl,user.getUid(),"sex");
                    //add user to firebase dtaabse
                    newRef = mDatabaseRef.child("/Users/"+ user.getUid());
                    newRef.setValue(usr).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    } );
                }
            }
        }
    }

    private void initialise(){
         mDatabaseRef= FirebaseDatabase.getInstance().getReference();
         sharedPreferences=getApplicationContext().getSharedPreferences( Constants.Pref, 0); // 0 - for private mode
        //userDetails
        profilePic=findViewById( R.id.profilePic );
        name=findViewById( R.id.nameEdit );
        address=findViewById( R.id.AddressEdit );
        mobileNO=findViewById( R.id.mobileEdit );

        orglist = findViewById( R.id.orgList );
        desigList = findViewById( R.id.designationList );

        name.setText( null );
        address.setText( null );
        mobileNO.setText( null );
    }
    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
    }
    public  void onClick(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction( Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get()
                    .load(mImageUri).into(profilePic);
        }
    }
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(OpenProfile.this, "User Signed Out", Toast.LENGTH_SHORT).show();
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
}
