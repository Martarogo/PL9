package amigosmap.uniovi.es.amigosmap;


import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChangePositionTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPostExecute(String result) {}

    @Override
    protected String doInBackground(String... urls) {

        String url = urls[0];
        String name = urls[1];

        double latitude;
        double longitude;

        try {
            latitude = Double.parseDouble(urls[2]);
            longitude = Double.parseDouble(urls[3]);

            ChangePosition(url, name, latitude, longitude);
        }
        catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }


        return "OK";
    }

    private void ChangePosition(String url, String name, double latitude, double longitude){

        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPut httpPut = new HttpPut(url);

            List <NameValuePair> list = new ArrayList<NameValuePair>();

            list.add(new BasicNameValuePair("name", name));
            list.add(new BasicNameValuePair("lati", String.valueOf(latitude).replace(".", ",")));
            list.add(new BasicNameValuePair("longi", String.valueOf(longitude).replace(".", ",")));

            httpPut.setEntity(new UrlEncodedFormEntity(list));
            httpClient.execute(httpPut);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}