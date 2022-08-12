package de.backxtar.clevercharge.data;

import com.google.gson.annotations.SerializedName;

/**
 * PlugType enum.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

/**
 * Enum for PlugType
 */
public enum PlugType {

    /**
     * Enum for AC_PLUG_TYPE_2.
     */
    @SerializedName("AC Steckdose Typ 2") AC_PLUG_TYPE_2,

    /**
     * Enum for AC_CLUTCH_TYPE_2.
     */
    @SerializedName("AC Kupplung Typ 2") AC_CLUTCH_TYPE_2,

    /**
     * Enum for AC_SCHUKO.
     */
    @SerializedName("AC Schuko") AC_SCHUKO,

    /**
     * Enum for DC_CLUTCH_COMBO.
     */
    @SerializedName("DC Kupplung Combo") DC_CLUTCH_COMBO,

    /**
     * Enum for DC_CHADEMO.
     */
    @SerializedName("DC CHAdeMO") DC_CHADEMO
}
