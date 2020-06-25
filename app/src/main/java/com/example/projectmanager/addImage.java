package com.example.projectmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.projectmanager.Classes.DrawingsDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteID;

public class addImage extends Activity {

    private EditText title,remarks;
    private TextView name;
    private ImageView buttonAdd,previewImg;
    private int PICK_IMAGE_REQUEST=1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private ProgressDialog progress;
    private String picUrl,type;
    private SharedPreferences sp;
    private Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_image );

        title= findViewById( R.id.titleEdit);
        remarks=findViewById( R.id.remarks );
        buttonAdd=findViewById( R.id.imageSelect );
        previewImg=findViewById( R.id. previewImage);
        upload= findViewById( R.id.upload );
        name= findViewById( R.id.FileName );

        upload.setOnClickListener( v ->{
            updateDataBase();
        } );
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
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            buttonAdd.setVisibility( View.GONE );
            name.setVisibility( View.VISIBLE );
            name.setText(getFileName( mImageUri ));

            Log.e( "msg", mImageUri.toString() );
            type = getContentResolver().getType( mImageUri );
            if (!type.matches( "application/pdf" ) || type.matches( "image/*" )) {
                Log.e( "msg", type );
                Picasso.get()
                        .load( mImageUri )
                        .fit()
                        .into( previewImg);
                previewImg.setVisibility( View.VISIBLE );
            }
        }
    }

    private void uploadImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference("/Sites/Drawings/"+ sp.getString( SiteID, "" ) + "/"+ title.getText());
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
                                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference( "/SiteDrawings/" + sp.getString( SiteID, "" ) + "/"+ title.getText().toString());

                                        DrawingsDetails drawingsDetails=new DrawingsDetails( title.getText().toString(),remarks.getText().toString(),picUrl,type );
                                        newRef.setValue( drawingsDetails ).addOnFailureListener( e ->
                                                Toast.makeText( getApplicationContext(),"Unable to add the drawing please try again "+ e.getMessage(),Toast.LENGTH_LONG ).show() );

                                    }
                                }else{

                                    Toast.makeText( getApplicationContext()," Please provide a remark to the file " ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText( getApplicationContext()," Please provide a title to the file " ,Toast.LENGTH_SHORT).show();
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
       if(type!=null) {
           if (type.matches( "application/pdf" ) || type.matches( "image/jpg" ) || type.matches( "image/png" ) || type.matches( "image/jpeg" ) || type.matches( "image/jpg" )) {
               AlertDialog.Builder builder = new AlertDialog.Builder( addImage.this );
               builder.setTitle( " Are you sure to add this image to site drawings? " )
                       .setCancelable( false )
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
               alert.setTitle( "Are you sure to add this image to site drawings? " );
               alert.show();
           }else{
               AlertDialog.Builder builder = new AlertDialog.Builder( addImage.this );
               builder.setTitle( " Please select an image or pdf to  proceed " )
                       .setCancelable( false )
                       .setPositiveButton( " ok ", (dialog, id) -> {
                           selectImage();
                       } )
                       .setNegativeButton( "cancel", (dialog, id) -> {
                           //  Action for 'NO' Button
                           dialog.cancel();
                       } );
               //Creating dialog box
               AlertDialog alert = builder.create();
               //Setting the title manually
               alert.setTitle( "Please select an image or pdf to  proceed " );
               alert.show();
           }
       }else{
           AlertDialog.Builder builder = new AlertDialog.Builder( addImage.this );
           builder.setTitle( " Please select an image or pdf to  proceed " )
                   .setCancelable( false )
                   .setPositiveButton( " ok ", (dialog, id) -> {
                       selectImage();
                   } )
                   .setNegativeButton( "cancel", (dialog, id) -> {
                       //  Action for 'NO' Button
                       dialog.cancel();
                   } );
           //Creating dialog box
           AlertDialog alert = builder.create();
           //Setting the title manually
           alert.setTitle( "Please select an image or pdf to  proceed " );
           alert.show();
       }

    }

}
