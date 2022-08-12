package de.backxtar.clevercharge.interfaces;
import de.backxtar.clevercharge.data.ChargingStation;

/**
 * Custom recyclerView interface.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public interface RecyclerViewInterface {

    /**
     * On item click interface for recyclerview.
     * @param position as int
     * @param station as object
     */
    void onItemClick(int position, ChargingStation station);
}
