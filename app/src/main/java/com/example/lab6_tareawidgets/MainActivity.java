package com.example.lab6_tareawidgets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;











public class MainActivity extends AppCompatActivity {

    TextView tvUbicacion,txtciudad,txtdistrito;

    TextView txttemperatura;
    Button btnapi;


    private int widgetId = 0;
    double latitud=0;
    double longitud;
    String ubicacion[] ;

    Button btnenviar;

    String temp,temp_min,temp_max,cielo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUbicacion = findViewById(R.id.tvUbicacion);
        txtciudad = findViewById(R.id.txtciudad);
        txtdistrito = findViewById(R.id.txtdistrito);

        btnenviar = findViewById(R.id.btn_enviar);

        txttemperatura = findViewById(R.id.txt_temperatura);
        btnapi =findViewById(R.id.btn_api);

        getUbicacion();


        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendDataToWidget();
            }
        });

        btnapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find_weather();
            }
        });


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


    public void getUbicacion(){
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


    public void sendDataToWidget(){


         /*
            //Se obtiene le intent que lanzó la apertura de la activdiad al
            //colocar widget. Se recupera parametros

            Intent recibidowidget = getIntent();
            Bundle parametros = recibidowidget.getExtras();
            if (parametros != null) {
                // Se obtiene ID de widget que se esta configurando
                widgetId = parametros.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
            }


        // Se establece un resultado por defecto(cuando se pulse el boton de
         // Atras del telefono, este será el mensaje mostrado)

            setResult(RESULT_CANCELED);
        */


        /*
        se apertura archivo de preferencias, para escribir
        datos que almacene la actividad
         */
        SharedPreferences datos = getSharedPreferences("DatosWidget", Context.MODE_PRIVATE);

        // se apertura editor para guardar datos
        SharedPreferences.Editor editor = datos.edit();

        try{
            editor.putString("ciudad", ubicacion[0]);
            editor.putString("distrito",ubicacion[1]);
            //editor.putString("latitud",""+latitud);
            //editor.putString("longitud", ""+longitud);
            editor.putString("temperatura", temp);
            editor.putString("temp_max", temp_max);
            editor.putString("temp_min",temp_min);
            editor.putString("cielo",cielo);

            //aplica cambios
            editor.commit();

            // se devuelve como resultado: ACEPTAR(RESULT_OK)
            Intent resultado = new Intent();
            resultado.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultado);
            finish();
        } catch(Exception e){
            Toast.makeText(getApplicationContext(),
                    "Todavía no se obtuvo la ubicación",Toast.LENGTH_SHORT).show();
        }

    }


    public void find_weather(){


        String url="https://api.openweathermap.org/data/2.5/weather?lat="+latitud+"&lon="+longitud+"&appid=c78a8d2bc11d77f8fd5f922ee00a9c24&units=metric";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main_object = response.getJSONObject("main");
                            JSONArray array = response.getJSONArray("weather");
                            JSONObject object = array.getJSONObject(0);

                            temp = String.valueOf(main_object.getDouble("temp"));
                            temp_min = String.valueOf(main_object.getDouble("temp_min"));
                            temp_max = String.valueOf(main_object.getDouble("temp_max"));
                            cielo = object.getString("main");
                            String description = object.getString("description");
                            String city = response.getString("name");

                            txttemperatura.setText(temp);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "error con la API",Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);


    }



}
