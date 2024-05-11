package com.fiubyte.bafix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
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

        // Get the intent that started this activity
        Intent intent = getIntent();
        if (!Objects.equals(intent.getAction(), "android.intent.action.MAIN")) {
            // TODO: gather the real data from the API
            String requestedPath = intent.getData().getLastPathSegment(); // whatever comes after "https://bafix-api.onrender.com/"
            Bundle bundle = new Bundle();
            bundle.putInt("rating", 4);
            ServiceData serviceData = new ServiceData(1, "Prueba", "data:image/jpeg;base64,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", "data:image/jpeg;base64,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", 30.0, "Tomas", 1, true, null, "1144332211", "Lu", "9:00", "9:00,23:00", true);
            bundle.putParcelable("serviceData", serviceData);
            Navigation.findNavController(this, R.id.main_fragment_container_view).navigate(R.id.serviceFragment, bundle);
        }
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