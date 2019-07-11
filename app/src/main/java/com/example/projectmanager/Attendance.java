package com.example.projectmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanager.Classes.SIteMembers;
import com.example.projectmanager.Classes.Site;
import com.example.projectmanager.Classes.usrAttendance;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static com.example.projectmanager.Classes.Constants.PACKAGE_NAME;
import static com.example.projectmanager.Classes.Constants.SiteID;

public class Attendance extends AppCompatActivity {

    private CalendarView calendarView;
    private Spinner spinner;
    private String datePresent;
    private ArrayList<String > siteNameList;
    private String siteSelected;
    private RecyclerView recyclerView;
    private ArrayList<SIteMembers> sIteMembersList;
    private FirebaseRecyclerAdapter<SIteMembers, ViewHolder> adapter1;

    private int workDuration;
    private usrAttendance usrAttendance;
    private String siteId;
    private ProgressBar progress_circular_attend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_attendance );

        initialise();
    }

    private void initialise() {
        TextView date;
        date=findViewById( R.id.date );
        calendarView=findViewById( R.id.dateSet );
        spinner=findViewById( R.id.site_List_Spinner );
        siteNameList=new ArrayList<>(  );
        recyclerView=findViewById( R.id.memeberListRec );
        progress_circular_attend=findViewById( R.id.progress_circular_attend );

        sIteMembersList=new ArrayList<>(  );
        calendarView.setVisibility( View.GONE );
        siteNameList=new ArrayList<>(  );

        generateList();

        String dateStr = "04/05/2010";

        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = null;
        try {
            dateObj = curFormater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yyyy");

         datePresent = postFormater.format(dateObj);
        date.setText( datePresent );

        date.setOnClickListener( v -> calendarView.setVisibility( View.VISIBLE ) );
        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {
                    if (dayOfMonth < 10) {
                        if (month < 10) {

                            datePresent = "0" + dayOfMonth + "-" + "0" + (month + 1) + "-" + year;
                        } else {
                            datePresent = "0" + dayOfMonth + "-" + (month + 1) + "-" + year;
                        }
                    } else {
                        datePresent = dayOfMonth + "-" + (month + 1) + "-" + year;

                    }
                    date.setText( datePresent );
                    calendarView.setVisibility( View.GONE );
                } );

        //generate sitename list
        progress_circular_attend.setVisibility( View.VISIBLE );
        DatabaseReference siteList=FirebaseDatabase.getInstance().getReference("Site Details/");
        siteNameList.clear();
        siteNameList.add( "Select" );
        siteList.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                     Site site= dataSnapshot1.getValue(Site.class);
                     siteNameList.add( site.getName() );
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progress_circular_attend.setVisibility( View.GONE );
            }
        } );
        ArrayAdapter<String > adapter=new ArrayAdapter<String>( getApplicationContext(),android.R.layout.simple_spinner_item, siteNameList );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( adapter );

        if(spinner.getAdapter()!=null) {
            progress_circular_attend.setVisibility( View.GONE );
        }
        siteSelected="";
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                siteSelected=siteNameList.get( position );
                //Toast.makeText( getApplicationContext(), siteSelected, Toast.LENGTH_SHORT ).show();

                //get the site id
                DatabaseReference siteIdFinder=FirebaseDatabase.getInstance().getReference("Site Details");
                siteIdFinder.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                            Site s = dataSnapshot1.getValue( Site.class );
                            if(s!=null) {
                                if (s.getName().matches( siteSelected )) {
                                    siteId = s.getId();
                                    //Toast.makeText( getApplicationContext(), siteId, Toast.LENGTH_SHORT ).show();
                                    progress_circular_attend.setVisibility( View.VISIBLE );
                                    generateList();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText( getApplicationContext(),"Error please try another site "+databaseError.getMessage(),Toast.LENGTH_SHORT ).show();
                    }
                } );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                siteSelected="";
            }
        } );


    }

    Query query;
    private void generateList() {
        recyclerView= findViewById( R.id.memeberListRec );
        recyclerView.setLayoutManager(new LinearLayoutManager( this ));
        sIteMembersList.clear();
        //write the right query

        SharedPreferences sp = getSharedPreferences( PACKAGE_NAME, Context.MODE_PRIVATE );
        siteId=sp.getString( SiteID, "" );
        Log.e( "msg",siteId);
        DatabaseReference employeeList=FirebaseDatabase.getInstance().getReference("Site Members/"+siteId+"/");

        query=employeeList.limitToLast( 30 );
        FirebaseRecyclerOptions<SIteMembers> options =
                new FirebaseRecyclerOptions.Builder<SIteMembers>()
                        .setQuery( query, SIteMembers.class )
                        .build();
        adapter1 = new FirebaseRecyclerAdapter<SIteMembers,ViewHolder>( options ) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from( parent.getContext() )
                        .inflate( R.layout.attendance_item, parent, false );

                return new ViewHolder( view );
            }



            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, SIteMembers usr) {
               // Toast.makeText( getApplicationContext(),usr.getDesignation(),Toast.LENGTH_SHORT ).show();
                holder.setImg( usr.getImgUrl() );
                holder.setDesig( usr.getDesignation() );
                holder.setName( usr.getName() );

                sIteMembersList.add( usr );
                holder.b1.setSelected( false );
                holder.b2.setSelected( false );
                holder.b3.setSelected( false );
                holder.b4.setSelected( false );

                holder.l1.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.b1.setVisibility( View.VISIBLE );
                        workDuration=8;
                        DatabaseReference users= FirebaseDatabase.getInstance().getReference("AttendanceRecords/"+siteId+"/"+datePresent+"/"+sIteMembersList.get( position ).getId()+"/");
                        usrAttendance=new usrAttendance( sIteMembersList.get( position ).getId(),sIteMembersList.get( position ).getName(),String.valueOf( workDuration ) );
                        users.setValue( usrAttendance ).addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("msg","Loaded");
                            }
                        } );
                        //Toast.makeText( getApplicationContext(),workDuration,Toast.LENGTH_SHORT ).show();
                        holder.b2.setVisibility( View.GONE );
                        holder.b3.setVisibility( View.GONE );
                        holder.b4.setVisibility( View.GONE );
                        holder.editLay.setVisibility( View.GONE );
                    }
                } );
                holder.l2.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.b2.setVisibility( View.VISIBLE );
                        workDuration=4;
                        DatabaseReference users= FirebaseDatabase.getInstance().getReference("AttendanceRecords/"+siteId+"/"+datePresent+"/"+sIteMembersList.get( position ).getId()+"/");
                        usrAttendance=new usrAttendance( sIteMembersList.get( position ).getId(),sIteMembersList.get( position ).getName(),String.valueOf( workDuration ) );
                        users.setValue( usrAttendance ).addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("msg","Loaded");
                            }
                        } );
                        //Toast.makeText( getApplicationContext(),workDuration,Toast.LENGTH_SHORT ).show();
                        holder.b3.setVisibility( View.GONE );
                        holder.b1.setVisibility( View.GONE );
                        holder.b4.setVisibility( View.GONE );
                        holder.editLay.setVisibility( View.GONE );
                    }
                } );
                holder.l3.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.b3.setVisibility( View.VISIBLE );
                        holder.editLay.setVisibility( View.VISIBLE );
                        holder.b1.setVisibility( View.GONE );
                        holder.b2.setVisibility( View.GONE );
                        holder.b4.setVisibility( View.GONE );
                    }
                } );

                holder.submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        workDuration= Integer.parseInt( holder.duration.getText().toString() );
                        //Toast.makeText( getApplicationContext(),String.valueOf( workDuration),Toast.LENGTH_SHORT ).show();
                        DatabaseReference users= FirebaseDatabase.getInstance().getReference("AttendanceRecords/"+siteId+"/"+datePresent+"/"+sIteMembersList.get( position ).getId()+"/");
                        usrAttendance=new usrAttendance( sIteMembersList.get( position ).getId(),sIteMembersList.get( position ).getName(),String.valueOf( workDuration ) );
                        users.setValue( usrAttendance ).addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("msg","Loaded");
                            }
                        } );
                        holder.editLay.setVisibility( View.GONE );
                    }
                } );
                /*holder.submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        workDuration= Integer.parseInt( holder.duration.getText().toString() );
                    }
                } );*/
            }

        };

        recyclerView.setAdapter( adapter1 );
        adapter1.startListening();
        progress_circular_attend.setVisibility( View.GONE );
    }

    private class ViewHolder extends RecyclerView.ViewHolder  {
        RelativeLayout root;
         LinearLayout editLay,l1,l2,l3,l4;
         ImageView b1,b2,b3,b4;
         TextView name,desig;
         ImageView img,submit;
         EditText duration;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.layout_root);
            b1 = itemView.findViewById(R.id.check1);
            b2 = itemView.findViewById(R.id.check2);
            b3 = itemView.findViewById(R.id.check3);
            b4 = itemView.findViewById(R.id.check4);
            img=itemView.findViewById( R.id.imgProfile );
            desig=itemView.findViewById( R.id.desigText);
            name=itemView.findViewById( R.id.nameText );
            editLay = itemView.findViewById(R.id.editLay);
            duration=itemView.findViewById( R.id.durationValue);
            submit=itemView.findViewById( R.id.submit );

            l1=itemView.findViewById(R.id.l1  );
            l2=itemView.findViewById(R.id.l2 );
            l3=itemView.findViewById(R.id.l3  );
            l4=itemView.findViewById(R.id.l4  );
            editLay.setVisibility( View.GONE );

            //Log.e( "msg","initation" );

        }



        public void setImg(String url){

            int pxw = (int) (90 * Resources.getSystem().getDisplayMetrics().density);
            int pxh = (int) (90 * Resources.getSystem().getDisplayMetrics().density);

            if(url!=null) {
                if (!url.matches( "" )) {
                    Picasso.get().load( url ).resize( pxw, pxh ).transform( new CircleTransform() ).into( img );
                }
            }
        }

        public void setName(String nme) {
           name.setText( nme );
        }

        public void setDesig(String deig) {
            desig.setText( deig );
        }

    }

    //setting image of profile as circular
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }


    @Override
    protected void onStart() {
        if(adapter1!=null){
            adapter1.startListening();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        adapter1.stopListening();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( getApplicationContext(),mainMenu.class ) );
    }
}
