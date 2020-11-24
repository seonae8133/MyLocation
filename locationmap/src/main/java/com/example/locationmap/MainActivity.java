package com.example.locationmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {

    Button btn;
    TextView tv;
    MapView mapView;

    GoogleMap map;
    SupportMapFragment magFragment;

    private final static String MAP_BUNDLE_KEY = "MAP_BUNDLE_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        tv = findViewById(R.id.textView);
        AutoPermissions.Companion.loadAllPermissions(this,101);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation();
            }
        });

        //map
        magFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        magFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("location", "지도 준비됨");
                map = googleMap;
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void startLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(getApplicationContext(), "no grant", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> providers = manager.getProviders(true);
            Location bestLocation = null;
/*            for (String provider : providers) {
                System.out.println(provider);
                Location l = manager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
                System.out.println(bestLocation);
            }*/

            bestLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(bestLocation != null) {
                double lat = bestLocation.getLatitude();
                double log =  bestLocation.getLongitude();
                String msg = "lat: "  + lat + ", log: " + log;
                tv.setText(msg);
            } else {
                Toast.makeText(getApplicationContext(), "no location", Toast.LENGTH_SHORT).show();
            }

            GpsListener gpsListener = new GpsListener();
            long mintime = 10000;
            float minDistonce = 0;
            //manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mintime, minDistonce, gpsListener);
            Toast.makeText(getApplicationContext(), "위치 요청", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDenied(int i, String[] strings) {    }

    @Override
    public void onGranted(int i, String[] strings) {    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

public void showCurrentLocation(double lat, double log){
    LatLng curPoint = new LatLng(lat, log);
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
}
    class GpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double log =  location.getLongitude();
            String msg = "lat: "  + lat + ", log: " + log;
            tv.setText(msg);

            showCurrentLocation(lat, log);
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
}
