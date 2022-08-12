package de.backxtar.clevercharge.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.interfaces.RecyclerViewInterface;
import de.backxtar.clevercharge.managers.UserManager;

/**
 * ChargingStation Adapter.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class ChargingStationAdapter extends RecyclerView.Adapter<ChargingStationAdapter.ViewHolder> {

    /* Global variables */

    /**
     * Custom recyclerView interface.
     */
    private final RecyclerViewInterface recyclerViewInterface;

    /**
     * Station ArrayList.
     */
    private ArrayList<ChargingStation> station_list;

    /**
     * Current context.
     */
    private final Context context;
    //==========================================================

    /**
     * Constructor.
     * @param station_list for the listView
     * @param context of the activity
     */
    public ChargingStationAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<ChargingStation> station_list, Context context) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.station_list = station_list;
        this.context = context;
    }

    /* ViewHolder class */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView operator, location, conn;

        /**
         * Constructor.
         * @param itemView as view
         */
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            operator = itemView.findViewById(R.id.txtTitle);
            location = itemView.findViewById(R.id.txtLocation);
            conn = itemView.findViewById(R.id.txtConnection);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();

                if (recyclerViewInterface == null || pos == RecyclerView.NO_POSITION) return;
                recyclerViewInterface.onItemClick(pos, station_list.get(pos));
            });
        }
    }

    /**
     * Check station params for layout design.
     * @param position of the station in the list
     * @return type as int
     */
    @Override
    public int getItemViewType(int position) {
        HashMap<Integer, String> defect_msg = UserManager.getApi_data().getDefect_stations_map();
        ArrayList<Integer> fav = UserManager.getApi_data().getFavorites();
        int type = 0;

        if (fav.isEmpty() && defect_msg.isEmpty()) {
            if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = 3;
            else type = 4;
        }

        else if (fav.isEmpty() && !defect_msg.isEmpty()) {
            if (defect_msg.containsKey(station_list.get(position).getId())) {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = -1;
                else type = -2;
            } else {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = 3;
                else type = 4;
            }
        }

        else if (defect_msg.isEmpty() && !fav.isEmpty()) {
            if (fav.contains(station_list.get(position).getId())) {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = 1;
                else type = 2;
            } else {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = 3;
                else type = 4;
            }
        }

        else if (!fav.isEmpty() && !defect_msg.isEmpty()) {
            if (defect_msg.containsKey(station_list.get(position).getId())) {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = -1;
                else type = -2;
            } else if (fav.contains(station_list.get(position).getId())) {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = 1;
                else type = 2;
            } else {
                if (station_list.get(position).getModule_type() == ModuleType.FAST_CHARGING) type = 3;
                else type = 4;
            }
        }

        return type;
    }

    /**
     * Inflate the design for one item.
     * @param parent ViewGroup
     * @param viewType for design switch case
     * @return new ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        switch (viewType) {
            case -1 : view = inflater.inflate(R.layout.station_item_2_block, parent, false);
                break;
            case -2 : view = inflater.inflate(R.layout.station_item_1_block, parent, false);
                break;
            case 1 : view = inflater.inflate(R.layout.station_item_2_fav, parent, false);
                break;
            case 2 : view = inflater.inflate(R.layout.station_item_1_fav, parent, false);
                break;
            case 3 : view = inflater.inflate(R.layout.station_item_2, parent, false);
                break;
            case 4 : view = inflater.inflate(R.layout.station_item_1, parent, false);
                break;
            default: view = inflater.inflate(R.layout.station_item_not_avail, parent, false);
        }
        return new ViewHolder(view, recyclerViewInterface);
    }

    /**
     * Set values to listItem.
     * @param holder that was created.
     * @param position int the list.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder.getItemViewType() != 0) {
            ChargingStation station = station_list.get(position);
            final double distance = station.getDistance();
            String dist = "(" + String.format(Locale.getDefault(),"%.2f", distance) + "km)";
            holder.operator.setText(station.getOperator() + " " + dist);
            holder.location.setText(station.getStreet() + " " + station.getNumber());
            holder.conn.setText(station.getPostal_code() + " " + station.getLocation());
        } else {
            holder.operator.setText(context.getResources().getString(R.string.not_available));
            holder.location.setText(context.getResources().getString(R.string.not_available));
            holder.conn.setText(context.getResources().getString(R.string.not_available));
            holder.conn.setTextColor(ContextCompat.getColor(context, R.color.notAvailable));
        }
    }

    /**
     * Get listSize.
     * @return the listSize as int
     */
    @Override
    public int getItemCount() {
        return station_list.size();
    }

    /**
     * Set new ArrayList and notify RecyclerView to update the data.
     * @param filteredList to set
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(ArrayList<ChargingStation> filteredList) {
        station_list = filteredList;
        notifyDataSetChanged();
    }
}
