package com.mindsmack.ribbit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindsmack.ribbit.R;
import com.mindsmack.ribbit.utils.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Juan on 06/04/2015.
 */
public class AdapterUsuario extends ArrayAdapter<ParseUser> {
    protected Context mContext;
    protected List<ParseUser> mUsuarios;

    public AdapterUsuario(Context context, List<ParseUser> usuarios){
        super(context, R.layout.elemento_usuario, usuarios);
        mContext = context;
        mUsuarios = usuarios;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.elemento_usuario, null);
            holder = new ViewHolder();
            holder.usuarioImageView = (ImageView) convertView.findViewById(R.id.gravatarUsuario);
            holder.etiquetaEmisorTextView = (TextView) convertView.findViewById(R.id.etiquetaNombre);
            holder.ImageViewCheckmark = (ImageView) convertView.findViewById(R.id.checkmarkUsuarioImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser usuario = mUsuarios.get(position);
        String email = usuario.getEmail().toLowerCase();

        if(email.equals("")){
            holder.usuarioImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/"+hash+"?s=204&d=404";
            Log.d("TEST", gravatarUrl);
            Picasso.with(mContext).
                    load(gravatarUrl).
                    placeholder(R.drawable.avatar_empty).//metodo para proporcionar una imagen por defecto si load() no retorna alguna imagen
                    into(holder.usuarioImageView);
        }
        holder.etiquetaEmisorTextView.setText(usuario.getUsername());

        GridView gridView = (GridView) parent;
        if(gridView.isItemChecked(position)){
            holder.ImageViewCheckmark.setVisibility(View.VISIBLE);
        } else {
            holder.ImageViewCheckmark.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static  class ViewHolder {
        ImageView usuarioImageView;
        ImageView ImageViewCheckmark;
        TextView etiquetaEmisorTextView;


    }

    public void refill(List<ParseUser> usuarios){
        // 1 limpiamos la data acual
        mUsuarios.clear();
        // Agregamos los nuevos mensajes
        mUsuarios.addAll(usuarios);
        notifyDataSetChanged();
    }
}
