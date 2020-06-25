package com.example.projectmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanager.Classes.ShopKeepers;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopsAddition extends AppCompatActivity {
    LinearLayout dynamicRel;
    EditText compName;
    Button addMore;
    Button finish;
    int productsNo;
    DatabaseReference Company ;
    String path;
    boolean doubleBackToExitPressedOnce = false;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops_addition);

        dynamicRel = findViewById(R.id.dynamicViewAdd);
        compName = findViewById(R.id.companyName);
        addMore = findViewById(R.id.addMoreButton);
        finish = findViewById(R.id.Finish);
        path = "Companies/";
        Company = FirebaseDatabase.getInstance().getReference(path);


         productsNo =0;
        addMore.setOnClickListener(v -> {
            if(!compName.getText().toString().equals("")) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View vs = inflater.inflate(R.layout.product_info, null);
                TextView idNo = vs.findViewById(R.id.IdView);
                idNo.setText(String.valueOf(productsNo + 1));
                dynamicRel.addView(vs, productsNo);
                productsNo++;
            }else{
                Toast.makeText(getApplicationContext(), "please give company name ", Toast.LENGTH_LONG).show();
            }
        });
        finish.setOnClickListener(v -> {

            ArrayList<Integer> cost =new ArrayList<>();
            ArrayList<String > prductName = new ArrayList<>();
            path += compName.getText().toString();
            Company = FirebaseDatabase.getInstance().getReference(path);
            HashMap<String, Integer> nameCost = new HashMap<>();
            for(int index = 0; index < dynamicRel.getChildCount(); index++) {
                View vs = dynamicRel.getChildAt(index);
                EditText name = vs.findViewById(R.id.prodName);
                EditText cst = vs.findViewById(R.id.prodCost);
                Log.e("msg", name.getText().toString()+" "+ cst.getText().toString());
                nameCost.put(name.getText().toString() , Integer.parseInt(cst.getText().toString()));

            }
            ShopKeepers spk = new ShopKeepers(compName.getText().toString(), nameCost);
            Company.setValue(spk);
        });
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
                    Toast.makeText(ShopsAddition.this, "User Signed Out", Toast.LENGTH_SHORT).show();
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
