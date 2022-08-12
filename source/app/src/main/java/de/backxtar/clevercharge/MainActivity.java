package de.backxtar.clevercharge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Comparator;

import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.managers.NavigationManager;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.MessageService;
import de.backxtar.clevercharge.services.SaveStateService;

/**
 * MainActivity of the app.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity implements LocationListener {

    /* Global variables */

    /**
     * Bottom navigation.
     */
    private static BottomNavigationView navigation;

    /**
     * Fragment manager.
     */
    private FragmentManager fragmentManager;

    /**
     * Navigation manager.
     */
    private NavigationManager navigationManager;

    /**
     * Location manager for user's location.
     */
    private LocationManager locationManager;

    /**
     * Request code for getting permissions.
     */
    private int LOCATION_REQUEST_CODE;
    //==============================================

    /**
     * On create activity.
     * @param savedInstanceState as object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            runMain();
        } else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
               Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    /**
     * Run main method if permissions granted!
     * Permission listener: {@link #onRequestPermissionsResult(int, String[], int[])}
     */
    @SuppressLint("NonConstantResourceId")
    private void runMain() {
        System.out.println(UserManager.getApi_data().getDefect_stations_map());
        /* Init objects */
        initObjects();

        /* Show home fragment */
        fragmentManager.beginTransaction()
                .replace(R.id.body_container, navigationManager.getHome(), navigationManager.getHome().getClass().getName())
                .commit();
        navigation.setSelectedItemId(R.id.nav_home);

        /* Add badges */
        updateBadges(R.id.nav_home, UserManager.getArticles().size() - UserManager.getApi_data().getNews_read_ids().size());
        updateBadges(R.id.nav_dev, UserManager.getApi_data().getDefect_stations_map().size());

        /* Menu listener */
        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == navigation.getSelectedItemId() || !areProvidersEnabled()) return false;
            if (UserManager.getMyPosition() == null) {
                MessageService msg = new MessageService(this, getResources().getString(R.string.wait_for_location), Gravity.TOP, true);
                msg.sendToast();
                return false;
            }

            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    fragment = navigationManager.getHome();
                    updateBadges(R.id.nav_home, UserManager.getArticles().size() - UserManager.getApi_data().getNews_read_ids().size());
                    break;
                case R.id.nav_map:
                    fragment = navigationManager.getMap();
                    break;
                case R.id.nav_search:
                    fragment = navigationManager.getSearch();
                    break;
                case R.id.nav_settings:
                    fragment = navigationManager.getSettings();
                    break;
                case R.id.nav_dev:
                    fragment = navigationManager.getDev();
                    updateBadges(R.id.nav_dev, UserManager.getApi_data().getDefect_stations_map().size());
                    break;
            }
            assert fragment != null;
            final String tag = fragment.getClass().getName();

            if (isFragmentInBackstack(tag))
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                    .replace(R.id.body_container, fragment, tag)
                    .addToBackStack(null)
                    .commit();

            return true;
        });
    }

    /**
     * Permission request listener
     * @param requestCode as int
     * @param permissions as string array
     * @param grantResults as int array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) runMain();
        else {
            MessageService msgService = new MessageService(
                    this, getResources().getString(R.string.need_permissions), Gravity.TOP, true);
            msgService.sendToast();
            new Handler().postDelayed(() -> System.exit(0), 3000);
        }
    }

    /**
     * Initialize Objects
     */
    @SuppressLint("MissingPermission")
    private void initObjects() {
        navigation = findViewById(R.id.bottom_navigation);

        if (UserManager.getApi_data().isDEV())
            navigation.inflateMenu(R.menu.item_menu_dev);
        else navigation.inflateMenu(R.menu.item_menu);

        this.fragmentManager = getSupportFragmentManager();
        this.navigationManager = new NavigationManager();
        UserManager.setSaveStateService(new SaveStateService());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    /**
     * BackStack check.
     * @param tag as string
     * @return yes or no as boolean
     */
    private boolean isFragmentInBackstack(String tag) {
        return fragmentManager.findFragmentByTag(tag) != null;
    }

    /**
     * Check if any provider is enabled.
     * @return boolean if some services are activated
     */
    private boolean areProvidersEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            MessageService msg = new MessageService(this, getResources().getString(R.string.turn_on_provider), Gravity.TOP, true);
            msg.sendToast();
            return false;
        }
        return true;
    }

    /**
     * Listen to location changes. Executed when location has changed.
     * @param location as object
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        UserManager.setMyPosition(new LatLng(
                location.getLatitude(),
                location.getLongitude()));
        //For only one location request, uncomment below! (Battery-Saver)
        //locationManager.removeUpdates(this);
    }

    /**
     * Listen to provider enabled events.
     * @param provider as string
     */
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        //Can send notifications
    }

    /**
     * Listen to provider disable events.
     * @param provider as string.
     */
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //Can send notifications
    }

    /**
     * Disable back button.
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    /**
     * Update menu badges.
     * @param menuItemId as int
     * @param number as int
     */
    public static void updateBadges(int menuItemId, int number) {
        if (number == 0) navigation.removeBadge(menuItemId);
        else navigation.getOrCreateBadge(menuItemId).setNumber(number);
    }
}