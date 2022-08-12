package de.backxtar.clevercharge.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.backxtar.clevercharge.managers.UserManager;

/**
 * ChargingStation container.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class ChargingStation {

    /* General information */

    /**
     * Station ID.
     */
    private int id;

    /**
     * Station operator.
     */
    private String operator;

    /**
     * Station street.
     */
    private String street;

    /**
     * Station number of street.
     */
    private String number;

    /**
     * Station additional.
     */
    private String additional;

    /**
     * Station postal code.
     */
    private int postal_code;

    /**
     * Station location.
     */
    private String location;

    /**
     * Station state.
     */
    private String state;

    /**
     * Station area.
     */
    private String area;

    /**
     * Station lat.
     */
    private double lat;

    /**
     * Station lon.
     */
    private double lon;

    /**
     * Station installation date.
     */
    private String installation_date;

    /**
     * Station connection power.
     */
    private double conn_power;

    /**
     * Station module type.
     */
    private ModuleType module_type;

    /**
     * Number of connections.
     */
    private int number_of_connections;

    /* Charging Point 1 */

    /**
     * Station plug types 1.
     */
    private ArrayList<PlugType> plug_types_1;

    /**
     * Station power of plug 1.
     */
    private double power_1;

    /**
     * Plug 1 public key.
     */
    private String public_key_1;

    /* Charging Point 2 */

    /**
     * Station plug types 2.
     */
    private ArrayList<PlugType> plug_types_2;

    /**
     * Station power of plug 2.
     */
    private double power_2;

    /**
     * Plug 2 public key.
     */
    private String public_key_2;

    /* Charging Point 3 */

    /**
     * Station plug types 3.
     */
    private ArrayList<PlugType> plug_types_3;

    /**
     * Station power of plug 3.
     */
    private double power_3;

    /**
     * Plug 3 public key.
     */
    private String public_key_3;

    /* Charging Point 4 */

    /**
     * Station plug types 4.
     */
    private ArrayList<PlugType> plug_types_4;

    /**
     * Station power of plug 4.
     */
    private double power_4;

    /**
     * Plug 4 public key.
     */
    private String public_key_4;
    //=============================================

    /**
     * Get distance between your position and the stations position.
     * @return distance in as double
     */
    public double getDistance() {
        LatLng myPosition = UserManager.getMyPosition();
        final double earth_radius = 6371000.785;

        final double myLat = Math.toRadians(myPosition.latitude);
        final double myLon = Math.toRadians(myPosition.longitude);
        final double latStation = Math.toRadians(lat);
        final double lonStation = Math.toRadians(lon);

        final double zeta = Math.acos(
                Math.sin(myLat) * Math.sin(latStation)
                        + Math.cos(myLat) * Math.cos(latStation)
                        * Math.cos(lonStation - myLon));
        return (zeta * earth_radius) / 1000;
    }

    /**
     * Get distance between your marker and the stations position.
     * @param mLat of marker
     * @param mLon of marker
     * @return distance as double
     */
    public double getDistance(final double mLat, final double mLon) {
        final double earth_radius = 6371000.785;

        final double myLat = Math.toRadians(mLat);
        final double myLon = Math.toRadians(mLon);
        final double latStation = Math.toRadians(lat);
        final double lonStation = Math.toRadians(lon);

        final double zeta = Math.acos(
                Math.sin(myLat) * Math.sin(latStation)
                        + Math.cos(myLat) * Math.cos(latStation)
                        * Math.cos(lonStation - myLon));
        return (zeta * earth_radius) / 1000;
    }

    /**
     * Get the stations id.
     * @return id as int
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the stations operator.
     * @return operator as string
     */
    public final String getOperator() {
        return operator;
    }

    /**
     * Get the stations street.
     * @return street as string
     */
    public final String getStreet() {
        return street;
    }

    /**
     * Get the stations number of street.
     * @return number as string
     */
    public final String getNumber() {
        return number;
    }

    /**
     * Get the stations additional.
     * @return additional as string
     */
    public final String getAdditional() {
        return additional;
    }

    /**
     * Get the stations postal code.
     * @return postal code as int
     */
    public final int getPostal_code() {
        return postal_code;
    }

    /**
     * Get the stations location.
     * @return location as string
     */
    public final String getLocation() {
        return location;
    }

    /**
     * Get the stations state.
     * @return state as string
     */
    public final String getState() {
        return state;
    }

    /**
     * Get the stations area.
     * @return area as string
     */
    public final String getArea() {
        return area;
    }

    /**
     * Get the stations lat position.
     * @return lat as double
     */
    public final double getLat() {
        return lat;
    }

    /**
     * Get the stations lon position.
     * @return lon as double
     */
    public final double getLon() {
        return lon;
    }

    /**
     * Get the stations installation date.
     * @return installation date as date format
     */
    public final String getInstallation_date() {
        return installation_date;
    }

    /**
     * Get the stations connection power.
     * @return connection power as double
     */
    public final double getConn_power() {
        return conn_power;
    }

    /**
     * Get the stations module type.
     * @return module type as enum
     */
    public final ModuleType getModule_type() {
        return module_type;
    }

    /**
     * Get the stations amount of connections.
     * @return amount of connection as int
     */
    public final int getNumber_of_connections() {
        return number_of_connections;
    }

    /**
     * Get the stations plug types.
     * @return ArrayList of enums
     */
    public final ArrayList<PlugType> getPlug_types_1() {
        return plug_types_1;
    }

    /**
     * Get the stations first plug power.
     * @return power as double
     */
    public final double getPower_1() {
        return power_1;
    }

    /**
     * Get the stations public key for first connection.
     * @return key as string
     */
    public final String getPublic_key_1() {
        return public_key_1;
    }

    /**
     * Get the stations plug types.
     * @return ArrayList of enums
     */
    public final ArrayList<PlugType> getPlug_types_2() {
        return plug_types_2;
    }

    /**
     * Get the stations second plug power.
     * @return power as double
     */
    public final double getPower_2() {
        return power_2;
    }

    /**
     * Get the stations public key for second connection.
     * @return key as string
     */
    public final String getPublic_key_2() {
        return public_key_2;
    }

    /**
     * Get the stations plug types.
     * @return ArrayList of enums
     */
    public final ArrayList<PlugType> getPlug_types_3() {
        return plug_types_3;
    }

    /**
     * Get the stations third plug power.
     * @return power as double
     */
    public final double getPower_3() {
        return power_3;
    }

    /**
     * Get the stations public key for third connection.
     * @return key as string
     */
    public final String getPublic_key_3() {
        return public_key_3;
    }

    /**
     * Get the stations plug types.
     * @return ArrayList of enums
     */
    public final ArrayList<PlugType> getPlug_types_4() {
        return plug_types_4;
    }

    /**
     * Get the stations fourth plug power.
     * @return power as double
     */
    public final double getPower_4() {
        return power_4;
    }

    /**
     * Get the stations public key for fourth connection.
     * @return key as string
     */
    public final String getPublic_key_4() {
        return public_key_4;
    }
}
