package amigosmap.uniovi.es.amigosmap;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

public class ShowAmigosTask extends AsyncTask<String, Void, String> {
    List<Amigo> amigosList;

    private static final String LOGTAG = "ShowAmigosTask";

    @Override
    protected void onPostExecute(String result) {

        if (result.equals("OK")) {
            new MapsActivity().SetPositions(amigosList);
        }
        else {
            Log.i(LOGTAG, "No hay amigos que mostrar");
        }
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            String allFriends = GetPositions(urls[0]);
            amigosList = parseDataFromNetwork(allFriends);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        /*if (amigosList.isEmpty()) {
            return null;
        }*/
        return "OK";
    }

    private List parseDataFromNetwork(String data) throws IOException, JSONException {

        List<Amigo> amigosList = new ArrayList<Amigo>();

        JSONArray amigos = new JSONArray(data);

        for(int i = 0; i < amigos.length(); i++) {
            JSONObject amigoObject = amigos.getJSONObject(i);

            String name = amigoObject.getString("name");
            String longi = amigoObject.getString("longi").replace(",", ".");
            String lati = amigoObject.getString("lati").replace(",", ".");

            double longiNumber;
            double latiNumber;
            try {
                longiNumber = Double.parseDouble(longi);
                latiNumber = Double.parseDouble(lati);
            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                continue;
            }

            amigosList.add(new Amigo(name, latiNumber, longiNumber));
        }

        return amigosList;
    }

    private static String GetPositions(String url) throws IOException {

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);

        HttpResponse getResponse = httpClient.execute(httpGet);
        HttpEntity entity = getResponse.getEntity();
        InputStream inputStream = entity.getContent();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String stringResponse = "";

        String line = bufferedReader.readLine();
        while (line != null) {
            stringResponse += line;
            line = bufferedReader.readLine();
        }

        return stringResponse;
    }
}