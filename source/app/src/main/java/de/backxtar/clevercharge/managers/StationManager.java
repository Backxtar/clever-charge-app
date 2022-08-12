package de.backxtar.clevercharge.managers;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.data.ModuleType;

/**
 * Wrapper for data.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class StationManager {

    /* Global variables */

    /**
     * Static comparator for sorting recyclerView.
     */
    private static final Comparator<ChargingStation> sortByDistance = Comparator.comparingDouble(ChargingStation::getDistance);

    /**
     * Global station storage.
     */
    private static ArrayList<ChargingStation> station_storage;

    /**
     * Static search radius.
     */
    private static int range;
    //===========================================================================================================================

    /**
     * Get comparator for sorting stations by distance.
     * @return Comparator<ChargingStation>
     */
    public static Comparator<ChargingStation> getSortByDistance() {
        return sortByDistance;
    }

    /**
     * Setter for container.
     * @param station_list of station objects
     */
    public static void setStation_list(ArrayList<ChargingStation> station_list) {
        station_storage = station_list;
    }

    /**
     * Getter for station container.
     * @return arrayList with station objects
     */
    public static ArrayList<ChargingStation> getStation_list() {
        return station_storage;
    }

    /**
     * Getter for view range.
     * @return range as int
     */
    public static int getRange() {
        return range;
    }

    /**
     * Setter for view range.
     * @param range as int
     */
    public static void setRange(int range) {
        StationManager.range = range;
    }

    /**
     * Get amount of stations.
     * @return amount of stations as int
     */
    public static int getStationAmount() {
        return station_storage.size();
    }

    /**
     * Sort by ModuleType
     * @param module_type as enum
     * @return new sorted ArrayList<ChargingStations>
     */
    public static ArrayList<ChargingStation> sort(final ModuleType module_type) {
        return station_storage.stream()
                .filter(station -> station.getModule_type() == module_type)
                .collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Sort station by radius.
     * @param lat of centered point
     * @param lon of centered point
     * @return new sorted ArrayList<ChargingStation>
     */
    public static ArrayList<ChargingStation> sort(final double lat, final double lon) {
        return station_storage.stream()
                .filter(station -> station.getDistance(lat, lon) <= (double) range)
                .collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Only local favorites.
     * @return new sorted ArrayList<ChargingStation>
     */
    public static ArrayList<ChargingStation> sortByFavAndLocal() {
        return sort(UserManager.getMyPosition().latitude, UserManager.getMyPosition().longitude).stream()
                .filter(station -> UserManager.getApi_data().getFavorites().contains(station.getId()))
                .collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Returns a list with favorites.
     * @return a sorted list only with favorites
     */
    public static ArrayList<ChargingStation> getFavorites() {
        return station_storage.stream()
                .filter(station -> UserManager.getApi_data().getFavorites().contains(station.getId()))
                .collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Returns a list with defect stations.
     * @return a sorted list only with defect stations
     */
    public static ArrayList<ChargingStation> getDefects() {
        return station_storage.stream()
                .filter(station -> UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId()))
                .collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Returns a list with local defect stations.
     * @param lat as double
     * @param lon as double
     * @return a sorted list only with local defect stations
     */
    public static ArrayList<ChargingStation> getLocalDefects(final double lat, final double lon) {
        return getDefects().stream()
                .filter(station -> station.getDistance(lat, lon) <= range)
                .collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Convert enum into string
     * @param module_type as enum
     * @return enum as string
     */
    public static String convertEnum(Context context, ModuleType module_type) {
        if (module_type == ModuleType.FAST_CHARGING) return context.getResources().getString(R.string.fast_charging);
        else return context.getResources().getString(R.string.normal_charging);
    }
}
