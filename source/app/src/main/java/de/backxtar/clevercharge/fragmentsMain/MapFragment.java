package de.backxtar.clevercharge.fragmentsMain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.MessageService;
import de.backxtar.clevercharge.services.PopupService;

/**
 * Map fragment.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class MapFragment extends Fragment {

    /* Global variables */

    /**
     * Map fragment.
     */
    private SupportMapFragment mapFragment;

    /**
     * GoogleMap object.
     */
    private GoogleMap googleMap;

    /**
     * Button for show stations.
     */
    private Button showStations;

    /**
     * State for button is clicked.
     */
    private boolean isClicked;
    //======================================

    /**
     * Map constructor.
     */
    public MapFragment() {
        // Constructor
    }

    /**
     * On create fragment.
     * @param savedInstanceState as object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On create fragment view.
     * @param inflater as object
     * @param container as object
     * @param savedInstanceState as object
     * @return new view of fragment
     */
    @SuppressLint({"DefaultLocale", "MissingPermission", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        showStations = view.findViewById(R.id.showStations);

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        checkButtonState();
        initMap();
        initButton();

        return view;
    }

    /**
     * Init mapFragment & googleMaps
     */
    @SuppressLint("MissingPermission")
    private void initMap() {
        mapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setTrafficEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.setMyLocationEnabled(true);
            initMapFunctions();
        });
    }

    /**
     * Init map functions.
     */
    private void initMapFunctions() {
        googleMap.setOnMapLongClickListener(latLng -> {
            isClicked = true;
            showStations.setText(getResources().getString(R.string.remove_marked_stations));

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(getResources().getString(R.string.central_point));
            googleMap.clear();
            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(latLng, 12), 2000, null);
            setPoints(latLng.latitude, latLng.longitude, true);
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            if (marker.getTag() == null) return;
            ChargingStation station = (ChargingStation) marker.getTag();
            PopupService popupService = new PopupService(getActivity(), station, marker);
            popupService.showPopup();
        });
    }

    /**
     * Init button
     */
    private void initButton() {
        showStations.setOnClickListener(v -> {
            if (!isClicked) {
                if (UserManager.getMyPosition() != null) {
                    if (setPoints(UserManager.getMyPosition().latitude, UserManager.getMyPosition().longitude, false)) {
                        showStations.setText(getResources().getString(R.string.remove_marked_stations));
                        isClicked = true;
                    }
                } else {
                    MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.location_cant_be_tracked), Gravity.CENTER, true);
                    msgService.sendToast();
                }
            } else {
                googleMap.clear();
                showStations.setText(getResources().getString(R.string.get_nearby_stations));
                isClicked = false;
            }
        });
    }

    /**
     * Check button text
     */
    private void checkButtonState() {
        if (isClicked) showStations.setText(getResources().getString(R.string.remove_marked_stations));
        else showStations.setText(getResources().getString(R.string.get_nearby_stations));
    }

    /**
     * Set markers on map.
     * @param lat as double
     * @param lon as double
     * @return boolean if set
     */
    private boolean setPoints(final double lat, final double lon, boolean onMapClick) {
        ArrayList<ChargingStation> stations_near = StationManager.sort(lat, lon);
        BitmapDescriptor bitMap = bitMapFromPng(getActivity(), R.drawable.ic_marker);
        BitmapDescriptor bitMapFav = bitMapFromPng(getActivity(), R.drawable.ic_marker_fav);
        
        if (stations_near.isEmpty()) {
            MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.no_stations_available), Gravity.CENTER, true);
            msgService.sendToast();
            return false;
        }
        stations_near.forEach(station -> {
            final double distance;
            if (!onMapClick) distance = station.getDistance();
            else distance = station.getDistance(lat, lon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(station.getLat(), station.getLon()));

            if (UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId()))
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            else if (UserManager.getApi_data().getFavorites().contains(station.getId()) &&
                    !UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId()))
                markerOptions.icon(bitMapFav);
            else markerOptions.icon(bitMap);

            markerOptions.title(station.getOperator() + " | "
                    + String.format(Locale.getDefault(),"%.2f", distance)
                    + "km | " + getResources().getString(R.string.more) + "...");
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(station);
        });
        return true;
    }

    /**
     * Get BitmapDescriptor for GoogleMaps
     * @param context from activity
     * @param drawableID from png image
     * @return icon as BitmapDescriptor
     */
    public static BitmapDescriptor bitMapFromPng(Context context, int drawableID) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableID);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}