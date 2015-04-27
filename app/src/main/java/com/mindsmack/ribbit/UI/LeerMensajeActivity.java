package com.mindsmack.ribbit.UI;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mindsmack.ribbit.utils.ParseConstants;
import com.mindsmack.ribbit.R;


public class LeerMensajeActivity extends ActionBarActivity {

    TextView mMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_mensaje);

        Intent intentAnterior= getIntent();
        String mensaje = intentAnterior.getStringExtra(ParseConstants.CLAVE_MENSAJE).toString();

        mMensaje = (TextView) findViewById(R.id.mensajeTextView);
        mMensaje.setText(mensaje);
    }



}
