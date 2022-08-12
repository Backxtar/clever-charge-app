package de.backxtar.clevercharge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.data.Article;
import de.backxtar.clevercharge.data.ChargingStation;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.MessageService;

/**
 * LoadingScreen of the app.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class LoadingScreen extends AppCompatActivity {

    /* Global variables */

    /**
     * New intent for switching.
     */
    private Intent intent;
    //========================

    /**
     * On create activity.
     * @param savedInstanceState as object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading_screen);
        intent = new Intent(LoadingScreen.this, Login.class);

        CompletableFuture<ArrayList<ChargingStation>> stationList = CompletableFuture.supplyAsync(DownloadService::getStations)
                .whenComplete((stations, throwable) -> {
                    if (throwable != null || stations == null) {
                        intent.putExtra("result_download", false);
                        throw new RuntimeException("Cant download stations.");
                    }
                    StationManager.setStation_list(stations);
                    intent.putExtra("result_download", true);
                });

        CompletableFuture<ArrayList<Article>> articleList = CompletableFuture.supplyAsync(DownloadService::getArticles)
                .whenComplete((articles, throwable) -> {
                    if (throwable != null || articles == null) {
                        intent.putExtra("result_articles", false);
                        throw new RuntimeException("Cant download articles.");
                    }
                    UserManager.setArticles(articles);
                    intent.putExtra("result_articles", true);
                });

        CompletableFuture.allOf(stationList, articleList)
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        MessageService msg = new MessageService(this, getResources().getString(R.string.error_while_getting_data), Toast.LENGTH_LONG, true);
                        msg.sendToast();
                        new Handler().postDelayed(() -> System.exit(0), 3000);
                        return;
                    }
                    startActivity(intent);
                    finish();
                });
    }
}