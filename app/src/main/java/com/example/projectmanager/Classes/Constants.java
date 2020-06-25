package com.example.projectmanager.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.squareup.picasso.Transformation;

public class Constants {
    public static final String PACKAGE_NAME ="com.example.projectmanager.Classes" ;
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";
    public static final String VIEW ="View" ;
    public static String Msg="Message";
    public static String Pref= "MyPref";
    public static String UserID= "UserID";
    public static String SiteID= "SiteID";
    public static String Designation= "Designation";
    public static String Org= "Organisation";
    public static String Completed="Completed";
    public static String ongoing="OnGoing";
    public static String[] priority={"Select","High","Medium","Low"};
    public static String[] org = {"Select","NO", "YES"};
    public static String[] desig = {"Select","Construction Contracting Agency","Consultant","Materials Supplier","Special Service Provider","Government Agency Representative"};
    public static String in="";
    public static String Priority="Priority";
    public static String SiteNAme="SiteName";
    public static String Creator="Creator";
    public static String[] activities ={"Sites","Employees","Attendance Register","Materials","Suppliers","Materials Supply Ledgers"};
    public static String Locale="locale";
    public static String Male="Male";
    public static String Female="Female";
    public static String SITE_NAME = "SITE_NAME";
    public static  String pdfLink= "https://firebasestorage.googleapis.com/v0/b/budgethandler-4dd29.appspot.com/o/pdf.png?alt=media&token=2a1aef98-9f60-4cfc-9cf0-175cddcef6dc";
    public static class CircleTransform implements Transformation {
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

    public static String mapStr1= "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    public static String mapStr2= "&types=geocode&language=fr&key=AIzaSyCyo9NTN6KV4Oo-n-CcSsfiAQ1-5fSZEME";

    public static float getPixel(int dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public static void hideSoftKeyboard(Activity act){
        act.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
