package com.example.lab4_iot_20196324;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    private TextView tvTitulo;
    private ImageView ivOjo;
    private Button btnIrA, btnAñadir;
    private ProgressBar loadingProgressBar;
    private List<ContactoDto> acelerometroLista = new ArrayList<>();
    private List<ContactoDto> magnetometroLista = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        ivOjo = findViewById(R.id.ojo);
        ivOjo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails();
            }
        });

        tvTitulo = findViewById(R.id.titulo);
        btnIrA = findViewById(R.id.irA);
        btnAñadir = findViewById(R.id.añadir);

        irAMagnetometro();

        btnIrA.setOnClickListener(v -> {
            if (tvTitulo.getText().equals("Magnetómetro")) {
                irAAcelerometro();
            } else {
                irAMagnetometro();
            }
        });


        btnAñadir.setOnClickListener(v -> {

            btnAñadir.setEnabled(false);
            btnIrA.setEnabled(false);

            // Configura la opacidad de los botones deshabilitados
            float disabledAlpha = 0.5f;
            btnAñadir.setAlpha(disabledAlpha);
            btnIrA.setAlpha(disabledAlpha);

            loadingProgressBar = findViewById(R.id.loadingProgressBar);

            // Cuando inicies la carga, muestra el ProgressBar
            loadingProgressBar.setVisibility(View.VISIBLE);

            añadirContacto();
        });

    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.irA);
        if (!navController.popBackStack()) {
            super.onBackPressed();
        }
    }

    public void deleteContact(int myPosition) {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (activeFragment instanceof AcelerometroFragment) {
            acelerometroLista.remove(myPosition);
        } else if (activeFragment instanceof MagnetometroFragment) {
            magnetometroLista.remove(myPosition);
        }
    }

    private void irAMagnetometro() {
        MagnetometroFragment fragment = new MagnetometroFragment();
        fragment.setearLista(magnetometroLista);

        tvTitulo.setText("Magnetómetro");
        btnIrA.setText("Ir a Acelerómetro");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void irAAcelerometro() {
        AcelerometroFragment fragment = new AcelerometroFragment();
        fragment.setearLista(acelerometroLista);

        tvTitulo.setText("Acelerómetro!");
        btnIrA.setText("Ir a Magnetómetro");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }



    private void añadirContacto() {
        new Thread(() -> {
            try {
                JSONObject contactJson = apiData();

                if (contactJson != null) {
                    ContactoDto contacto = parseContactData(contactJson);

                    runOnUiThread(() -> {
                        updateActiveFragment(contacto);
                        enableButtons();
                        loadingProgressBar.setVisibility(View.GONE);
                    });
                } else {
                    showErrorToast("Error al añadir contacto");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorToast("Error JSON Exception");
            }
        }).start();
    }

    private JSONObject apiData() throws IOException, JSONException {
        URL url = new URL("https://randomuser.me/api/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return new JSONObject(response.toString()).optJSONArray("results").optJSONObject(0);
            }
        }

        return null;
    }


    private ContactoDto parseContactData(JSONObject contactJson) throws JSONException {
        JSONObject nameObj = contactJson.getJSONObject("name");
        String nombre = nameObj.getString("title") + " " + nameObj.getString("first") + " " + nameObj.getString("last");
        String genero = contactJson.getString("gender");
        JSONObject locationObj = contactJson.getJSONObject("location");
        String ciudad = locationObj.getString("city");
        String pais = locationObj.getString("country");
        String email = contactJson.getString("email");
        String telefono = contactJson.getString("phone");
        String foto = contactJson.getJSONObject("picture").getString("large");

        return new ContactoDto(nombre, genero, ciudad, pais, email, telefono, foto);
    }

    private void updateActiveFragment(ContactoDto contacto) {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (activeFragment instanceof AcelerometroFragment) {
            acelerometroLista.add(contacto);
            AcelerometroFragment acelerometroFragment = (AcelerometroFragment) activeFragment;
            acelerometroFragment.actualizarLista(acelerometroLista);
        } else if (activeFragment instanceof MagnetometroFragment) {
            magnetometroLista.add(contacto);
            MagnetometroFragment magnetometroFragment = (MagnetometroFragment) activeFragment;
            magnetometroFragment.actualizarLista(magnetometroLista);
        }
    }

    private void enableButtons() {
        btnAñadir.setEnabled(true);
        btnIrA.setEnabled(true);

        // Restaura la opacidad de los botones habilitados a su estado normal
        btnAñadir.setAlpha(1.0f);
        btnIrA.setAlpha(1.0f);
    }

    private void showErrorToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(AppActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showDetails() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment == null) return;

        String title = currentFragment instanceof AcelerometroFragment ? "Detalles - Acelerómetro" :
                currentFragment instanceof MagnetometroFragment ? "Detalles - Magnetómetro" :
                        null;

        String message = currentFragment instanceof AcelerometroFragment ?
                "Haga CLICK en 'Añadir' para agregar contactos a su lista. Esta aplicación está utilizando el ACELERÓMETRO de su dispositivo.\n\n" +
                        "De este forma, la lista hará scroll hacia abajo, cuando agite su dispositivo." :
                currentFragment instanceof MagnetometroFragment ?
                        "Haga CLICK en 'Añadir' para agregar contactos a su lista. Esta aplicación está utilizando el MAGNETÓMETRO de su dispositivo.\n\n" +
                                "De esta forma, la lista se mostrará al 100% cuando se apunte al NORTE. Caso contrario, se desvanecerá..." :
                        null;

        if (title != null && message != null) {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }

}