package com.mindsmack.ribbit.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindsmack.ribbit.adapters.AdapterUsuario;
import com.mindsmack.ribbit.utils.ParseConstants;
import com.mindsmack.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditarAmigosActivity extends Activity {
    public static final String TAG = EditarAmigosActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseUser mUsuarioActual;
    protected ParseRelation<ParseUser> mRelacionAmigos;
    protected GridView mGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_amigos);

        mGridView = (GridView) findViewById(R.id.gridViewAmigos);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        TextView mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);
        mGridView.setOnItemClickListener(mOnClickListener);


    }

    @Override
    protected void onResume() {
        super.onResume();

        mUsuarioActual = ParseUser.getCurrentUser();
        mRelacionAmigos = mUsuarioActual.getRelation(ParseConstants.KEY_RELACION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    mUsers = parseUsers;
                    String[] userNames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        userNames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        AdapterUsuario adapter = new AdapterUsuario(EditarAmigosActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((AdapterUsuario) mGridView.getAdapter()).refill(mUsers);
                    }

                    agregarMarcasAmigos();


                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditarAmigosActivity.this);
                    builder.setTitle(getString(R.string.dialogo_error_buscando_amigos_titulo))
                            .setMessage(getString(R.string.dialogo_error_buscando_amigos_mensaje))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialogo = builder.create();
                    dialogo.show();
                }
            }
        });


    }

    private void agregarMarcasAmigos() {
        mRelacionAmigos.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> amigos, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser usuario = mUsers.get(i);

                        for (ParseUser amigo : amigos) {
                            if (amigo.getObjectId().equals(usuario.getObjectId())) {
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }

                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_amigos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkmarkUsuarioImageView);
            if (mGridView.isItemChecked(position)) {

                mRelacionAmigos.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
                mUsuarioActual.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error guardando la relacion en backend: " + e.getMessage());
                        }
                    }
                });
            } else {
                mRelacionAmigos.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }
                mUsuarioActual.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error guardando la relacion en backend: " + e.getMessage());
                        }
                    }
                });
            };
        };
    }
