package com.example.mylocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    Button button;
    TextView txtView;
    SupportMapFragment mapFragment; //지도 view
    GoogleMap map;  //지도 data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoPermissions.Companion.loadAllPermissions(this, 101);
        button = findViewById(R.id.button);
        txtView = findViewById(R.id.txtView);

        button.setOnClickListener((v) -> {
            startLocationService();
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("Map", "지도 준비됨");
                map = googleMap;
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .title("Marker"));
            }
        });
        MapsInitializer.initialize(this);

    }//end of onCreate

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @SuppressLint("MissingPermission")
    public void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try { //바로 최근 위치 정보 확인
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //가장 최근의 위치 가져옴 (위치 정해주는거 : GPS_PROVIDER)
            List<String> providers = manager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                System.out.println(provider);
                Location l = manager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) { //;.getAccuracy(): 정확도 (현재 위치값과 이전 위치값이 ㄷㅏ르면)
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }

            if (location != null) {
                GPSListener gpsListner = new GPSListener();
                long minTime = 10000;
                float minDistance = 0;
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListner);
                Toast.makeText(getApplicationContext(), "내 위치 확인 요청함", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showCurrentLocation(double lat, double log) {
        LatLng curPoint = new LatLng(lat, log);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
    }

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude(); //위도
            double longitude = location.getLongitude(); //경도
            String message = "최근 위치: " + latitude + ", " + longitude;
            txtView.setText(txtView.getText() + "\n" + message);
            showCurrentLocation(latitude,longitude);
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