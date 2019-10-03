package com.example.lab6_tareawidgets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Button btnGPS;
    TextView tvUbicacion;

    double latitud=0;
    double longitud;

    TextView txtciudad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUbicacion = findViewById(R.id.tvUbicacion);
        btnGPS = findViewById(R.id.button);
        txtciudad = findViewById(R.id.txtciudad);


































        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Acquire a reference to the system Location Manager
                LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

                // Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.

                        //makeUseOfNewLocation(location);
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        tvUbicacion.setText(""+latitud+" "+longitud);

                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                // Register the listener with the Location Manager to receive location updates
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                // averiguar ciudad

                try {
                    if(latitud!=0) {
                        String ciudad = getMyCity(latitud, longitud);
                        txtciudad.setText(ciudad);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });



        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }



    }

    public String getMyCity(Double latitud,Double longitud) throws IOException {
        String myCity = "";

        Geocoder geocoder = new Geocoder(
                MainActivity.this,
                Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitud,longitud,1);
        //String address = addresses.get(0).getAddressLine(0);
        myCity = addresses.get(0).getLocality();//getlocality se puede cambiar por otra cosa como:pais

        return myCity;
    }

}
