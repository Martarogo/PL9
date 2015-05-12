package amigosmap.uniovi.es.amigosmap;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

import org.apache.http.util.EntityUtils;

public class ShowAmigosTask extends AsyncTask<String, Void, String> {

    List<Amigo> amigosList;

    @Override
    protected void onPostExecute(String result) {

        if (result.equals("OK")) {
            new MapsActivity().SetPositions(amigosList);
        }
        else {
            System.out.println("No hay amigos que mostrar");
        }
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            //String JSONInfo = Request.Get(urls[0]).execute().returnContent().asString();
            HttpClient httpClient = new DefaultHttpClient();
            String allFriends = GetMethod(httpClient, urls[0]);
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
            String longi = amigoObject.getString("longi").replace(",", ".");
            String lati = amigoObject.getString("lati").replace(",", ".");

            double longiNumber;
            double latiNumber;
            try {
                longiNumber = Double.parseDouble(longi);
                latiNumber = Double.parseDouble(lati);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
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

    private static String GetMethod(HttpClient httpClient, String url) throws IOException {

        //String url = "http://localhost:54321/api/amigo";

        InputStreamReader keywordISR = new InputStreamReader(System.in);
        BufferedReader keywordBR = new BufferedReader(keywordISR);

        /*
        System.out.println("Introduce el ID del amigo del que quieres obtener informacion: ");
        url += "/" + keywordBR.readLine();
        */
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
            System.out.println(line);
            line = bufferedReader.readLine();
        }

        //EntityUtils.consume(entity);
        //getResponse.close();

        return stringResponse;
    }

}