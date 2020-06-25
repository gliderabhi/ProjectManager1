package com.example.projectmanager.Classes;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanager.R;
import com.squareup.picasso.Picasso;

public class AttendanceViewHolder extends RecyclerView.ViewHolder  {
   public RelativeLayout root;
    public LinearLayout editLay,l1,l2,l3,l4;
    public ImageView b1,b2,b3,b4;
    public TextView name,desig;
    public ImageView img,submit;
    public EditText duration;

    public AttendanceViewHolder(View itemView) {
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
                Picasso.get().load( url ).resize( pxw, pxh ).transform(new CircleTransform()).into( img );
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
