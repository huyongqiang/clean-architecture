package com.tylersuehr.cleanarchitecture.ui.adapters;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tylersuehr.cleanarchitecture.R;
import com.tylersuehr.cleanarchitecture.data.models.Entity;
import com.tylersuehr.cleanarchitecture.data.models.Phone;
import com.tylersuehr.cleanarchitecture.data.models.Tablet;
import com.tylersuehr.cleanarchitecture.data.models.Technology;
import com.tylersuehr.cleanarchitecture.data.models.User;
import com.tylersuehr.cleanarchitecture.data.models.Watch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Copyright 2016 Tyler Suehr
 * Created by tyler on 8/28/2016.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Holder> {
    private static final int USER = 0;
    private static final int PHONE = 1;
    private static final int TABLET = 2;
    private static final int WATCH = 3;
    private List<Entity> items;


    public ItemAdapter() {
        this.items = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof User)
            return USER;
        else if (items.get(position) instanceof Phone)
            return PHONE;
        else if (items.get(position) instanceof Tablet)
            return TABLET;
        else if (items.get(position) instanceof Watch)
            return WATCH;
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        Holder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case USER:
                v = inflater.inflate(R.layout.adapter_chip, parent, false);
                holder = new Holder(v);
                holder.text1 = (TextView)v.findViewById(R.id.title);
                holder.image = (ImageView)v.findViewById(R.id.image);
                return holder;
            case PHONE:
            case TABLET:
            case WATCH:
                v = inflater.inflate(R.layout.adapter_card, parent, false);
                holder = new Holder(v);
                holder.text1 = (TextView)v.findViewById(R.id.title);
                holder.text2 = (TextView)v.findViewById(R.id.description);
                holder.image = (ImageView)v.findViewById(R.id.image);
                return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == USER) {
            User user = (User)items.get(position);
            String name = user.getFirstName();
            name += " " + user.getLastName();
            holder.text1.setText(name);
            holder.image.setImageResource(R.drawable.ic_person_24dp);
        } else {
            Technology tech = (Technology)items.get(position);
            holder.image.setImageResource(tech.getIcon(holder.image.getResources()));
            holder.text1.setText(tech.getClass().getSimpleName());
            holder.text2.setText(tech.getBrand() + " " + tech.getModel() + " $" + tech.getPrice());
        }
    }

    public void add(Entity e) {
        this.items.add(e);
        this.notifyItemInserted(getActualCount());
    }

    public void addAll(Collection<? extends Entity> entities) {
        int count = getActualCount();
        for (Entity e : entities) {
            this.items.add(e);
        }
        this.notifyItemRangeInserted(count, getActualCount());
    }

    public void replaceAll(List<Entity> entities) {
        this.items = entities;
        this.notifyItemRangeInserted(0, getActualCount());
    }

    public void clear() {
        int count = getActualCount();
        this.items.clear();
        this.notifyItemRangeRemoved(0, count);
    }

    private int getActualCount() {
        return items.size() - 1;
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        ImageView image;


        public Holder(View v) {
            super(v);
        }
    }
}