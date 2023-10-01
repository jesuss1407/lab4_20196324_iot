package com.example.lab4_iot_20196324;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AcelerometroFragment extends Fragment implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime;
    private static final float SHAKE_THRESHOLD = 20f;// Umbral de aceleración
    private RecyclerView recyclerView;
    private ContactoAdapter adapter;
    private List<ContactoDto> listaContactos = new ArrayList<>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_acelerometro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.lista);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ContactoAdapter(getContext(), listaContactos);
        recyclerView.setAdapter(adapter);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcular la aceleración total
            double acceleration = Math.abs(x + y + z - SensorManager.GRAVITY_EARTH);

            // Verificar si se supera el umbral de agitación
            if (acceleration > SHAKE_THRESHOLD) {
                Toast.makeText(getContext(), String.format("Su aceleración: " + acceleration + " m/s^2"), Toast.LENGTH_SHORT).show();
                //Log.d("a","entre aceleracion");
                if (x > 0.7) {
                    scrollDown();
                } else {
                    scrollUp();
                }
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }


    private void scrollUp() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition > 0) {
                // Si no estás en la parte superior de la lista, desplázate hacia arriba
                recyclerView.smoothScrollToPosition(firstVisibleItemPosition - 1);
            }
        }
    }

    private void scrollDown() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition < recyclerView.getAdapter().getItemCount() - 1) {
                // Si no estás en la parte inferior de la lista, desplázate hacia abajo
                recyclerView.smoothScrollToPosition(lastVisibleItemPosition + 1);
            }
        }
    }


}