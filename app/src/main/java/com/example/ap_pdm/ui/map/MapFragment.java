package com.example.ap_pdm.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ap_pdm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.*;



public class MapFragment extends Fragment {

    private GoogleMap map;
    private FusedLocationProviderClient fused;

    private final Map<Long, Marker> markers = new HashMap<>(); // map for markers

    private static final int REQ_LOC = 1001;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        fused = LocationServices.getFusedLocationProviderClient(requireContext());
        initMap();

        Button btnNormal = v.findViewById(R.id.btnNormal);
        Button btnSatellite = v.findViewById(R.id.btnSatellite);
        Button btnTerrain = v.findViewById(R.id.btnTerrain);
        Button btnHybrid = v.findViewById(R.id.btnHybrid);

        btnHybrid.setOnClickListener(view -> map.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        btnNormal.setOnClickListener(view -> map.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        btnSatellite.setOnClickListener(view -> map.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnTerrain.setOnClickListener(view -> map.setMapType(GoogleMap.MAP_TYPE_TERRAIN));

    }

    private void initMap() {
        SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map); // get the supportMapFragment
        if (smf == null) return;
        smf.getMapAsync(this::onMapReady); // if not null, when the map is ready call onMapReady
    }

    private void onMapReady(GoogleMap gm) {
        map = gm;

        map.getUiSettings().setZoomControlsEnabled(true); // turn on UI Setting (zoom buttons)

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override public void onMarkerDragStart(Marker m) {}
            @Override public void onMarkerDrag(Marker m) {}
            @Override public void onMarkerDragEnd(Marker m) {
                LatLng newPos = m.getPosition();
                Toast.makeText(getContext(),
                        "Nova posição: " + newPos.latitude + ", " + newPos.longitude,
                        Toast.LENGTH_SHORT).show();
            }
        });

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // listeners
        setupInfoWindowClick(); // click on a marker -> print
        setupLongClickCreate(); // long click on map -> create property

        enableMyLocationAndCenter(); // activate location

    }
    private void setupInfoWindowClick() {
        // listener when a marker is clicked
        map.setOnInfoWindowClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof Long) {
                long pid = (Long) tag;
                LatLng position = marker.getPosition();
                Toast.makeText(getContext(),
                        "Clicaste no marcador - Lat: " + position.latitude + ", Lng: " + position.longitude ,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLongClickCreate() {
        // listener for the long click on the map
        map.setOnMapLongClickListener(latLng -> {
            Toast.makeText(getContext(),
                    "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude,
                    Toast.LENGTH_SHORT).show();

            map.addMarker(new MarkerOptions().position(latLng).title("Novo Marcador").draggable(true));
        });

    }


    private void enableMyLocationAndCenter() {
        if (map == null) return;

        // if no permission then asks for permission on location, else it tries to get last location via FusedLocationProviderClient

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOC);
            return;
        }

        map.setMyLocationEnabled(true);

        fused.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) {
                LatLng me = new LatLng(loc.getLatitude(), loc.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(me, 15f));
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == REQ_LOC && res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocationAndCenter();
        } else {
            Toast.makeText(requireContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show();
        }
    }
}
