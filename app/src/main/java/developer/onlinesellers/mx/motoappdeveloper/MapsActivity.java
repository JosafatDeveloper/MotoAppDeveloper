package developer.onlinesellers.mx.motoappdeveloper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import developer.onlinesellers.mx.motoappdeveloper.servicios.Servicios;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import android.graphics.Color;

import android.provider.Settings.Secure;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Timer timer;
    private Timer Savetimer;
    private Servicios servicio;
    double latitud;
    double longitud;

    private String User_string = "";
    private String Viaje_string = "";
    float duracion = 0;
    boolean ReadySave = false;
    Polyline polyline;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        servicio = new Servicios(getApplicationContext());
        servicio.printCoordenadas();

        Button boton = (Button) findViewById(R.id.playbtn);
        boton.setText("Iniciar Viaje");
        boton.setBackgroundColor(Color.GREEN);
        boton.setOnClickListener(this);

        timer = new Timer();
        Savetimer = new Timer();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //saveDataServe();

    }
    @Override
    public void onClick (View v)
    {
        // Para tener control se condiciona la identificacion del boton al que se le dio click
        // de estaforma solo se ejecuta el codigo correspondiente a ese boton
        if (v.getId() == R.id.playbtn) {
            Button boton = (Button)
                    findViewById(R.id.playbtn);
            if(boton.getText().toString() == "Detener Grabación"){
                boton.setText("Iniciar Viaje");
                boton.setBackgroundColor(Color.GREEN);
                ReadySave = false;
                duracion = 0;
                servicio.distancia = 0;
                timer.cancel();
                Savetimer.cancel();
                timer = null;
                Savetimer = null;
            }else{
                boton.setText("Detener Grabación");
                boton.setBackgroundColor(Color.RED);
                AlertasShowViaje();
                AlertasShowUser();
            }
        }


    }

    public void AlertasShowUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alias o Usuario *obligatorio");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString() != null && !input.getText().toString().isEmpty()) {
                    User_string = input.getText().toString()+"-"+ Math.random();
                } else {
                    User_string = "USER-" + Math.random();
                }

            }
        });


        builder.show();
    }
    public void AlertasShowViaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre del viaje *obligatorio");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString() != null && !input.getText().toString().isEmpty()){
                    Viaje_string = "a-"+input.getText().toString()+"-"+ Math.random();
                }else {
                    Viaje_string = "VIAJE-" + Math.random();
                }
                ReadySave = true;
            }
        });


        builder.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        latitud = servicio.getLocationLatitude();
        longitud = servicio.getLocationLongitud();
        playLocate();
        playlocateSave();

    }

    public void playLocate() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //saveDataServe();
                        if (ReadySave) {
                            addlocatemap();
                        }
                    }
                });
            }
        }, 0, 3000);
    }
    public void playlocateSave(){
        Savetimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //saveDataServe();
                        if (ReadySave) {
                            duracion += 15;
                            addlocate();
                        }
                    }
                });
            }
        }, 0, 15000);
    }

    public void addlocate() {
        //servicio.printCoordenadas();
        //saveDataServe();
        new MyAsyncTask().execute();
    }
    public void addlocatemap() {
        if(latitud == 0.000000000000000){

        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(servicio.getLocationLatitude(), servicio.getLocationLongitud()), 18));
            mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(new LatLng(latitud, longitud))
                    .add(new LatLng(servicio.getLocationLatitude(), servicio.getLocationLongitud())));
            latitud = servicio.getLocationLatitude();
            longitud = servicio.getLocationLongitud();
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... parametros) {
            // TODO Auto-generated method stub
            saveDataServe(latitud, longitud);
            return null;
        }

        protected void onPostExecute(Double result){
            /*
            pb.setVisibility(View.GONE);
            Toas.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
            */
        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }
    }

    public void saveDataServe(double lat, double log){
        HttpURLConnection urlc = null;
        OutputStreamWriter out = null;
        DataOutputStream dataout = null;
        BufferedReader in = null;
        try {
            URL url = new URL("http://squashmex.com.mx/api_motoapp/DeveloperSaveSatusMovil.php");
            urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestMethod("POST");
            urlc.setDoOutput(true);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            urlc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            dataout = new DataOutputStream(urlc.getOutputStream());
            // perform POST operation
            String rss = "r={\"d\":"+servicio.distancia+",\"u\":"+duracion+",\"v\":\""+URLEncoder.encode(Viaje_string, "utf-8")+"\",\"n\":\""+ URLEncoder.encode(User_string, "utf-8")+"\"}";
            String lss = "l={\"l\":[{\"la\":"+lat+",\"lo\":"+log+"}]}";
            dataout.writeBytes(rss+"&"+lss);
            int responseCode = urlc.getResponseCode();
            in = new BufferedReader(new InputStreamReader(urlc.getInputStream()),8096);
            String response;
            // write html to System.out for debug
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
            in.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}