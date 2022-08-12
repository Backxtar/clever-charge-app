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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.MainActivity;
import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.data.ChargingStationAdapter;
import de.backxtar.clevercharge.managers.UserManager;

/**
 * ReportService object.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class ReportService {

    /* Global variables */

    /**
     * Current activity.
     */
    private final Activity activity;

    /**
     * Station ID.
     */
    private final int station_id;

    /**
     * ChargingStation adapter.
     */
    private ChargingStationAdapter adapter;

    /**
     * Button object.
     */
    private AppCompatButton button;

    /**
     * TextView for available or not.
     */
    private TextView available;

    /**
     * AlertDialog object of current instance.
     */
    private AlertDialog dialog;

    /**
     * Marker object of the current station.
     */
    private Marker marker;

    /**
     * Constructor one.
     * @param activity as object
     * @param button as object
     * @param available as object
     * @param station_id as int
     * @param marker as object
     */
    public ReportService(Activity activity, AppCompatButton button, TextView available, int station_id, Marker marker) {
        this.activity = activity;
        this.button = button;
        this.available = available;
        this.station_id = station_id;
        this.marker = marker;
    }

    /**
     * Constructor two.
     * @param activity as object
     * @param button as object
     * @param available as object
     * @param station_id as int
     * @param adapter as object
     */
    public ReportService(Activity activity, AppCompatButton button, TextView available, int station_id, ChargingStationAdapter adapter) {
        this.activity = activity;
        this.button = button;
        this.available = available;
        this.station_id = station_id;
        this.adapter = adapter;
    }

    /**
     * Show popup window.
     */
    public void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.report_station, null);

        AppCompatEditText report_msg = view.findViewById(R.id.message_report);

        ImageButton cancel = view.findViewById(R.id.cancel_report);
        cancel.setOnClickListener(v -> dismissPopup());

        ImageButton report = view.findViewById(R.id.send_report);
        report.setOnClickListener(v -> {
            report.setClickable(false);

            if (report_msg.getText() == null || report_msg.getText().toString().isEmpty()) {
                MessageService msgService = new MessageService(activity, activity.getResources().getString(R.string.please_enter_a_description), Gravity.TOP, true);
                msgService.sendToast();
            } else {
                String msg = report_msg.getText().toString();
                CompletableFuture.supplyAsync(() -> DownloadService.getResponse("set_def",
                        new String[]{"station_id=" + station_id, "report_msg=" + msg}))
                        .whenComplete(((apiResponse, throwable) -> {
                            MessageService service;
                            if (throwable != null || apiResponse.getResponseCode() != 1)
                                service = new MessageService(activity, activity.getResources().getString(R.string.something_went_wrong), Gravity.TOP, true);
                            else service = new MessageService(activity, activity.getResources().getString(R.string.thanks_for_report), Gravity.TOP, false);
                            activity.runOnUiThread(service::sendToast);
                        }));
                UserManager.getApi_data().getDefect_stations_map().put(station_id, msg);
                button.setClickable(false);
                button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.secondary_text)));
                available.setText(activity.getResources().getString(R.string.not_available));
                if (marker != null) marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                available.setTextColor(ContextCompat.getColor(activity, R.color.notAvailable));
                MainActivity.updateBadges(R.id.nav_dev, UserManager.getApi_data().getDefect_stations_map().size());
                dismissPopup();
            }
            updateAdapter();
            report.setClickable(true);
        });

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.show();
        dialog.getWindow().setWindowAnimations(R.style.alertAnimations);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    /**
     * Dismiss popup window.
     */
    private void dismissPopup() {
        dialog.dismiss();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter() {
        if (adapter == null) return;
        adapter.notifyDataSetChanged();
    }

}
