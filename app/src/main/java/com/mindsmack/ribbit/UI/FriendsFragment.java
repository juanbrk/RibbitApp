package com.mindsmack.ribbit.UI;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.mindsmack.ribbit.R;
import com.mindsmack.ribbit.adapters.AdapterUsuario;
import com.mindsmack.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Juan on 20/03/2015.
 */
public class FriendsFragment extends Fragment {
    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mAmigos;
    protected ParseUser mUsuarioActual;
    protected ParseRelation<ParseUser> mRelacionAmigos;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_amigos, container, false);
        mGridView = (GridView)rootView.findViewById(R.id.gridViewAmigos);
        TextView mEmptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);
        return rootView;
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
                    for (ParseUser user : mAmigos) {
                        userNames[i] = user.getUsername();
                        i++;
                    }
                    if(mGridView.getAdapter() == null) {
                        AdapterUsuario adapter = new AdapterUsuario(getActivity(), mAmigos);
                        mGridView.setAdapter(adapter);
                    } else{
                        ((AdapterUsuario) mGridView.getAdapter()).refill(mAmigos);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.dialogo_error_buscando_amigos_titulo))
                            .setMessage(getString(R.string.dialogo_error_buscando_amigos_mensaje))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialogo = builder.create();
                    dialogo.show();

                }
            }
        });
    }
}
