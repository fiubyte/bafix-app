package com.fiubyte.bafix.utils;

import android.graphics.drawable.Drawable;

import com.fiubyte.bafix.BuildConfig;
import com.fiubyte.bafix.entities.CustomInfoWindow;
import com.fiubyte.bafix.entities.ProviderData;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class OpenStreetMapManager {
    private static MapView mapView;
    private static Drawable locationIcon;
    private static final int MAP_INITIAL_ZOOM = 14;

    private static GeoPoint initialLocation;

    public static void initializeMap(MapView mapView, Drawable locationIcon, GeoPoint initialLocation) {
        OpenStreetMapManager.mapView = mapView;
        OpenStreetMapManager.locationIcon = locationIcon;
        OpenStreetMapManager.initialLocation = initialLocation;
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setupMapView();
    }

    private static void setupMapView() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setCenter(initialLocation);
        mapController.setZoom(MAP_INITIAL_ZOOM);
    }

    public static void addMarkers(ArrayList<ProviderData> providers) {
        for (ProviderData provider : providers) {
            CustomInfoWindow customInfoWindow = new
                    CustomInfoWindow(mapView,
                                     provider.getName(),
                                     provider.getServicesOffered());

            Marker marker = new Marker(mapView);
            marker.setPosition(provider.getGeoPosition());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(locationIcon);
            marker.setInfoWindow(customInfoWindow);
            mapView.getOverlays().add(marker);
        }

       /* double[][] locations = {
                {-34.6037, -58.3816},
                {-34.5829, -58.4109},
                {-34.6083, -58.3700}
        };

        ArrayList<String> servicesList = new ArrayList<String>();
        servicesList.add("Reparación de cañerías de agua caliente");
        servicesList.add("Pintura de interiores");
        servicesList.add("Reparación de grietas en el techo");
        servicesList.add("Fabricación de muebles a medida para cocina");

        CustomInfoWindow customInfoWindow = new CustomInfoWindow(mapView, "Mario Delgado",
        servicesList);

        for (double[] location : locations) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(location[0], location[1]));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(locationIcon);
            marker.setInfoWindow(customInfoWindow);
            mapView.getOverlays().add(marker);
        }*/
    }
}
