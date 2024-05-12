package com.fiubyte.bafix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract =
            new ActivityResultContracts.RequestMultiplePermissions();
    private DataViewModel dataViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private final ActivityResultLauncher<String[]> multiplePermissionLauncher =
            registerForActivityResult(multiplePermissionsContract, isGranted -> {
                if (isGranted.containsValue(false)) {
                    Toast.makeText(this, "Location permission is needed", Toast.LENGTH_LONG).show();
                } else {
                    getLocation();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar)); // FIXME: this crashes in some android
        SharedPreferencesManager.initialize(this);
        setSupportActionBar(findViewById(R.id.toolbar));

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        updateLocation();

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    boolean locationPermissionsGranted() {
        for (String permission : LOCATION_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void updateLocation() {
        if (!locationPermissionsGranted()) {
            multiplePermissionLauncher.launch(LOCATION_PERMISSIONS);
        } else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                saveLocation(location);
            }
        });
    }

    private void saveLocation(Location location) {
        Map<String, Double> currentLocation = new HashMap<>();
        currentLocation.put("latitude", location.getLatitude());
        currentLocation.put("longitude", location.getLongitude());
        dataViewModel.getCurrentLocation().setValue(currentLocation);
    }
}