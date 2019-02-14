package fr.unicaen.aera128.immobilier.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.unicaen.aera128.immobilier.Models.Propriete;
import fr.unicaen.aera128.immobilier.R;

public class ProprietesAdapter extends RecyclerView.Adapter<ProprietesAdapter.MyViewHolder> {

    private List<Propriete> proprietes;
    private final OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titre, ville, prix;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.imageCell);
            titre = (TextView) view.findViewById(R.id.titreCell);
            ville = (TextView) view.findViewById(R.id.villeCell);
            prix = (TextView) view.findViewById(R.id.prixCell);
        }

        public void bind(final Propriete propriete, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(propriete);
                }
            });
        }
    }

    public ProprietesAdapter(List<Propriete> proprietes, OnItemClickListener listener) {
        this.proprietes = proprietes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_liste_biens, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Propriete propriete = proprietes.get(i);
        holder.bind(proprietes.get(i), listener);
        Picasso.get().load(propriete.getImages()[0]).resize(200, 200).centerCrop().into(holder.image);
        holder.titre.setText(propriete.getTitre());
        holder.ville.setText(propriete.getVille());
        holder.prix.setText(propriete.getPrix() + " €");
    }

    @Override
    public int getItemCount() {
        return proprietes.size();
    }

    public void setProprietes(List<Propriete> proprietes) {
        this.proprietes = proprietes;
    }
}
