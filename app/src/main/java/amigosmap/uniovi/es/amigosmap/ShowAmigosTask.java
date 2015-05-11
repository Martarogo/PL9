package amigosmap.uniovi.es.amigosmap;

import android.os.AsyncTask;

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

public class ShowAmigosTask extends AsyncTask<String, Void, String> {

    List<Amigo> amigosList;

    @Override
    protected void onPostExecute(String result) {
        new MapsActivity().SetPositions(amigosList);
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            amigosList = parseDataFromNetwork(readStream(openUrl(urls[0])));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (!amigosList.isEmpty()) {
            return "OK";
        }
        else {
            return null;
        }
    }

    /*
    protected List getCurrencyRateUsdRate(String url) throws IOException, JSONException {
        List<Amigo> amigosList = parseDataFromNetwork(readStream(openUrl(url)));

        return parseDataFromNetwork(readStream(openUrl(url)));
    }
    */

    private List parseDataFromNetwork(String data) throws IOException, JSONException {

        List<Amigo> amigosList = new ArrayList<Amigo>();

        JSONArray amigos = new JSONArray(data);

        for(int i = 0; i < amigos.length(); i++) {
            JSONObject amigoObject = amigos.getJSONObject(i);

            String name = amigoObject.getString("name");
            String longi = amigoObject.getString("longi");
            String lati = amigoObject.getString("lati");

            double longiNumber;
            double latiNumber;
            try {
                longiNumber = Double.parseDouble(longi);
                latiNumber = Double.parseDouble(lati);
            } catch (NumberFormatException nfe) {
                continue;
            }

            amigosList.add(new Amigo(name, latiNumber, longiNumber));
        }

        return amigosList;
    }

    protected InputStream openUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    protected String readStream(InputStream urlStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(urlStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        return total.toString();
    }

}