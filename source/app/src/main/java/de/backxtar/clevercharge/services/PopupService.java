package de.backxtar.clevercharge.services;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.MainActivity;
import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.data.ChargingStationAdapter;
import de.backxtar.clevercharge.data.ModuleType;
import de.backxtar.clevercharge.fragmentsMain.MapFragment;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;

/**
 * MarkerInfo of the app.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class PopupService {

    /* Global variables */

    /**
     * Current activity.
     */
    private final Activity activity;

    /**
     * Current chargingStation object.
     */
    private final ChargingStation station;

    /**
     * ChargingStation adapter.
     */
    private ChargingStationAdapter adapter;

    /**
     * Switch objects of recyclerView.
     */
    private SwitchCompat nearby, fav;

    /**
     * TextView for defect stations.
     */
    private TextView def;

    /**
     * AlertDialog object of the instance.
     */
    private AlertDialog dialog;

    /**
     * Is favorite station or not.
     */
    private boolean isFav;

    /**
     * Marker object of the station.
     */
    private Marker marker;
    //======================================

    /**
     * Constructor one.
     * @param activity from Activity
     * @param station to show information
     */
    public PopupService(Activity activity, ChargingStation station, Marker marker) {
        this.activity = activity;
        this.station = station;
        this.marker = marker;
    }

    /**
     * Constructor two.
     * @param activity from Activity
     * @param station to show information
     * @param adapter of the users adapter
     * @param nearby button object
     * @param fav button object
     */
    public PopupService(Activity activity, ChargingStation station, ChargingStationAdapter adapter, SwitchCompat nearby, SwitchCompat fav) {
        this.activity = activity;
        this.station = station;
        this.adapter = adapter;
        this.nearby = nearby;
        this.fav = fav;
    }

    /**
     * Constructor three.
     * @param activity from Activity
     * @param station to show information
     * @param adapter of the users adapter
     * @param nearby button object
     */
    public PopupService(Activity activity, ChargingStation station, ChargingStationAdapter adapter, SwitchCompat nearby, TextView def) {
        this.activity = activity;
        this.station = station;
        this.adapter = adapter;
        this.nearby = nearby;
        this.def = def;
    }

    /**
     * Builds and show the popup.
     */
    @SuppressLint("SetTextI18n")
    public void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view;
        if (adapter == null || fav != null && nearby != null)
            view = LayoutInflater.from(activity).inflate(R.layout.popup_window, null);
        else {
            view = LayoutInflater.from(activity).inflate(R.layout.popup_window_dev, null);

            TextView issue = view.findViewById(R.id.info_issue);
            issue.setText(UserManager.getApi_data().getDefect_stations_map().get(station.getId()));

            AppCompatButton fix = view.findViewById(R.id.btn_fix);
            fix.setOnClickListener(v -> {
                devApi();
                UserManager.getApi_data().getDefect_stations_map().remove(station.getId());
                MainActivity.updateBadges(R.id.nav_dev, UserManager.getApi_data().getDefect_stations_map().size());
                updateAdapter();
                def.setText(Integer.toString(UserManager.getApi_data().getDefect_stations_map().size()));
                dismissPopup();
            });
        }

        ImageButton btn_fav = view.findViewById(R.id.btn_favorite);

        if (UserManager.getApi_data().getFavorites().contains(station.getId())) {
            btn_fav.setImageResource(R.drawable.ic_star);
            isFav = true;
        } else {
            btn_fav.setImageResource(R.drawable.ic_star_grey);
            isFav = false;
        }

        btn_fav.setOnClickListener(v -> {
            btn_fav.setClickable(false);
            callApi();

            if (isFav) {
                int index = UserManager.getApi_data().getFavorites().indexOf(station.getId());
                UserManager.getApi_data().getFavorites().remove(index);
                btn_fav.setImageResource(R.drawable.ic_star_grey);
                if (marker != null && !UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId()))
                    marker.setIcon(MapFragment.bitMapFromPng(activity, R.drawable.ic_marker));
            } else {
                UserManager.getApi_data().getFavorites().add(station.getId());
                btn_fav.setImageResource(R.drawable.ic_star);
                if (marker != null && !UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId()))
                    marker.setIcon(MapFragment.bitMapFromPng(activity, R.drawable.ic_marker_fav));
            }
            isFav = !isFav;
            updateAdapter();
            btn_fav.setClickable(true);
        });

        ImageButton btn_close = view.findViewById(R.id.btn_info_close);
        btn_close.setOnClickListener(v -> dismissPopup());

        TextView op_range = view.findViewById(R.id.info_op_range);
        final double distance = station.getDistance();
        op_range.setText(station.getOperator() + " (" + String.format(Locale.getDefault(),"%.2f", distance) + "km)");

        TextView street = view.findViewById(R.id.info_street);
        street.setText(station.getStreet() + " " + station.getNumber());

        TextView location = view.findViewById(R.id.info_location);
        location.setText(station.getPostal_code() + ", " + station.getLocation());

        TextView available = view.findViewById(R.id.info_available);
        if (UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId())) {
            available.setText(activity.getResources().getString(R.string.not_available));
            available.setTextColor(ContextCompat.getColor(activity, R.color.notAvailable));
        } else {
            available.setText(activity.getResources().getString(R.string.available));
            available.setTextColor(ContextCompat.getColor(activity, R.color.dashboard_item_2_dark));
        }

        TextView module = view.findViewById(R.id.info_module);
        if (station.getModule_type().equals(ModuleType.FAST_CHARGING)) {
            module.setText(activity.getResources().getString(R.string.supports_fast_charging));
            module.setTextColor(ContextCompat.getColor(activity, R.color.dashboard_item_2_dark));
        } else {
            module.setText(activity.getResources().getString(R.string.does_not_support_fast_charging));
            module.setTextColor(ContextCompat.getColor(activity, R.color.notAvailable));
        }

        TextView max_power = view.findViewById(R.id.info_power);
        max_power.setText("Max. " + activity.getResources().getString(R.string.power) + ": " + station.getConn_power() + "kW");

        TextView conn = view.findViewById(R.id.info_conn);
        conn.setText(activity.getResources().getString(R.string.connections) + station.getNumber_of_connections());

        AppCompatButton btn_report = view.findViewById(R.id.btn_report);

        btn_report.setOnClickListener(v -> {
            if (UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId())) return;
            ReportService reportService;

            if (adapter != null) reportService = new ReportService(
                    activity,
                    btn_report,
                    available,
                    station.getId(),
                    adapter);
            else reportService = new ReportService(
                    activity,
                    btn_report,
                    available,
                    station.getId(),
                    marker);

            reportService.showPopup();
        });

        if (UserManager.getApi_data().getDefect_stations_map().containsKey(station.getId())) {
            btn_report.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.secondary_text)));
            btn_report.setClickable(false);
        }

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.show();
        dialog.getWindow().setWindowAnimations(R.style.alertAnimations);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    /**
     * Closes the popup
     */
    private void dismissPopup() {
        dialog.dismiss();
    }

    /**
     * Update recycler views adapter.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter() {
        if (adapter == null) return;
        ArrayList<ChargingStation> stations;

        if (fav != null && nearby != null) {
            if (UserManager.getMyPosition() == null) {
                MessageService msgService = new MessageService(activity, activity.getResources().getString(R.string.location_cant_be_tracked), Gravity.TOP, true);
                msgService.sendToast();
                return;
            }

            if (nearby.isChecked() && fav.isChecked())
                stations = StationManager.sortByFavAndLocal();
            else if (nearby.isChecked() && !fav.isChecked())
                stations = StationManager.sort(
                        UserManager.getMyPosition().latitude,
                        UserManager.getMyPosition().longitude);
            else if (!nearby.isChecked() && fav.isChecked())
                stations = StationManager.getFavorites();
            else stations = StationManager.getStation_list();
        }
        else if (fav == null && nearby != null) {
            if (nearby.isChecked())
                stations = StationManager.getLocalDefects(
                        UserManager.getMyPosition().latitude,
                        UserManager.getMyPosition().longitude);
            else stations = StationManager.getDefects();
        }
        else return;
        stations.sort(StationManager.getSortByDistance());
        adapter.updateAdapter(stations);
    }

    /**
     * Call api endpoint.
     */
    private void callApi() {
        final String endpoint = isFav ? "del_fav" : "set_fav";

        CompletableFuture.supplyAsync(() -> DownloadService.getResponse(endpoint,
                new String[]{"userid=" + UserManager.getApi_data().getUserID(), "favorites=" + station.getId()}))
                .whenComplete((apiResponse, throwable) -> {
                    if (throwable != null || apiResponse.getResponseCode() != 1) {
                        MessageService service = new MessageService(activity, activity.getResources().getString(R.string.something_went_wrong), Gravity.TOP, true);
                        activity.runOnUiThread(service::sendToast);
                    }
                });
    }

    /**
     * Call api endpoint for devs.
     */
    private void devApi() {
        CompletableFuture.supplyAsync(() -> DownloadService.getResponse("del_def",
                new String[]{"station_id=" + station.getId()}))
                .whenComplete(((apiResponse, throwable) -> {
                    MessageService service;
                    if (throwable != null || apiResponse.getResponseCode() != 1)
                        service = new MessageService(activity, activity.getResources().getString(R.string.something_went_wrong), Gravity.TOP, true);
                    else service = new MessageService(activity, "Station (" + station.getId() + ") " + activity.getResources().getString(R.string.fixed) + "!", Gravity.TOP, false);
                    activity.runOnUiThread(service::sendToast);
                }));
    }
}
