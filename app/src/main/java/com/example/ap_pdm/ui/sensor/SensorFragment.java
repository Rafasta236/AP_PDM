package com.example.ap_pdm.ui.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ap_pdm.R;
import com.example.ap_pdm.ui.elements.BallView;

public class SensorFragment extends Fragment implements SensorEventListener {

    /*
     * SensorEventListener sensorListener;
     *
     * sensorListener = new SensorEventListener() {
     * @Override
     * public void onSensorChanged(SensorEvent event) { ... }
     *
     * @Override
     * public void onAccuracyChanged(SensorEvent even) { ... }
     * }
     */

    private SensorManager sensorManager;
    private BallView ballViewAcc;
    private BallView ballViewGyro;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private long lastTimestamp = 0;
    private DisplayMetrics displayMetrics;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);

        ballViewAcc = root.findViewById(R.id.ballViewAcc);
        ballViewGyro = root.findViewById(R.id.ballViewGyro);

        displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        float size = Math.min(screenWidth * 0.35f, screenHeight * 0.35f);

        float accLeft = screenWidth * 0.5f;
        float accTop = screenHeight * 0.4f;
        float accRight = accLeft + size;
        float accBottom = accTop + size;

        float gyroLeft = screenWidth * 0.5f;
        float gyroTop = screenHeight * 0.6f;
        float gyroRight = gyroLeft + size;
        float gyroBottom = gyroTop + size;

        ballViewAcc.setLimit(accLeft, accTop, accRight, accBottom);
        ballViewGyro.setLimit(gyroLeft, gyroTop, gyroRight, gyroBottom);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscope == null) {
            Toast.makeText(getContext(), "Gyroscope not available", Toast.LENGTH_SHORT).show();
        }
        if (accelerometer == null) {
            Toast.makeText(getContext(), "Accelerometer not available", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * SENSOR_DELAY_FASTEST -> o mais rápido possível, sem limitação (mais consumo de bateria)
         * SENSOR_DELAY_GAME -> ideal para jogos (~50 Hz), bom compromisso entre velocidade e bateria
         * SENSOR_DELAY_UI -> otimizado para interfaces de utilizador
         * SENSOR_DELAY_NORMAL -> padrão do sistema, mais lento (~200 ms), economiza bateria
         */
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*
         * TYPE_ACCELEROMETER -> mede aceleração linear do dispositivo em m/s²
         * TYPE_GYROSCOPE -> mede velocidade angular (rad/s) do dispositivo
         * TYPE_MAGNETIC_FIELD -> mede campo magnético em microteslas
         * TYPE_LIGHT -> mede luminosidade ambiente em lux
         * TYPE_PROXIMITY -> detecta proximidade de objetos (cm)
         * TYPE_PRESSURE -> mede pressão atmosférica (hPa)
         * TYPE_GRAVITY -> mede aceleração da gravidade (m/s²)
         * TYPE_LINEAR_ACCELERATION -> aceleração sem gravidade
         */

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float dx = -event.values[0];
            float dy = event.values[1];
            ballViewAcc.updatePosition(dx * 2, dy * 2);
        }

        // Usamos o dt para transformar velocidade angular no deslocamento da bola
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (lastTimestamp != 0) {
                float dt = (event.timestamp - lastTimestamp) * 1e-9f;
                float dx = event.values[1] * dt * 500;
                float dy = event.values[0] * dt * 500;
                ballViewGyro.updatePosition(dx, dy);
            }
            lastTimestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
