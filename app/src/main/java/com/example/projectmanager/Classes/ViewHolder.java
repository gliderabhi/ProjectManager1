package com.example.projectmanager.Classes;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout root;
    TextView txtTitle;
    TextView txtDesc;
    public ImageView img;

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById( R.id.list_root);
        txtTitle = itemView.findViewById(R.id.list_title);
        txtDesc = itemView.findViewById(R.id.list_desc);
        img=itemView.findViewById( R.id.image );
        //Log.e( "msg","initation" );

    }

    public void setImg(String url){

        int pxw = (int) (80 * Resources.getSystem().getDisplayMetrics().density);
        int pxh = (int) (80 * Resources.getSystem().getDisplayMetrics().density);
       if(url!=null ){
            if (!url.matches( "" )) {
                Picasso.get().load( url ).resize( pxw, pxh ).transform( new Constants.CircleTransform() ).into( img );
            }
        }

    }
    public void setTxtTitle(String string) {
        txtTitle.setText(string);
    }


    public void setTxtDesc(String string) {
        txtDesc.setText(string);
    }
}