package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment {

// estas dependencias en gradle
//    implementation 'org.osmdroid:osmdroid-android:6.1.10'
//    implementation 'androidx.preference:preference:1.1.1'


// esto en el manifest
//<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//<uses-permission android:name="android.permission.INTERNET" />
//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

// esto como layout para el mapa

//<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:gravity="center"
//        android:orientation="vertical">
//
//<org.osmdroid.views.MapView
//        android:id="@+id/map"
//        android:layout_width="750dp"
//        android:layout_height="750dp" />
//
//</LinearLayout>
public class MainActivity extends AppCompatActivity {

        private MapView mapView;


        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

            mapView = findViewById(R.id.map);
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);

            IMapController mapController = mapView.getController();
            mapController.setCenter(new GeoPoint(-34.6037, -58.3816));
            mapController.setZoom(14);

            addMarkers();

            Marker startMarker = new Marker(mapView);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(startMarker);
        }

        @Override
        protected void onResume() {
            super.onResume();
            mapView.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            mapView.onPause();
        }
        private void addMarkers() {
            double[][] locations = {
                    {-34.6037, -58.3816},
                    {-34.5829, -58.4109},
                    {-34.6083, -58.3700}
            };

            for (double[] location : locations) {
                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(location[0], location[1]));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays().add(marker);
            }
        }
    }

}
