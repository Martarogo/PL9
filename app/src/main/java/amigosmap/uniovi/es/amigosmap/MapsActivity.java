package amigosmap.uniovi.es.amigosmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {

    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final String IP = "192.168.0.12";
    private final String LIST_URL = "http://" + IP + ":54321/api/amigo/";
    private final int UPDATE_PERIOD = 10000;
    private String mUserName = null;

    private static final String LOGTAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        askUserName();

        PrepareLocation();

        Timer timer = new Timer();
        TimerTask updateAmigos = new UpdateAmigoPosition();
        timer.scheduleAtFixedRate(updateAmigos, 0, UPDATE_PERIOD);
    }

    class UpdateAmigoPosition extends TimerTask {
        public void run() {
            new ShowAmigosTask().execute(LIST_URL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Toast.makeText(getApplicationContext(), "Mapa cargado", Toast.LENGTH_SHORT).show();
    }

    public void SetPositions(List<Amigo> amigosList) {
        mMap.clear();
        for (Amigo amigo: amigosList) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(amigo.GetLatitude(), amigo.GetLongitude())).title(amigo.GetName()));
        }
        Log.i(LOGTAG, "Posicion actualizada");
    }

    public void askUserName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Settings");
        alert.setMessage("User name:");

        // Crear un EditText para obtener el nombre
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mUserName = input.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getApplicationContext(), "Nombre de usuario no especificado", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();

    }

    private void PrepareLocation() {
        // Se debe adquirir una referencia al Location Manager del sistema
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Se obtiene el mejor provider de posición
        Criteria criteria = new Criteria();
        String  provider = locationManager.getBestProvider(criteria, false);

        // Se crea un listener de la clase que se va a definir luego
        MyLocationListener locationListener = new MyLocationListener();

        // Se registra el listener con el Location Manager para recibir actualizaciones
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);

        // Comprobar si se puede obtener la posición ahora mismo
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double lati = location.getLatitude();
            double longi = location.getLongitude();
            new ChangePositionTask().execute(LIST_URL + mUserName, mUserName, String.valueOf(lati), String.valueOf(longi));
        }
        else {
            String text = "Posicion de " + mUserName + " todavia no disponible";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            if (mUserName != null) {
                double lati = location.getLatitude();
                double longi = location.getLongitude();

                new ChangePositionTask().execute(LIST_URL + mUserName, mUserName, String.valueOf(lati), String.valueOf(longi));
            }

        }

        // Se llama cuando cambia el estado
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        // Se llama cuando se activa el provider
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
        }

        // Se llama cuando se desactiva el provider
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
