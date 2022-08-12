package de.backxtar.clevercharge.managers;

import androidx.fragment.app.Fragment;

import de.backxtar.clevercharge.fragmentsMain.DevFragment;
import de.backxtar.clevercharge.fragmentsMain.HomeFragment;
import de.backxtar.clevercharge.fragmentsMain.MapFragment;
import de.backxtar.clevercharge.fragmentsMain.SearchFragment;
import de.backxtar.clevercharge.fragmentsMain.SettingsFragment;

/**
 * FragmentManager of the app.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class NavigationManager {

    /* Global variables */

    /**
     * Map fragment.
     */
    private final Fragment map;

    /**
     * Settings fragment.
     */
    private final Fragment settings;
    //================================

    /**
     * Constructor of NavigationManager
     */
    public NavigationManager() {
        this.map = new MapFragment();
        this.settings = new SettingsFragment();
    }

    /**
     * Get always new home fragment to update RecyclerView
     * @return fragment as reference
     */
    public Fragment getHome() {
        return new HomeFragment();
    }

    /**
     * Get map fragment.
     * @return fragment as reference
     */
    public Fragment getMap() {
        return map;
    }

    /**
     * Get search fragment.
     * @return fragment as reference
     */
    public Fragment getSearch() {
        return new SearchFragment();
    }

    /**
     * Get settings fragment.
     * @return fragment as reference
     */
    public Fragment getSettings() {
        return settings;
    }

    /**
     * Get about fragment.
     * @return fragment as reference
     */
    public Fragment getDev() {
        return new DevFragment();
    }
}
