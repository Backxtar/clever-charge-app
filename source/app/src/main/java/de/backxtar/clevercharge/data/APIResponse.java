package de.backxtar.clevercharge.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * APIResponse container.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class APIResponse {

    /* Response values */

    /**
     * Response code.
     */
    private int response_code;

    /**
     * Response message.
     */
    private String response_msg;

    /**
     * User ID.
     */
    private int user_id;

    /**
     * Username.
     */
    private String user_name;

    /**
     * User mail.
     */
    private String user_email;

    /**
     * Is dev or normal user.
     */
    private boolean is_dev;

    /**
     * Favorite stations.
     */
    private ArrayList<Integer> fav_station_ids;

    /**
     * Defect stations with report msg.
     */
    private HashMap<Integer, String> defect_stations_map;

    /**
     * News marked as read.
     */
    private ArrayList<Integer> news_read_ids;
    //=====================================================

    /**
     * Get response code send by api.
     * @return code as int
     */
    public int getResponseCode() {
        return response_code;
    }

    /**
     * Get users id.
     * @return userId as int
     */
    public int getUserID() {
        return user_id;
    }

    /**
     * Get users username.
     * @return userName as String
     */
    public String getUserNAME() {
        return user_name;
    }

    /**
     * Get users email.
     * @return userMail as String
     */
    public String getUserEMAIL() {
        return user_email;
    }

    /**
     * Get users permissions.
     * @return as bool
     */
    public boolean isDEV() {
        return is_dev;
    }

    /**
     * Get users favorites
     * @return favorites as ArrayList<Integer>
     */
    public ArrayList<Integer> getFavorites() {
        return fav_station_ids;
    }

    /**
     * Get global defect stations.
     * @return defect stations as HashMap<Integer, String>
     */
    public HashMap<Integer, String> getDefect_stations_map() {
        return defect_stations_map;
    }

    /**
     * Set global defect stations.
     * @param defect_stations_map as object
     */
    public void setDefect_stations_map(HashMap<Integer, String> defect_stations_map) {
        this.defect_stations_map = defect_stations_map;
    }

    /**
     * Get users news.
     * @return read news as ArrayList<Integer>
     */
    public ArrayList<Integer> getNews_read_ids() { return news_read_ids; }
}
