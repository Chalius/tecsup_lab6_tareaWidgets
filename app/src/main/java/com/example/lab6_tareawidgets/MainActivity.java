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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView tvUbicacion,txtciudad,txtdistrito;


    double latitud=0;
    double longitud;
    String ubicacion[] ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUbicacion = findViewById(R.id.tvUbicacion);
        txtciudad = findViewById(R.id.txtciudad);
        txtdistrito = findViewById(R.id.txtdistrito);


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
                // averiguar ciudad

                try {
                    if(latitud!=0) {
                        ubicacion = getMyCity(latitud, longitud);
                        txtciudad.setText(ubicacion[0]);
                        txtdistrito.setText(ubicacion[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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



        /*
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        */

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

    public String[] getMyCity(Double latitud,Double longitud) throws IOException {
        String myCity = "";
        String distrito = "";
        String completo = "";
        String ubicacion[] = new String[2];


        Geocoder geocoder = new Geocoder(
                MainActivity.this,
                Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitud,longitud,1);
        myCity = addresses.get(0).getAdminArea();//getlocality se puede cambiar por otra cosa como:pais
        distrito = addresses.get(0).getSubLocality();
        completo =addresses.toString(); //toda la información extraida de las coordenadas

        ubicacion[0] = myCity;
        ubicacion[1] = distrito;

        return ubicacion;
    }

}
