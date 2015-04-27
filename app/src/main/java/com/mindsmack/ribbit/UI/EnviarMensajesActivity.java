package com.mindsmack.ribbit.UI;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mindsmack.ribbit.utils.ParseConstants;
import com.mindsmack.ribbit.R;


public class EnviarMensajesActivity extends ActionBarActivity {

    private Button mBotonEnviarMensaje;
    private EditText mCampoTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_mensajes);

        mCampoTexto = (EditText) findViewById(R.id.campoMensaje);
        mBotonEnviarMensaje = (Button) findViewById(R.id.enviar_mensaje_boton);

        mBotonEnviarMensaje.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = mCampoTexto.getText().toString();

                if (mensaje.equals("")){
                    Toast.makeText(EnviarMensajesActivity.this,
                            getString(R.string.mensaje_vacio_toast),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    String tipoArchivo = ParseConstants.TIPO_MENSAJE;
                    Intent pasarAReceptoresIntent = new Intent(EnviarMensajesActivity.this, ReceptoresActivity.class);
                    pasarAReceptoresIntent.putExtra(ParseConstants.CLAVE_MENSAJE, mensaje);
                    pasarAReceptoresIntent.putExtra(ParseConstants.CLAVE_TIPO_ARCHIVO, tipoArchivo);
                    startActivity(pasarAReceptoresIntent);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
