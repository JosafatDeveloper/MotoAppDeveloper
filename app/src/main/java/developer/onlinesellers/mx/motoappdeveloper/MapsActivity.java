package developer.onlinesellers.mx.motoappdeveloper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import developer.onlinesellers.mx.motoappdeveloper.servicios.Servicios;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Timer timer;
    private Servicios servicio;
    double latitud;
    double longitud;

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
        timer = new Timer();
        playLocate();
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
        }else{
            mMap.setMyLocationEnabled(true);
        }

    }

    public void playLocate(){
        /*
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(servicio.getLocationLatitude(), servicio.getLocationLongitud()), 15));
                */
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                latitud = servicio.getLocationLatitude();
                longitud = servicio.getLocationLongitud();
                addlocate();
            }
        }, 0, 30000);

    }
    public void addlocate(){
        servicio.printCoordenadas();

        /*
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitud, longitud), 15));

        mMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(servicio.getLocationLatitude(), servicio.getLocationLongitud()))  // Print mapa
        );
        */
        if(this.mMap != null){
            this.mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(new LatLng(latitud, longitud))  // Print mapa
            );
            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(latitud, longitud), 15));
        }


    }

}