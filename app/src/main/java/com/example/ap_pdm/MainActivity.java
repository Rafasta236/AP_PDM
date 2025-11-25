package com.example.ap_pdm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.ap_pdm.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    boolean debug = true;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> { // basic navigation handling
            Fragment f = null;
            int id = item.getItemId();

            if (id == R.id.nav_map) f = new com.example.ap_pdm.ui.map.MapFragment();
            if (id == R.id.nav_camera) f = new com.example.ap_pdm.ui.camera.CameraFragment();
            if (id == R.id.nav_sensor) f = new com.example.ap_pdm.ui.sensor.SensorFragment();

            if (f == null) return false;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f)
                    .commit();

            return true;
        });

        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_map);
        }
    }
    public void replace(Fragment f){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }
}