package developer.onlinesellers.mx.motoappdeveloper.servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

/**
 * Created by dis2 on 07/04/16.
 */
public class Servicios extends Service implements LocationListener {
    private Context ctx;
    double latitud;
    double longitud;
    Location location;
    boolean GPSActive;
    protected LocationManager locationManager;

    public Servicios() {
        super();
        this.ctx = this.getApplicationContext();
    }

    public Servicios(Context c) {
        super();
        this.ctx = c;
        getlocation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void printCoordenadas(){
        Log.e("Cooredenadas: ", latitud + ", " + longitud);
    }

    public void getlocation() {
        try {
            locationManager = (LocationManager)ctx.getSystemService(LOCATION_SERVICE);
            GPSActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (GPSActive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation

                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    } else {
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300 * 60, 10, this);
                if(locationManager != null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                }
            }
        } catch (Exception e) {
            Log.d("Error", "Error en el servicio:"+e.toString());
        }


    }

    public double getLocationLatitude(){
        return latitud;
    }
    public double getLocationLongitud(){
        return longitud;
    }
    @Override
    public void onLocationChanged(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();
        Log.d("ChangeLocate", "Se cambio la localización");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
