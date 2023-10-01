package com.example.lab4_iot_20196324;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MagnetometroFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private RecyclerView contactListView;
    private ContactoAdapter adapter;
    private List<ContactoDto> listaContactos = new ArrayList<>();


    private float azimuth;


    private boolean contactListVisible = true;
    public MagnetometroFragment() {
        // Required empty public constructor
    }


    public static MagnetometroFragment newInstance(String param1, String param2) {
        MagnetometroFragment fragment = new MagnetometroFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_magnetometro, container, false);

        contactListView = view.findViewById(R.id.lista);

        // Configura el sensor de brújula
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactListView= view.findViewById(R.id.lista);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        contactListView.setLayoutManager(layoutManager);

        adapter = new ContactoAdapter(getContext(), listaContactos);
        contactListView.setAdapter(adapter);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



    }
    public void actualizarLista(List<ContactoDto> contactos) {
        this.listaContactos.clear();
        this.listaContactos.addAll(contactos);
        adapter.notifyDataSetChanged();
    }

    public void setearLista(List<ContactoDto> contactos) {
        this.listaContactos.clear();
        this.listaContactos.addAll(contactos);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registra los sensores cuando el fragmento está en primer plano
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onPause() {
        super.onPause();
        // Detiene la detección de sensores cuando el fragmento está en segundo plano
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        // Calcula la orientación del dispositivo
        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float) Math.toDegrees(orientation[0]);
            // La variable "azimuth" contiene el ángulo con respecto al norte

            // Calcula el porcentaje de visibilidad en función del ángulo
            float visibilityPercentage = calculateVisibilityPercentage(azimuth);

            // Actualiza la visibilidad de la lista de contactos
            updateContactListVisibility(visibilityPercentage);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float calculateVisibilityPercentage(float azimuth) {
        if (azimuth >= 0 && azimuth < 45) {
            return 100.0f; // 100% de visibilidad
        } else if (azimuth >= 45 && azimuth < 90) {
            return 75.0f; // 75% de visibilidad
        } else if (azimuth >= 90 && azimuth < 135) {
            return 50.0f; // 50% de visibilidad
        } else if (azimuth >= 135 && azimuth < 180) {
            return 20.0f; // 25% de visibilidad
        } else if (azimuth >= 180 && azimuth < 225) {
            return 0.0f; // 5% de visibilidad
        } else if (azimuth >= 225 && azimuth < 270) {
            return 20.0f;
        } else if (azimuth >= 270 && azimuth < 315) {
            return 50.0f;
        } else if (azimuth >= 315 && azimuth < 359) {
            return 70.0f;
        } else {
            return 0.0f; // 0% de visibilidad
        }
    }

    private void updateContactListVisibility(float percentage) {
        // Ajusta la visibilidad de la lista de contactos en función del porcentaje
        contactListView.setAlpha(percentage / 100.0f);
    }


}