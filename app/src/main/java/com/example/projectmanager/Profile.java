package com.example.projectmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectmanager.Classes.BankDetails;
import com.example.projectmanager.Classes.Constants;
import com.example.projectmanager.Classes.OrganisationDetails;
import com.example.projectmanager.Classes.UserDetails;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.regex.Pattern;

import static com.example.projectmanager.Classes.Constants.Female;
import static com.example.projectmanager.Classes.Constants.KEY_LATITUDE;
import static com.example.projectmanager.Classes.Constants.KEY_LONGITUDE;
import static com.example.projectmanager.Classes.Constants.Locale;
import static com.example.projectmanager.Classes.Constants.Male;
import static com.example.projectmanager.Classes.Constants.hideSoftKeyboard;

public class Profile extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private SharedPreferences.Editor editor;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profilePic;
    private EditText name,mobileNO,pass,checkPass,usrAccname,userAccNo,userIFSC,userBankName,userBankBranch,orgName,orgAddress,orgEmail,orgMOb,orgGstin;
    private SharedPreferences pref;
    private String nme,addres,mobile_nO,picUrl,desigTit;
    private RelativeLayout orgDetailsREl,orgAccDelRel;
    private BankDetails orgBnk,userBank;
    private OrganisationDetails orgDet;
    private UserDetails usr;
    StorageReference mStorageRef;
    FirebaseDatabase mFirebase;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference newRef;
    private FirebaseAuth mAuth;
    private Spinner orglist,desigList;
    private int fillCount=0;
    private TextView designation;
    private TextView address;
    private ImageView mapButton;
    private LinearLayout maleLay,femaleLay;
    private String sex;
    private RelativeLayout relFull;
    private ProgressDialog progress;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );
        hideSoftKeyboard(this);

        mFirebase= FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        pref = getApplicationContext().getSharedPreferences( Constants.Pref, 0); // 0 - for private mode
        editor = pref.edit();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();

        //initialise all the items in the view and generate references
        initialiseView();

        //update the data from the database and fill the textFields
        fillData();

        //two relative layouts
        orgDetailsREl=findViewById( R.id.OrganisationDetails );
        orgAccDelRel=findViewById( R.id.OrgAccount );
        //hide the relative layouts
        orgDetailsREl.setVisibility( View.GONE );
        orgAccDelRel.setVisibility( View.GONE );



        ArrayAdapter<String> adapter=new ArrayAdapter<>( this,android.R.layout.simple_spinner_item,Constants.org );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        orglist.setAdapter( adapter );

        orglist.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putString( Constants.Org,Constants.org[position] );
                editor.apply();
                //show the org details if org is yes
                if(Constants.org[position].matches( "YES" )){
                    Toast.makeText( getApplicationContext(),"yes", Toast.LENGTH_LONG ).show();
                    orgDetailsREl.setVisibility( View.VISIBLE );
                    orgAccDelRel.setVisibility( View.VISIBLE );

                }
                if(Constants.org[position].matches( "NO" )){
                    Toast.makeText( getApplicationContext(),"no", Toast.LENGTH_LONG ).show();
                    orgDetailsREl.setVisibility( View.GONE );
                    orgAccDelRel.setVisibility( View.GONE );

                }
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

        cancel.setOnClickListener( (View v) -> {
            startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );

        } );

        upload.setOnClickListener( v -> {
           /* if(fillCount==1){
                startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );

            }else {
                uploadImage();
            }
            */
           uploadImage();
        } );


    }

    private void fillData() {
        progress = new ProgressDialog( this );
        progress.setMessage( "Wait Loading" );
        progress.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        progress.setIndeterminate( true );
        progress.show();

        newRef = mDatabaseRef.child( "/Users/" );
        Query phoneQuery = newRef.orderByChild( "id" ).equalTo( user.getUid() );
        phoneQuery.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        usr = singleSnapshot.getValue( UserDetails.class );

                        if (usr != null) {

                            if(!usr.getImageUrl().matches( "" )) {
                                Picasso.get()
                                        .load( usr.getImageUrl() )
                                        .fit()
                                        .into( profilePic );
                                picUrl= usr.getImageUrl();
                            }
                            name.setText( usr.getName() );

                            if(usr.getSex().matches( Male )){
                                maleLay.setBackgroundColor( Color.YELLOW );
                                sex=Male;
                            }else{
                                femaleLay.setBackgroundColor( Color.YELLOW );
                                sex=Female;
                            }
                            if(usr.getAddress()!=null) {
                                address.setText( usr.getAddress() );
                            }
                            mobileNO.setText( usr.getMobileNo() );
                            designation.setText( usr.getTitle() );
                            designation.setVisibility( View.VISIBLE );
                            desigList.setVisibility( View.GONE );
                            fillCount = 1;
                            progress.dismiss();
                            relFull.setVisibility(View.VISIBLE);
                        } else {
                            name.setText( user.getDisplayName() );
                            mobileNO.setText( user.getPhoneNumber() );
                            designation.setVisibility( View.GONE );
                            desigList.setVisibility( View.VISIBLE );
                            progress.dismiss();
                            relFull.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    progress.dismiss();
                    relFull.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.e( "msg", "onCancelled", databaseError.toException() );
                progress.dismiss();
                relFull.setVisibility(View.VISIBLE);
            }


        } );

        //similarly fill others fields can be done later

    }

    private void initialiseView() {

        //userDetails
        profilePic=findViewById( R.id.profilePic );
        name=findViewById( R.id.nameEdit );
        address=findViewById( R.id.AddressEdit );
        mobileNO=findViewById( R.id.mobileEdit );
        checkPass=findViewById( R.id.passCheckEdit );
        designation=findViewById( R.id.designationText );
        mapButton=findViewById( R.id.mapProf );
        maleLay=findViewById( R.id.maleLay);
        femaleLay=findViewById( R.id.femaleLay );
        relFull = findViewById(R.id.ProfileRel);
        relFull.setVisibility(View.GONE);

        maleLay.setOnClickListener( v -> {
            sex= Male;
            maleLay.setBackgroundColor( Color.YELLOW);
            femaleLay.setBackgroundColor( Color.WHITE );
        } );
        femaleLay.setOnClickListener( v -> {
            sex=Female;
            femaleLay.setBackgroundColor( Color.YELLOW );
            maleLay.setBackgroundColor( Color.WHITE );
        } );

        mapButton.setOnClickListener( v -> {
            startActivity(new Intent(   getApplicationContext(), MapsActivity.class ));
            pref.getString( Locale,"0" );
            if(!pref.getString( Locale,"0" ).matches( "0" )){
                address.setText( pref.getString( Locale,"0" ));
            }else{

                address.setText( pref.getString( KEY_LATITUDE,"0" ) + "/"+ pref.getString( KEY_LONGITUDE,"0" ) );
            }
        } );


         orglist = findViewById( R.id.orgList );
         desigList = findViewById( R.id.designationList );

        name.setText( null );
        address.setText( null );
        mobileNO.setText( null );
        checkPass.setText( null );
        //userAccount details
        userAccNo=findViewById( R.id.acNo );
        userBankBranch=findViewById( R.id.bank_branch );
        userBankName=findViewById( R.id.bank_name );
        userIFSC=findViewById( R.id.ifsc );
        usrAccname=findViewById( R.id.accName );

        userIFSC.setText( null );
        userAccNo.setText( null );
        userBankName.setText( null );
        userBankBranch.setText( null );
        usrAccname.setText( null );

        //organisationDetails
        orgName=findViewById( R.id.companyEdit );
        orgAddress=findViewById( R.id.comAddressEdit );
        orgGstin=findViewById( R.id.gstin );
        orgMOb=findViewById( R.id.comMob );
        orgEmail=findViewById( R.id.comMail );

        orgName.setText( null );
        orgAddress.setText( null );
        orgMOb.setText( null );
        orgEmail.setText( null );
        orgGstin.setText( null );

        //organisation bank details
        orgNAme=findViewById( R.id.org_accName );
        orgAccNo=findViewById( R.id.org_acNo );
        orgIFSC=findViewById( R.id.org_ifsc );
        orgBank=findViewById( R.id.org_bank_name );
        orgBranch=findViewById( R.id.org_bank_branch );
        orgNAme.setText( null );
        orgAccNo.setText( null );
        orgIFSC.setText( null );
        orgBank.setText( null );
        orgBranch.setText( null );
    }


    private void uploadImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference("/Users/ProfilePics/"+ user.getUid());
        progress = new ProgressDialog(this);
        progress.setMessage("Uploading Image");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        if(mImageUri!=null) {
            new Thread( () -> mStorageRef.putFile( mImageUri )
                    .addOnSuccessListener( taskSnapshot -> mStorageRef.getDownloadUrl().addOnSuccessListener( uri -> {
                        //Log.e( "pic url",uri.toString() );
                        picUrl = uri.toString();
                        getData();
                        progress.dismiss();
                        progress.cancel();

                    } ) )
                    .addOnFailureListener( exception -> {
                        progress.dismiss();
                        progress.cancel();
                        exception.printStackTrace();
                    } ) ).start();
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder( Profile.this );
            builder.setMessage( "Do you want to add a profile pic " )
                    .setPositiveButton( "Yes", (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction( Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    } )
                    .setNegativeButton( "No", (dialog, which) -> {
                        mImageUri=null;
                        if(picUrl==null) {
                            picUrl = "";
                        }
                        getData();
                        progress.dismiss();
                    } );
            builder.create();
            builder.show();
        }



    }

    private EditText orgBank,orgBranch,orgAccNo,orgNAme,orgIFSC;
    String org_nm,org_add,org_mob,org_email,org_gstin;
    String usr_acc_name,usr_ifsc,usr_bank,usr_branch,usr_acc_no,org_acc_no,org_ac_name,org_ac_ifsc,org_bank,org_branch;

    private void getData() {

        progress.show();
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
                if(pref.getString( KEY_LATITUDE,"0" ).matches( "0" )) {
                    addres = address.getText().toString().trim();
                    if (!addres.matches( "" )) {
                        //Log.e( "pic url", picUrl );

                        //put check for sex
                        usr = new UserDetails( nme, addres, mobile_nO, desigTit, picUrl, user.getUid(), sex );
                        //add user to firebase dtaabse
                        newRef = mDatabaseRef.child( "/Users/" + user.getUid() );
                        newRef.setValue( usr ).addOnFailureListener( e -> e.printStackTrace() );
                    }
                }else{
                    //Log.e( "pic url", picUrl );
                    addres=pref.getString( KEY_LATITUDE,"0" ) + "/"+ pref.getString( KEY_LONGITUDE,"0" );
                    usr = new UserDetails( nme, addres, mobile_nO, desigTit, picUrl, user.getUid(), sex );
                    //add user to firebase dtaabse
                    newRef = mDatabaseRef.child( "/Users/" + user.getUid() );
                    newRef.setValue( usr ).addOnFailureListener( e -> e.printStackTrace() );
                }
            }
        }

       // TODO: add checks if textfields empty
        //user account details
        usr_acc_name=usrAccname.getText().toString().trim();
        usr_acc_no=userAccNo.getText().toString().trim();
        usr_ifsc=userIFSC.getText().toString().trim();
        usr_bank=userBankName.getText().toString().trim();
        usr_branch=userBankBranch.getText().toString().trim();

        userBank=new BankDetails( usr_acc_name,usr_acc_no,usr_ifsc,usr_bank,usr_branch );
        //adding user bank details
         newRef = mDatabaseRef.child("/Bank Details/Users/"+ user.getUid());
         newRef.setValue( userBank ).addOnFailureListener( e -> e.printStackTrace() );


        //if organisation is yes
           if(pref.getString( Constants.Org,"NO" ).matches( "YES" )){
                org_nm=orgName.getText().toString().trim();
                org_add=orgAddress.getText().toString().trim();
                org_email=orgEmail.getText().toString().trim();
               org_mob=orgMOb.getText().toString().trim();
               org_gstin=orgGstin.getText().toString().trim();
               orgDet=new OrganisationDetails( org_nm,org_add,org_email,org_mob,org_gstin );
               newRef = mDatabaseRef.child("/Bank Details/Users/"+ user.getUid());
               newRef.setValue( userBank ).addOnFailureListener( e -> e.printStackTrace() );

               //organisation bank details add to firebase
               org_ac_name=orgNAme.getText().toString().trim();
               org_ac_ifsc=orgIFSC.getText().toString().trim();
               org_bank=orgBank.getText().toString().trim();
               org_branch=orgBranch.getText().toString().trim();
               org_acc_no=orgAccNo.getText().toString().trim();
               orgBnk=new BankDetails( org_ac_name,org_acc_no,org_ac_ifsc,org_bank,org_branch );
               newRef = mDatabaseRef.child("/Bank Details/Organisation/"+ user.getUid());
               newRef.setValue( userBank ).addOnFailureListener( e -> e.printStackTrace() );
           }


        startActivity( new Intent( getApplicationContext(),SelectionPanel.class ) );
           Toast.makeText( getApplicationContext(),"Details Updated ",Toast.LENGTH_SHORT ).show();
           progress.dismiss();
    }


    private Uri mImageUri;
    public void onClick(View v){

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
                    .load(mImageUri)
                    .fit()
                    .into(profilePic);
        }
    }

    @Override
    protected void onDestroy() {
        editor.putString( KEY_LATITUDE,"0" );
        editor.putString( KEY_LONGITUDE,"0" );
        editor.apply();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
          switch (arg0.getId()){
              case R.id.orgList:
                  //Log.e( Constants.Msg,"org spinner" );
                  editor.putString( Constants.Org,Constants.org[position] );
                  editor.apply();
                  Toast.makeText( getApplicationContext(),"yes", Toast.LENGTH_LONG ).show();
                  //show the org details if org is yes
                  if(Constants.org[position].matches( "YES" )){
                      Toast.makeText( getApplicationContext(),"yes", Toast.LENGTH_LONG ).show();
                      orgDetailsREl.setVisibility( View.VISIBLE );
                      orgAccDelRel.setVisibility( View.VISIBLE );

                  }
                  break;
              case R.id.designationList:
                  //Log.e( Constants.Msg,"desig spinner" );
                  editor.putString( Constants.Designation,Constants.desig[position] );
                  editor.apply(); break;
          }

    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

        if(arg0.getId()==R.id.designationList) {
            editor.putString( Constants.Designation, "None" );
            editor.apply();
        }else if(arg0.getId()==R.id.orgList){
            editor.putString( Constants.Org,"No" );
            editor.apply();
        }
        // TODO Auto-generated method stub
    }
    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener( task -> {
                    Toast.makeText(Profile.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                    finish();
                } );
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
}
