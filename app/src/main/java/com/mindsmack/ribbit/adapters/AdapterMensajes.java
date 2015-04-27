package com.mindsmack.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindsmack.ribbit.utils.ParseConstants;
import com.mindsmack.ribbit.R;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Juan on 06/04/2015.
 */
public class AdapterMensajes extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mMensajes;

    public AdapterMensajes(Context context, List<ParseObject> mensajes){
        super(context, R.layout.item_mensaje, mensajes);
        mContext = context;
        mMensajes = mensajes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mensaje, null);
            holder = new ViewHolder();
            holder.iconoMensajeImageView = (ImageView) convertView.findViewById(R.id.iconoMensaje);
            holder.etiquetaEmisorTextView = (TextView) convertView.findViewById(R.id.etiquetaEmisor);
            holder.etiquetaTiempoTextView = (TextView) convertView.findViewById(R.id.etiquetaTiempo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject mensaje = mMensajes.get(position);
        Date creadoALas = mensaje.getCreatedAt();
        long ahora = new Date().getTime();
        String convertidorFecha = DateUtils.getRelativeTimeSpanString(creadoALas.getTime(), ahora, DateUtils.SECOND_IN_MILLIS).toString();

        holder.etiquetaTiempoTextView.setText(convertidorFecha);


        if(mensaje.getString(ParseConstants.CLAVE_TIPO_ARCHIVO).equals(ParseConstants.TIPO_FOTO)){
            holder.iconoMensajeImageView.setImageResource(R.drawable.ic_picture);
        } else if (mensaje.getString(ParseConstants.CLAVE_TIPO_ARCHIVO).equals(ParseConstants.TIPO_VIDEO)) {
            holder.iconoMensajeImageView.setImageResource(R.drawable.ic_video);
        } else if (mensaje.getString(ParseConstants.CLAVE_TIPO_ARCHIVO).equals(ParseConstants.TIPO_MENSAJE)){
            holder.iconoMensajeImageView.setImageResource(R.drawable.ic_content_email);
        }
        holder.etiquetaEmisorTextView.setText(mensaje.getString(ParseConstants.CLAVE_NOMBRE_EMISOR));

        return convertView;
    }

    private static  class ViewHolder {
        ImageView iconoMensajeImageView;
        TextView etiquetaEmisorTextView;
        TextView etiquetaTiempoTextView;


    }

    public void refill(List<ParseObject> mensajes){
        // 1 limpiamos la data acual
        mMensajes.clear();
        // Agregamos los nuevos mensajes
        mMensajes.addAll(mensajes);
        notifyDataSetChanged();
    }
}
