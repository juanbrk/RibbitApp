package com.mindsmack.ribbit.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mindsmack.ribbit.R;
import com.mindsmack.ribbit.UI.LeerMensajeActivity;
import com.mindsmack.ribbit.UI.verImagenActivity;
import com.mindsmack.ribbit.adapters.AdapterMensajes;
import com.mindsmack.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan on 20/03/2015.
 */
public class InboxFragment extends ListFragment {
    protected List<ParseObject> mMensajes;
    protected SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        obtenerMensajes();
    }

    private void obtenerMensajes() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASE_MENSAJES);
        query.whereEqualTo(ParseConstants.CLAVE_ID_RECEPTORES, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.CLAVE_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> mensajes, ParseException e) {
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if ( e == null){
                    mMensajes = mensajes;

                    String[] userNames = new String[mMensajes.size()];
                    int i = 0;
                    for (ParseObject mensaje : mMensajes) {
                        userNames[i] = mensaje.getString(ParseConstants.CLAVE_NOMBRE_EMISOR);
                        i++;
                    }
                    // Chequeamos si ya se ha creado el adapter, para no crearlo c vez que iniciamos el fragment
                    if(getListView().getAdapter() == null){
                        AdapterMensajes adapter = new AdapterMensajes
                                (getListView().getContext(),
                                        mMensajes);
                        setListAdapter(adapter);
                    } else{ // si ya se creo el adapter, solo lo recargamos con el metodo recargar al final de la clase AdapterMensaje
                        ((AdapterMensajes)getListView().getAdapter()).refill(mMensajes);
                    }



                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject mensaje = mMensajes.get(position);
        String tipoArchivo = mensaje.getString(ParseConstants.CLAVE_TIPO_ARCHIVO);
        ParseFile archivo = mensaje.getParseFile(ParseConstants.CLAVE_ARCHIVO);

        if (tipoArchivo.equals(ParseConstants.TIPO_FOTO)|| tipoArchivo.equals(ParseConstants.TIPO_VIDEO)){
            Uri uriArchivo = Uri.parse(archivo.getUrl());

            if(tipoArchivo.equals(ParseConstants.TIPO_FOTO) ){

                Intent verFotoIntent = new Intent(getActivity(), verImagenActivity.class);
                verFotoIntent.setData(uriArchivo);
                startActivity(verFotoIntent);

            } else if (tipoArchivo.equals(ParseConstants.TIPO_VIDEO) ) {
                Intent verVideoIntent = new Intent(Intent.ACTION_VIEW, uriArchivo);
                verVideoIntent.setDataAndType(uriArchivo, "video/*");
                startActivity(verVideoIntent);

            }
        } else {
            Intent leerMensajeintent = new Intent(getActivity(), LeerMensajeActivity.class);
            leerMensajeintent.putExtra(ParseConstants.CLAVE_MENSAJE, mensaje.getString(ParseConstants.CLAVE_MENSAJE));
            startActivity(leerMensajeintent);
        }

        List<String> ids = mensaje.getList(ParseConstants.CLAVE_ID_RECEPTORES);

        if (ids.size() == 1){
            mensaje.deleteInBackground();
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());
            ArrayList<String> idsAEliminar = new ArrayList<String>();
            idsAEliminar.add(ParseUser.getCurrentUser().getObjectId());
            mensaje.removeAll(ParseConstants.CLAVE_ID_RECEPTORES, idsAEliminar);
            mensaje.saveInBackground();
        }




    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            obtenerMensajes();
        }
    };
}
