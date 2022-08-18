package de.backxtar.clevercharge.fragmentsMain;

import android.annotation.SuppressLint;
import android.os.Bundle;

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
import de.backxtar.clevercharge.data.Article;
import de.backxtar.clevercharge.data.ArticleAdapter;
import de.backxtar.clevercharge.data.ModuleType;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.messageService.MessageService;
import de.backxtar.clevercharge.services.SpacingItemService;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.services.messageService.Popup;

/**
 * ChargingStation container.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class HomeFragment extends Fragment {

    /* Global variables */

    /**
     * Article adapter.
     */
    private ArticleAdapter adapter;
    //==============================

    /**
     * Home Constructor.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * On create fragment.
     * @param savedInstanceState
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
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView all_stations = view.findViewById(R.id.all_stations);
        TextView fc_stations = view.findViewById(R.id.fc_stations);
        TextView nc_stations = view.findViewById(R.id.nc_stations);

        all_stations.setText(Integer.toString(StationManager.getStationAmount()));
        fc_stations.setText(Integer.toString(StationManager.sort(ModuleType.FAST_CHARGING).size()));
        nc_stations.setText(Integer.toString(StationManager.sort(ModuleType.STANDARD).size()));

        RecyclerView recyclerView = view.findViewById(R.id.fav_rv);
        adapter = new ArticleAdapter(UserManager.getArticles(), getActivity());
        SpacingItemService spacingItemService = new SpacingItemService(55);
        recyclerView.addItemDecoration(spacingItemService);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initSwipeRefresh(view);

        return view;
    }

    /**
     * Init refresh object
     * @param view as object
     */
    private void initSwipeRefresh(View view) {
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_home);
        refreshLayout.setColorSchemeResources(R.color.primary);
        refreshLayout.setOnRefreshListener(() -> CompletableFuture.supplyAsync(DownloadService::getArticles)
                .whenComplete((articles, throwable) -> {
                    MessageService msgService;

                    if (throwable != null || articles == null) {
                        msgService = new MessageService(getActivity(), getResources().getString(R.string.something_went_wrong), Gravity.TOP, Popup.ERROR);
                        msgService.sendToast();
                        return;
                    }

                    MessageService msg;
                    if (UserManager.compareArticles(articles)) {
                        msg = new MessageService(getActivity(), getString(R.string.download_update), Gravity.TOP, Popup.INFO);
                        UserManager.setArticles(articles);
                    }
                    else msg = new MessageService(getActivity(), getString(R.string.no_update), Gravity.TOP, Popup.INFO);
                    msg.sendToast();

                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            adapter.setArticles(UserManager.getArticles());
                            refreshLayout.setRefreshing(false);
                        });
                }));
    }
}