package de.backxtar.clevercharge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;

import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.managers.LoginManager;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.messageService.MessageService;
import de.backxtar.clevercharge.services.messageService.Popup;

/**
 * Login activity.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class Login extends AppCompatActivity {

    /* Global variables */

    /**
     * Fragment manager.
     */
    private FragmentManager fragmentManager;

    /**
     * Intent for swapping screen.
     */
    private Intent intentThis;
    //=======================================

    /**
     * On create activity.
     * @param savedInstanceState as object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        intentThis = new Intent(this, MainActivity.class);

        SharedPreferences sharedPref = this.getSharedPreferences("MySet", MODE_PRIVATE);
        String userPref = sharedPref.getString("USER_NAME", null);
        String passPref = sharedPref.getString("USER_PASSWD", null);
        int rangePref = sharedPref.getInt("RANGE", 0);

        if (userPref != null && passPref != null && rangePref != 0) {
            StationManager.setRange(rangePref);

            CompletableFuture.supplyAsync(() -> DownloadService.getResponse("login",
                    new String[]{"username=" + userPref, "userpasswd=" + passPref}))
                    .whenComplete((apiResponse, throwable) -> {
                        if (throwable != null ||
                                apiResponse.getResponseCode() != 1 ||
                                apiResponse.getDefect_stations_map() == null ||
                                apiResponse.getFavorites() == null) {
                            MessageService msgService = new MessageService(this, getResources().getString(R.string.login_failed), Gravity.TOP, Popup.ERROR);
                            msgService.sendToast();
                            return;
                        }
                        UserManager.setApi_data(apiResponse);
                        startActivity(intentThis);
                        this.finish();
                    });
        } else StationManager.setRange(10);

        this.fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.body_container_login, LoginManager.getSignIn(), LoginManager.getSignIn().getClass().getName())
                .commit();

        if (!intent.getBooleanExtra("result_articles", false) ||
            !intent.getBooleanExtra("result_download", false)) {
            MessageService msgService = new MessageService(this, getResources().getString(R.string.restart_the_app), Gravity.TOP, Popup.ERROR);
            msgService.sendToast();
            new Handler().postDelayed(() -> System.exit(0), 3000);
        }
    }

    /**
     * Disable back button.
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}