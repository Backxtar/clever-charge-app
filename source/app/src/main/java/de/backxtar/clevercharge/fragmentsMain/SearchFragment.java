package de.backxtar.clevercharge.fragmentsMain;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.data.APIResponse;
import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.data.ChargingStationAdapter;
import de.backxtar.clevercharge.interfaces.RecyclerViewInterface;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.MessageService;
import de.backxtar.clevercharge.services.PopupService;
import de.backxtar.clevercharge.services.SpacingItemService;

/**
 * Search fragment.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class SearchFragment extends Fragment implements RecyclerViewInterface {

    /* Global variables */

    /**
     * ChargingStation adapter.
     */
    private ChargingStationAdapter adapter;

    /**
     * ArrayList of adapters chargingStations.
     */
    private ArrayList<ChargingStation> stations;

    /**
     * Switched for nearby and favorites.
     */
    private SwitchCompat nearby, fav;

    /**
     * EditText field for search option.
     */
    private AppCompatEditText search;
    //===========================================

    /**
     * Search constructor.
     */
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * On fragment create.
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ArrayList<ChargingStation> sorted = StationManager.getStation_list();
        sorted.sort(StationManager.getSortByDistance());
        stations = sorted;

        initSwipeRefresh(view);
        initRecyclerView(view);
        initSearch(view);
        initSwitch(view);
        updateComponents();

        return view;
    }

    /**
     * Init refresh object.
     * @param view as object
     */
    private void initSwipeRefresh(View view) {
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_search);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(() -> {
            CompletableFuture<APIResponse> futureResponseDef = CompletableFuture.supplyAsync(() -> DownloadService.getResponse("get_def"))
                    .whenComplete((apiResponse, throwable) -> {
                        if (throwable != null || apiResponse.getResponseCode() != 1) {
                            MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.cant_load_defect_stations), Gravity.TOP, true);
                            msgService.sendToast();
                        } else UserManager.getApi_data().setDefect_stations_map(apiResponse.getDefect_stations_map());
                    });

            CompletableFuture<ArrayList<ChargingStation>> futureStationResponse = CompletableFuture.supplyAsync(DownloadService::getStations)
                    .whenComplete((stations, throwable) -> {
                       if (throwable != null || stations == null) {
                           MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.cant_get_station_list), Gravity.TOP, true);
                           msgService.sendToast();
                       } else StationManager.setStation_list(stations);
                    });

            CompletableFuture.allOf(futureResponseDef, futureStationResponse)
                    .whenComplete((unused, throwable) -> {
                        MessageService msgService;

                        if (throwable != null) {
                            msgService = new MessageService(getActivity(), getResources().getString(R.string.error_while_getting_data), Gravity.TOP, true);
                            msgService.sendToast();
                            return;
                        }
                        msgService = new MessageService(getActivity(), getResources().getString(R.string.update_successful), Gravity.TOP, false);
                        msgService.sendToast();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                updateAdapter();
                                refreshLayout.setRefreshing(false);
                            });
                        }
                    });
        });
    }

    /**
     * Init recyclerview object.
     * @param view as object
     */
    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.all_rv);
        adapter = new ChargingStationAdapter(this, stations, getContext());
        SpacingItemService spacingItemService = new SpacingItemService(50);
        recyclerView.addItemDecoration(spacingItemService);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Custom interface for on item click of recyclerview.
     * @param position as int
     * @param station as object
     */
    @Override
    public void onItemClick(int position, ChargingStation station) {
        PopupService popupService = new PopupService(getActivity(), station, adapter, nearby, fav);
        popupService.showPopup();
    }

    /**
     * Init search line object.
     * @param view as object
     */
    private void initSearch(View view) {
        search = view.findViewById(R.id.search_line);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO: Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                CompletableFuture.supplyAsync(() -> filterList(s))
                        .whenComplete((list, throwable) -> {
                            UserManager.getSaveStateService().setSearch(s.toString());
                           if (throwable == null && getActivity() != null)
                               getActivity().runOnUiThread(() -> adapter.updateAdapter(list));
                        });
            }
        });
    }

    /**
     * Filter function for recyclerview.
     * @param s as Editable
     * @return new ArrayList<ChargingStation>
     */
    private ArrayList<ChargingStation> filterList(Editable s) {
        return StationManager.getStation_list().stream()
                .filter(station -> station.getOperator().toLowerCase().contains(s.toString().toLowerCase()) ||
                        station.getStreet().toLowerCase().contains(s.toString().toLowerCase()) ||
                        station.getNumber().toLowerCase().contains(s.toString().toLowerCase()) ||
                        Integer.toString(station.getPostal_code()).toLowerCase().contains(s.toString().toLowerCase()) ||
                        station.getLocation().toLowerCase().contains(s.toString().toLowerCase()) ||
                        StationManager.convertEnum(getActivity(), station.getModule_type()).toLowerCase().contains(s.toString().toLowerCase()))
                .sorted(StationManager.getSortByDistance()).collect(Collectors.toCollection(ArrayList<ChargingStation>::new));
    }

    /**
     * Init switches.
     * @param view as object
     */
    private void initSwitch(View view) {
        nearby = view.findViewById(R.id.only_local);
        fav = view.findViewById(R.id.only_fav);

        nearby.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateAdapter();
            UserManager.getSaveStateService().setLocal(isChecked);
        });

        fav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateAdapter();
            UserManager.getSaveStateService().setFav(isChecked);
        });
    }

    /**
     * Update components after screen swap.
     */
    private void updateComponents() {
        fav.setChecked(UserManager.getSaveStateService().isFav());
        nearby.setChecked(UserManager.getSaveStateService().isLocal());
        search.setText(UserManager.getSaveStateService().getSearch());
        updateAdapter();
    }

    /**
     * Update adapter function for recyclerview.
     */
    private void updateAdapter() {
        if (UserManager.getMyPosition() == null) {
            MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.location_cant_be_tracked), Gravity.CENTER, true);
            msgService.sendToast();
            return;
        }
        ArrayList<ChargingStation> sorted;

        if (nearby.isChecked() && fav.isChecked())
            sorted = StationManager.sortByFavAndLocal();
        else if (nearby.isChecked() && !fav.isChecked())
            sorted = StationManager.sort(
                    UserManager.getMyPosition().latitude,
                    UserManager.getMyPosition().longitude);
        else if (!nearby.isChecked() && fav.isChecked())
            sorted = StationManager.getFavorites();
        else sorted = StationManager.getStation_list();

        sorted.sort(StationManager.getSortByDistance());
        stations = sorted;
        adapter.updateAdapter(stations);

        if (search.getText() != null && !search.getText().toString().isEmpty()) {
            String toSearch = search.getText().toString();
            search.setText(toSearch);
        }
    }
}