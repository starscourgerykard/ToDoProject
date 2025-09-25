package com.example.to_do_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    /**
     * Este método es llamado cuando se presiona el botón "Volver".
     * Inicia explícitamente la MainActivity.
     */
    public void goBackToMain(View view) {
        // Crea un Intent para ir a la MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // Opcional: cierra la pantalla de bienvenida para que no se quede en el historial de navegación
        finish();
    }
}