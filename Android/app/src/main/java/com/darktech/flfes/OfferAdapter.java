package com.darktech.flfes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder> {

    private final LinkedList<String[]> offers;
    private LayoutInflater inflater;

    public OfferAdapter(Context context, LinkedList<String[]> inOffers ){
        inflater = LayoutInflater.from(context);
        this.offers = inOffers;
    }

    @NonNull
    @Override
    public OfferAdapter.OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.offers_item, parent, false);
        return new OfferHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.OfferHolder holder, int position) {
        String[] mCurrent = offers.get(position);
        holder.title.setText(mCurrent[0]);
        holder.mat.setText(mCurrent[1]);
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OfferHolder extends RecyclerView.ViewHolder{
        public final MaliFontTextView title;
        public final MaliFontTextView mat;
        final OfferAdapter oAdapter;

        public OfferHolder(@NonNull View itemView, OfferAdapter Adapter) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            mat = itemView.findViewById(R.id.textViewMateria);
            this.oAdapter = Adapter;
        }
    }
}
