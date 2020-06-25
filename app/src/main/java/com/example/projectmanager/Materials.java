package com.example.projectmanager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectmanager.Classes.ShopKeepers;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteID;

public class Materials extends Activity  {
    Button writeExcelButton, readExcelButton;
    static String TAG = "ExelLog";
    Spinner shopName ;
    String siteId;
    TextView name ;
    DatabaseReference shopkeepers;
    boolean doubleBackToExitPressedOnce = false;


    private static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e( TAG, "Storage not available or read only" );
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor( HSSFColor.LIME.index );
        cs.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet( "myOrder" );

        // Generate column headings
        Row row = sheet1.createRow( 0 );

        c = row.createCell( 0 );
        c.setCellValue( "Item Number" );
        c.setCellStyle( cs );

        c = row.createCell( 1 );
        c.setCellValue( "Quantity" );
        c.setCellStyle( cs );

        c = row.createCell( 2 );
        c.setCellValue( "Price" );
        c.setCellStyle( cs );

        sheet1.setColumnWidth( 0, (15 * 500) );
        sheet1.setColumnWidth( 1, (15 * 500) );
        sheet1.setColumnWidth( 2, (15 * 500) );

        // Create a path where we will place our List of objects on external storage
        File file = new File( context.getExternalFilesDir( null ), fileName );
        FileOutputStream os = null;

        try {
            os = new FileOutputStream( file );
            wb.write( os );
            Log.w( "FileUtils", "Writing file" + file );
            success = true;
        } catch (IOException e) {
            Log.w( "FileUtils", "Error writing " + file, e );
        } catch (Exception e) {
            Log.w( "FileUtils", "Failed to save file", e );
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    private static void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e( TAG, "Storage not available or read only" );
            return;
        }

        try {
            // Creating Input Stream
            File file = new File( context.getExternalFilesDir( null ), filename );
            FileInputStream myInput = new FileInputStream( file );

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem( myInput );

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook( myFileSystem );

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt( 0 );

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d( TAG, "Cell Value: " + myCell.toString() );
                    Toast.makeText( context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT ).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_materials );
        SharedPreferences sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );
        siteId=sp.getString( SiteID, "" );
        name = findViewById(R.id.sitename);
        name.setText(siteId);
        shopName = findViewById(R.id.ShopNameSpinner);

        name.setVisibility(View.GONE);
        shopName.setVisibility(View.GONE);
        ArrayList<String > names = new ArrayList<>();
        names.add("Select name of shop");
        shopkeepers = FirebaseDatabase.getInstance().getReference("Companies/");
        shopkeepers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ShopKeepers sp = child.getValue(ShopKeepers.class);
                    System.out.println(sp.getName());
                    names.add(sp.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ArrayAdapter ad = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, names);
        shopName.setAdapter(ad);
        shopName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item selected "+ position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        name.setVisibility(View.VISIBLE);
        shopName.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener( task -> {
                    Toast.makeText(Materials.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                    finish();
                } );
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            startActivity( new Intent( getApplicationContext(),mainMenu.class ) );
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed( () -> doubleBackToExitPressedOnce=false, 2000);
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
