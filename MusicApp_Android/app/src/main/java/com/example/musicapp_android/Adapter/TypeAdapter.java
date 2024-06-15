package com.example.musicapp_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.musicapp_android.Data.HomeArtists;
import com.example.musicapp_android.Data.HomeSongs;
import com.example.musicapp_android.Data.HomeType;
import com.example.musicapp_android.R;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypesViewHolder> {

    private final List<HomeType> typeList;

    private Context context;
    private OnItemClickListener listener;

    public TypeAdapter(List<HomeType> typeList) {
        this.typeList = typeList;
    }

    @NonNull
    @Override
    public TypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_type_item, parent, false);

        context = parent.getContext();

        return new TypesViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TypesViewHolder holder, int position) {
        HomeType type = typeList.get(position);
        holder.name.setText(typeList.get(position).getTypeName());
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<HomeType> newTypeList) {
        this.typeList.clear();
        for(int i = 0; i < 6; i++) {
            this.typeList.add(newTypeList.get(i));
        }
        //this.typeList.addAll(newTypeList);
        notifyDataSetChanged();
    }


    static class TypesViewHolder extends RecyclerView.ViewHolder {

        public MaterialButton name;

        public TypesViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.btnType);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
