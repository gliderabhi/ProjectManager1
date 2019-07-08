package com.example.projectmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projectmanager.Classes.DrawingsDetails;
import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.UserDetails;
import com.example.projectmanager.Classes.UserSite;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.example.projectmanager.Classes.Constants.ID;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteNAme;
import static com.example.projectmanager.Classes.Constants.ongoing;

public class addImage extends AppCompatActivity {

    private EditText title,remarks;
    private ImageView buttonAdd,previewImg;
    private int PICK_IMAGE_REQUEST=1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private ProgressDialog progress;
    private String picUrl,type;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_image );

        title= findViewById( R.id.titleEdit);
        remarks=findViewById( R.id.remarks );
        buttonAdd=findViewById( R.id.imageSelect );
        previewImg=findViewById( R.id. previewImage);

        buttonAdd.setOnClickListener( v -> {
            selectImage();
        } );

         sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    private void selectImage(){

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction( Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
             type = MimeTypeMap.getFileExtensionFromUrl(mImageUri.toString());
            Picasso.get()
                    .load(mImageUri)
                    .fit()
                    .into( previewImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            updateDataBase();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    } );
            previewImg.setVisibility( View.VISIBLE );
        }
    }

    private void uploadImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference("/Sites/Drawings/"+ sp.getString( ID, "" ));
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

                        //define the priority some way after creating view for the same
                        //adding the user as site member
                        if(title.getText().toString()!=null ){
                            if(!title.getText().toString().matches( "" )){
                                if(remarks.getText().toString()!=null ){
                                    if(!remarks.getText().toString().matches( "" )){
                                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/SiteDrawings/" + sp.getString( ID, "" ) + "/"+ title.getText().toString());

                                        DrawingsDetails drawingsDetails=new DrawingsDetails( title.getText().toString(),remarks.getText().toString(),picUrl,type );
                                        newRef.setValue( drawingsDetails ).addOnFailureListener( e ->
                                                Toast.makeText( getApplicationContext(),"Unable to add the drawing please try again "+ e.getMessage(),Toast.LENGTH_LONG ).show() );

                                    }
                                }
                            }
                        }

                        startActivity( new Intent( getApplicationContext(),Drawings.class ) );

                        progress.dismiss();
                        progress.cancel();

                    } ) )
                    .addOnFailureListener( exception -> {
                        progress.dismiss();
                        progress.cancel();
                        exception.printStackTrace();
                    } ) ).start();
        }


    }
    private void updateDataBase(){
        AlertDialog.Builder builder=new AlertDialog.Builder( addImage.this );
        builder.setTitle( " Are you sure to add this image to site drawings? " )
                .setCancelable(false)
                .setPositiveButton( " ok ", (dialog, id) -> {
                    uploadImage();
                } )
                .setNegativeButton( "cancel", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();
                } );
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Are you sure to add this image to site drawings? ");
        alert.show();
    }

}
