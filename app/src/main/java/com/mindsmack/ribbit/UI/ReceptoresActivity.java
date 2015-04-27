package com.mindsmack.ribbit.UI;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindsmack.ribbit.adapters.AdapterUsuario;
import com.mindsmack.ribbit.utils.FileHelper;
import com.mindsmack.ribbit.utils.ParseConstants;
import com.mindsmack.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ReceptoresActivity extends ActionBarActivity {
    public static final String TAG = ReceptoresActivity.class.getSimpleName();

    protected List<ParseUser> mAmigos;
    protected ParseUser mUsuarioActual;
    protected ParseRelation<ParseUser> mRelacionAmigos;
    protected Uri mMediaUri;
    protected String mTipoArchivo;
    protected String mMensaje;

    protected MenuItem mEnviarMenu;
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_amigos);

        mMediaUri = getIntent().getData();
        mTipoArchivo = getIntent().getExtras().getString(ParseConstants.CLAVE_TIPO_ARCHIVO);
        mMensaje = getIntent().getExtras().getString(ParseConstants.CLAVE_MENSAJE);

        mGridView = (GridView) findViewById(R.id.gridViewAmigos);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        TextView mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);

        Log.d(TAG, mMensaje + " "+  mTipoArchivo);
    }

    @Override
    public void onResume() {
        super.onResume();

        mUsuarioActual = ParseUser.getCurrentUser();
        mRelacionAmigos = mUsuarioActual.getRelation(ParseConstants.KEY_RELACION);

        mRelacionAmigos.getQuery().findInBackground( new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> amigos, ParseException e) {
                if (e == null) {
                    mAmigos = amigos;

                    String[] userNames = new String[mAmigos.size()];
                    int i = 0;
                    for (ParseUser user : mAmigos){
                            userNames[i] = user.getUsername();
                            i++;
                }

                    if (mGridView.getAdapter() == null) {
                        AdapterUsuario adapter = new AdapterUsuario(ReceptoresActivity.this, mAmigos);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((AdapterUsuario) mGridView.getAdapter()).refill(mAmigos);

                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceptoresActivity.this);
                    builder.setTitle(getString(R.string.dialogo_error_buscando_amigos_titulo))
                            .setMessage(getString(R.string.dialogo_error_buscando_amigos_mensaje))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialogo = builder.create();
                    dialogo.show();

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receptores, menu);
        mEnviarMenu = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_send:
                ParseObject mensaje = crearMensaje();

                if (mensaje == null){
                    AlertDialog.Builder constructor = new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.error_general_titulo_dialogo))
                            .setMessage(getString(R.string.error_seleccionando_archivo_dialogo))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialogo = constructor.create();
                    dialogo.show();
                } else{
                    enviar(mensaje);
                    finish();
                }

        }


        return super.onOptionsItemSelected(item);
    }

    protected void enviar(ParseObject mensaje) {
        mensaje.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(ReceptoresActivity.this, getString(R.string.mensaje_enviado), Toast.LENGTH_LONG).show();

                    enviarNotificacionPush();

                } else {
                    AlertDialog.Builder constructor = new AlertDialog.Builder(ReceptoresActivity.this)
                            .setTitle(getString(R.string.error_general_titulo_dialogo))
                            .setMessage(getString(R.string.error_enviando_mensaje_dialogo))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialogo = constructor.create();
                    dialogo.show();
                }
            }
        });
    }

    protected ParseObject crearMensaje(){
        ParseObject mensaje = new ParseObject(ParseConstants.CLASE_MENSAJES);
        mensaje.put(ParseConstants.CLAVE_ID_EMISOR, ParseUser.getCurrentUser().getObjectId());
        mensaje.put(ParseConstants.CLAVE_NOMBRE_EMISOR, ParseUser.getCurrentUser().getUsername());
        mensaje.put(ParseConstants.CLAVE_ID_RECEPTORES, getidReceptores());
        if (mTipoArchivo != null) {
            if (mTipoArchivo.equals(ParseConstants.TIPO_FOTO)
                    || mTipoArchivo.equals(ParseConstants.TIPO_VIDEO)){
            mensaje.put(ParseConstants.CLAVE_TIPO_ARCHIVO, mTipoArchivo);
            byte[] bytesArchivo = FileHelper.getByteArrayFromFile(this, mMediaUri);

            if(bytesArchivo == null){
                Toast.makeText(this, getString(R.string.error_general),Toast.LENGTH_LONG).show();
                return null;
            } else {
                if (mTipoArchivo.equals(ParseConstants.TIPO_FOTO)){
                    bytesArchivo = FileHelper.reduceImageForUpload(bytesArchivo);
                }

                String nombreArchivo = FileHelper.getFileName(this, mMediaUri, mTipoArchivo);
                ParseFile archivo = new ParseFile(nombreArchivo, bytesArchivo);
                mensaje.put(ParseConstants.CLAVE_ARCHIVO, archivo);
            } } else  {
            mensaje.put(ParseConstants.CLAVE_MENSAJE, mMensaje);
            mensaje.put(ParseConstants.CLAVE_TIPO_ARCHIVO, mTipoArchivo);
        }
        }
        return mensaje;
    }

    protected ArrayList<String> getidReceptores(){
        ArrayList<String> idsReceptores = new ArrayList<String>();
        for (int i =0; i <mGridView.getCount(); i++){
            if(mGridView.isItemChecked(i)){
                idsReceptores.add(mAmigos.get(i).getObjectId());
            }
        }
        return idsReceptores;
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkmarkUsuarioImageView);
            if (mGridView.getCheckedItemCount() > 0 ){
                mEnviarMenu.setVisible(true);
            } else {
                mEnviarMenu.setVisible(false);
            }
            if (mGridView.isItemChecked(position)) {

                checkImageView.setVisibility(View.VISIBLE);

            } else {
                checkImageView.setVisibility(View.INVISIBLE);
            }
        }
    };

    protected void enviarNotificacionPush(){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.CLAVE_ID_USUARIO, getidReceptores());
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_nuevo_ribbit, ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
    }

}
