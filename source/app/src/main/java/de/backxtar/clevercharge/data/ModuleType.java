package de.backxtar.clevercharge.data;

import com.google.gson.annotations.SerializedName;

/**
 * ModuleType enum.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

/**
 * Enum for ModuleType
 */
public enum ModuleType {

    /**
     * Enum for Standard.
     */
    @SerializedName("Normalladeeinrichtung") STANDARD,

    /**
     * Enum for FAST_CHARGING.
     */
    @SerializedName("Schnellladeeinrichtung") FAST_CHARGING
}
