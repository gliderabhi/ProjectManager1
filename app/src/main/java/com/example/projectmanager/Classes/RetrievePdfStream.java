package com.example.projectmanager.Classes;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RetrievePdfStream extends AsyncTask<String,Void,InputStream> {

    @Override
    protected InputStream doInBackground(String... strings) {
        InputStream inputStream =null;
        try{
            URL url= new URL( strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection() ;
            if( urlConnection.getResponseCode() == 200){
                inputStream= new BufferedInputStream( urlConnection.getInputStream() );
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  inputStream;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        super.onPostExecute( inputStream );
    }
}

