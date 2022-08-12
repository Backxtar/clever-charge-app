package de.backxtar.clevercharge.managers;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.backxtar.clevercharge.data.APIResponse;
import de.backxtar.clevercharge.data.Article;
import de.backxtar.clevercharge.services.SaveStateService;

/**
 * UserManager.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class UserManager {

    /* Global variables */

    /**
     * Static APIResponse class.
     */
    private static APIResponse api_data;

    /**
     * Static article ArrayList.
     */
    private static ArrayList<Article> articles;

    /**
     * Static LatLng for user's location.
     */
    private static LatLng myPosition;

    /**
     * Static container for saveStates.
     */
    private static SaveStateService saveStateService;
    //================================================

    /**
     * Get current saved api data.
     * @return ApiResponse object
     */
    public static APIResponse getApi_data() {
        return api_data;
    }

    /**
     * Set ApiResponse data to current data.
     * @param api_data as object
     */
    public static void setApi_data(APIResponse api_data) {
        UserManager.api_data = api_data;
    }

    /**
     * Get current articles for landing page.
     * @return articles as ArrayList<Article>
     */
    public static ArrayList<Article> getArticles() {
        return articles;
    }

    /**
     * Set Articles to ArrayList<Article>.
     * @param articles as ArrayList<Article> object
     */
    public static void setArticles(ArrayList<Article> articles) {
        UserManager.articles = articles;
    }

    /**
     * Return the users current position.
     * @return position as LatLng object
     */
    public static LatLng getMyPosition() {
        return myPosition;
    }

    /**
     * Set users current position.
     * @param myPosition as LatLng object
     */
    public static void setMyPosition(LatLng myPosition) {
        UserManager.myPosition = myPosition;
    }

    /**
     * Return SaveState.
     * @return SaveStateService object
     */
    public static SaveStateService getSaveStateService() {
        return saveStateService;
    }

    /**
     * Set SaveState
     * @param saveStateService as object
     */
    public static void setSaveStateService(SaveStateService saveStateService) {
        UserManager.saveStateService = saveStateService;
    }
}
