package com.mindsmack.ribbit.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mindsmack.ribbit.utils.ParseConstants;
import com.mindsmack.ribbit.R;
import com.mindsmack.ribbit.adapters.SectionsPagerAdapter;
import com.parse.ParseUser;

import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    public static final int REQUEST_SACAR_FOTO = 0;
    public static final int REQUEST_GRABAR_VIDEO = 1;
    public static final int REQUEST_SELECCIONAR_FOTO = 2;
    public static final int REQUEST_SELECCIONAR_VIDEO = 3;

    public static final int MEDIA_TYPE_IMAGEN = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int TAMANO_LIMITE_ARCHIVO = 1024 * 1024 *10;

    protected Uri mMediaUri;


    protected DialogInterface.OnClickListener mDialogoListener =
            new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    Intent sacarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaUri(MEDIA_TYPE_IMAGEN);

                    if (mMediaUri == null){
                        Toast.makeText(MainActivity.this, getString(R.string.error_dispositivo_almacenamiento), Toast.LENGTH_LONG).show();
                    }
                    sacarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                    startActivityForResult(sacarFotoIntent,REQUEST_SACAR_FOTO );

                    break;
                case 1:
                    Intent grabarVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaUri(MEDIA_TYPE_VIDEO);

                    if (mMediaUri == null){
                        Toast.makeText(MainActivity.this, getString(R.string.error_dispositivo_almacenamiento), Toast.LENGTH_LONG).show();
                    }
                    else {
                        grabarVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        grabarVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                        grabarVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);

                        startActivityForResult(grabarVideoIntent,REQUEST_GRABAR_VIDEO);

                    }
                    break;
                case 2:
                    Intent elegirFotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    elegirFotoIntent.setType("image/*");
                    startActivityForResult(elegirFotoIntent, REQUEST_SELECCIONAR_FOTO);

                    break;

                case 3:
                    Intent elegirVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    elegirVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, getString(R.string.advertencia_peso_video), Toast.LENGTH_LONG).show();
                    startActivityForResult(elegirVideoIntent,REQUEST_SELECCIONAR_VIDEO);
                    break;
            }
        }

                private Uri getOutputMediaUri(int mediaType) {
                    if (isExternalStorageAvailable()){
                        String appName = MainActivity.this.getString(R.string.app_name);
                        //1- Obtener el directorio externo
                        File directorio = new File
                                (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);
                        //2- Crear el subdirectorio para la app
                        if (!directorio.exists()){
                            if(!directorio.mkdirs()){
                                Log.e(TAG, "HUBO UN ERROR CREANDO EL DIRECTORIO" );
                                return null;
                            }
                        }
                        //3- Crear un nombre p el archivo
                        //4- Crear el archivo
                        File archivoMedia;
                        Date ahora = new Date();
                        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(ahora);
                        String path = directorio.getPath()+File.separator;

                        if (mediaType == MEDIA_TYPE_IMAGEN){
                            archivoMedia = new File(path+"IMG_"+fecha+".jpg");
                        } else if (mediaType == MEDIA_TYPE_VIDEO){
                            archivoMedia = new File(path+"VID_"+fecha+".mp4");
                        }else {
                            return null;
                        }
                        Log.d(TAG, "Archivo: " + Uri.fromFile(archivoMedia));
                        //5- Retornar la Uri del archivo.
                        return Uri.fromFile(archivoMedia);

                    }else {
                        return null;
                    }
                }


            };

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        } else{
            return false;
        }
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {

            navegarALogin();

        } else {

            Log.i(TAG, "from MainACtivity: "+currentUser.getUsername());

        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_SELECCIONAR_FOTO || requestCode == REQUEST_SELECCIONAR_VIDEO) {
                if (data == null) {
                    Toast.makeText(this, R.string.error_general, Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();
                }

                if (requestCode == REQUEST_SELECCIONAR_VIDEO){
                    int tamanoArchivo = 0;
                    InputStream inputStream = null;
                    try{
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        tamanoArchivo = inputStream.available();
                    }catch(FileNotFoundException e){
                        Toast.makeText(this, getString(R.string.file_not_found_exception),Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e){
                        Toast.makeText(this, getString(R.string.error_general),Toast.LENGTH_LONG).show();
                        return;
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    if (tamanoArchivo >= TAMANO_LIMITE_ARCHIVO){
                        Toast.makeText(this, getString(R.string.archivo_excede_limite), Toast.LENGTH_LONG).show();
                        return;
                    }

                }
            } else {

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(mMediaUri);
                sendBroadcast(intent);


            }

            Intent pasarAReceptoresIntent = new Intent(this, ReceptoresActivity.class);
            pasarAReceptoresIntent.setData(mMediaUri);

            String tipoArchivo;

            if(requestCode == REQUEST_SELECCIONAR_FOTO || requestCode == REQUEST_SACAR_FOTO){
               tipoArchivo = ParseConstants.TIPO_FOTO;
            } else {
                tipoArchivo = ParseConstants.TIPO_VIDEO;
            }
            pasarAReceptoresIntent.putExtra(ParseConstants.CLAVE_TIPO_ARCHIVO,tipoArchivo);
            startActivity(pasarAReceptoresIntent);

        } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.error_general), Toast.LENGTH_LONG).show();
            }

        }

    public void navegarALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.accion_salir:
                ParseUser.logOut();
                navegarALogin();
                break;
            case R.id.accion_editar_amigos:
                Intent intent = new Intent(this, EditarAmigosActivity.class);
                startActivity(intent);
                break;
            case R.id.accion_camara:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.opciones_camara_menu_dialogo, mDialogoListener);
                AlertDialog dialogo = builder.create();
                dialogo.show();
                break;
            case R.id.accion_enviar_mensaje:
                Intent enviarMsjIntent = new Intent(MainActivity.this, EnviarMensajesActivity.class);
                startActivity(enviarMsjIntent);

        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * A placeholder fragment containing a simple view.
     */

}
