package de.backxtar.clevercharge.services;

/**
 * SaveStateService container.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class SaveStateService {

    /* Global variables */

    /**
     * Local is checked.
     */
    private boolean local;

    /**
     * Favorites is checked.
     */
    private boolean fav;

    /**
     * Local for devs is checked.
     */
    private boolean localDef;

    /**
     * Current String to be searched.
     */
    private String search;
    //=========================

    /**
     * Constructor of saveState
     */
    public SaveStateService() {
        this.local = false;
        this.fav = false;
        this.localDef = false;
    }

    /**
     * Getter for is local.
     * @return boolean
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Getter for is fav.
     * @return true or false as boolean
     */
    public boolean isFav() {
        return fav;
    }

    /**
     * Getter for is local for devs.
     * @return true or false as boolean
     */
    public boolean isLocalDef() {
        return localDef;
    }

    /**
     * Setter for is local.
     * @param local as boolean
     */
    public void setLocal(boolean local) {
        this.local = local;
    }

    /**
     * Setter for is fav.
     * @param fav as boolean
     */
    public void setFav(boolean fav) {
        this.fav = fav;
    }

    /**
     * Setter for is local dev.
     * @param localDef as boolean
     */
    public void setLocalDef(boolean localDef) {
        this.localDef = localDef;
    }

    /**
     * Getter for search text.
     * @return searched text as String
     */
    public String getSearch() {
        return search;
    }

    /**
     * Setter for search text.
     * @param search as String
     */
    public void setSearch(String search) {
        this.search = search;
    }
}
