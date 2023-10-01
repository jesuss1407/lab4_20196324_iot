package com.example.lab4_iot_20196324;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoDtoViewHolder> {
    private List<ContactoDto> listaContactos;
    private Context context;


    public ContactoAdapter(Context context, List<ContactoDto> listaContactos) {
        this.context = context;
        this.listaContactos = listaContactos;
    }
    @NonNull
    @Override
    public ContactoDtoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
        return new ContactoDtoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoDtoViewHolder holder, int position) {
        ContactoDto contact = listaContactos.get(position);

        Glide.with(context)
                .load(contact.getImage_link())
                .into(holder.ivfoto);

        holder.tvnombre.setText(contact.getNombre());
        holder.tvgenero.setText("Genero: " + contact.getGenero());
        holder.tvciudad.setText("Ciudad: " + contact.getCiudad());
        holder.tvpais.setText("Pais: " + contact.getPais());
        holder.tvemail.setText("Email: " + contact.getEmail());
        holder.tvtelefono.setText("Telefono: " + contact.getTelefono());

        holder.eliminarContacto.setOnClickListener(v -> {
            int myPosition = holder.getAdapterPosition();
            deleteContact(myPosition);
            if (context instanceof AppActivity) {
                ((AppActivity) context).deleteContact(myPosition);
            }
        });

    }
    public void deleteContact(int position) {
        listaContactos.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public class ContactoDtoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivfoto;
        TextView tvnombre,tvgenero,tvciudad,tvpais,tvemail,tvtelefono;
        ImageButton eliminarContacto;

        public ContactoDtoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivfoto = itemView.findViewById(R.id.imagen);
            tvnombre = itemView.findViewById(R.id.nombre);
            tvgenero = itemView.findViewById(R.id.genero);
            tvciudad = itemView.findViewById(R.id.ciudad);
            tvpais = itemView.findViewById(R.id.pais);
            tvemail = itemView.findViewById(R.id.email);
            tvtelefono = itemView.findViewById(R.id.telefono);
            eliminarContacto = itemView.findViewById(R.id.eliminar);


        }
    }
}
