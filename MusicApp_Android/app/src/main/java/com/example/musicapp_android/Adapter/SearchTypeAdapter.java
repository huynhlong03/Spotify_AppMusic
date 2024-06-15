package com.example.musicapp_android.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp_android.Data.HomeType;
import com.example.musicapp_android.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Random;

public class SearchTypeAdapter extends RecyclerView.Adapter<SearchTypeAdapter.TypesViewHolder>{
    private final List<HomeType> typeList;

    private Context context;
    private SearchTypeAdapter.OnItemClickListener listener;

    public SearchTypeAdapter(List<HomeType> typeList) {
        this.typeList = typeList;
    }

    @NonNull
    @Override
    public SearchTypeAdapter.TypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_type_item, parent, false);

        context = parent.getContext();

        return new SearchTypeAdapter.TypesViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTypeAdapter.TypesViewHolder holder, int position) {
        HomeType type = typeList.get(position);
        holder.name.setText(typeList.get(position).getTypeName());
        holder.name.setBackgroundColor(getRandomColor());
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(SearchTypeAdapter.OnItemClickListener listener) {
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

        public TypesViewHolder(@NonNull View itemView, final SearchTypeAdapter.OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.btnType_Search);

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

    public int getRandomColor() {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return color;
    }
}
