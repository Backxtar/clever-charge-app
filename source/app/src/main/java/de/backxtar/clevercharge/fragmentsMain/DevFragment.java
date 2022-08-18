package de.backxtar.clevercharge.fragmentsMain;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.data.ChargingStationAdapter;
import de.backxtar.clevercharge.interfaces.RecyclerViewInterface;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.messageService.MessageService;
import de.backxtar.clevercharge.services.PopupService;
import de.backxtar.clevercharge.services.SpacingItemService;
import de.backxtar.clevercharge.services.messageService.Popup;

/**
 * Dev fragment.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class DevFragment extends Fragment implements RecyclerViewInterface {

    /* Global variables */

    /**
     * ChargingStation adapter.
     */
    private ChargingStationAdapter adapter;

    /**
     * TextViews for all stations and defect stations.
     */
    private TextView all, def;

    /**
     * Switch for local.
     */
    private SwitchCompat local;
    //======================================

    /**
     * Dev constructor.
     */
    public DevFragment() {
        // Required empty public constructor
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dev, container, false);
        initText(view);
        initSwipeRefresh(view);
        initRecyclerView(view);
        initSwitch(view);

        return view;
    }

    /**
     * Init textView objects.
     * @param view as object
     */
    private void initText(View view) {
        all = view.findViewById(R.id.all_stations_dev);
        def = view.findViewById(R.id.def_stations_def);
        updateInformation();
    }

    /**
     * Init swipe to refresh object.
     * @param view as object
     */
    private void initSwipeRefresh(View view) {
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_dev);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(() -> CompletableFuture.supplyAsync(() -> DownloadService.getResponse("get_def"))
                .whenComplete((apiResponse, throwable) -> {
                    MessageService msg;

                    if (throwable != null || apiResponse.getResponseCode() != 1) {
                        msg = new MessageService(getActivity(), getResources().getString(R.string.something_went_wrong), Gravity.TOP, Popup.ERROR);
                        msg.sendToast();
                        return;
                    }

                    if (!UserManager.getApi_data().getDefect_stations_map().equals(apiResponse.getDefect_stations_map())) {
                        msg = new MessageService(getActivity(), getString(R.string.download_update), Gravity.TOP, Popup.INFO);
                        UserManager.getApi_data().setDefect_stations_map(apiResponse.getDefect_stations_map());
                    }
                    else msg = new MessageService(getActivity(), getString(R.string.no_update), Gravity.TOP, Popup.INFO);
                    msg.sendToast();

                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            updateAdapter(local.isChecked());
                            updateInformation();
                            refreshLayout.setRefreshing(false);
                        });
                }));
    }

    /**
     * Init recyclerView object.
     * @param view as object
     */
    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.dev_rv);
        ArrayList<ChargingStation> sorted = StationManager.getDefects();
        sorted.sort(StationManager.getSortByDistance());
        adapter = new ChargingStationAdapter(this, sorted, getContext());
        SpacingItemService spacingItemService = new SpacingItemService(50);
        recyclerView.addItemDecoration(spacingItemService);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Init switch object.
     * @param view as object
     */
    private void initSwitch(View view) {
        local = view.findViewById(R.id.only_local_dev);
        local.setChecked(UserManager.getSaveStateService().isLocalDef());
        updateAdapter(local.isChecked());
        local.setOnCheckedChangeListener((buttonView, isChecked) -> {
            UserManager.getSaveStateService().setLocalDef(isChecked);
            updateAdapter(isChecked);
        });
    }

    /**
     * RecyclerViewInterface method of myself.
     * @param position as int
     * @param station as object
     */
    @Override
    public void onItemClick(int position, ChargingStation station) {
        PopupService popupService = new PopupService(getActivity(), station, adapter, local, def);
        popupService.showPopup();
    }

    /**
     * Update textViews information.
     */
    @SuppressLint("SetTextI18n")
    private void updateInformation() {
        all.setText(Integer.toString(StationManager.getStationAmount()));
        def.setText(Integer.toString(StationManager.getDefects().size()));
    }

    /**
     * Update recyclerViews adapter
     * @param isChecked as boolean
     */
    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter(boolean isChecked) {
        ArrayList<ChargingStation> sorted;

        if (isChecked) {
            if (UserManager.getMyPosition() != null) {
                sorted = StationManager.getLocalDefects(
                        UserManager.getMyPosition().latitude,
                        UserManager.getMyPosition().longitude);
                sorted.sort(StationManager.getSortByDistance());
                adapter.updateAdapter(sorted);
            }
            else {
                MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.location_cant_be_tracked), Gravity.CENTER, Popup.ERROR);
                msgService.sendToast();
            }
        } else {
            sorted = StationManager.getDefects();
            sorted.sort(StationManager.getSortByDistance());
            adapter.updateAdapter(sorted);
        }
        adapter.notifyDataSetChanged();
    }
}