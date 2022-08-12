package de.backxtar.clevercharge.fragmentsMain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.concurrent.CompletableFuture;
import de.backxtar.clevercharge.Login;
import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.managers.StationManager;
import de.backxtar.clevercharge.managers.UserManager;

/**
 * Settings fragment.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class SettingsFragment extends Fragment {

    /**
     * Settings constructor.
     */
    public SettingsFragment() {
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
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initTextFields(view);
        initProgress(view);
        initLogout(view);

        return view;
    }

    /**
     * Init display text fields objects.
     * @param view as object
     */
    private void initTextFields(View view) {
        TextView userName = view.findViewById(R.id.account_username);
        TextView userMail = view.findViewById(R.id.account_email);
        TextView isDev = view.findViewById(R.id.account_role);

        userName.setText(UserManager.getApi_data().getUserNAME());
        userMail.setText(UserManager.getApi_data().getUserEMAIL());

        if(UserManager.getApi_data().isDEV()) isDev.setText(getResources().getString(R.string.dev));
        else isDev.setText(getResources().getString(R.string.user));
    }

    /**
     * Init progress bar object.
     * @param view as object
     */
    @SuppressLint("SetTextI18n")
    private void initProgress(View view) {
        TextView range = view.findViewById(R.id.range_value);
        AppCompatSeekBar bar = view.findViewById(R.id.range_seekbar);

        bar.setProgress(StationManager.getRange());
        range.setText(StationManager.getRange() + "km");

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBar.setProgress(i);
                range.setText(i + "km");
                StationManager.setRange(i);

                if (getActivity() != null) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("MySet", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit().putInt("RANGE", i);
                    editor.apply();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Init logout button object.
     * @param view as object
     */
    private void initLogout(View view) {
        AppCompatButton button = view.findViewById(R.id.btn_logout);

        button.setOnClickListener(v -> {
            Activity activity = getActivity();

            CompletableFuture.supplyAsync(() -> logout(activity))
                    .whenComplete((success, throwable) -> {
                        if (throwable != null || !success || activity == null)
                            return;
                        activity.finish();
                    });
        });
    }

    /**
     * Logout function of the button {@link #initLogout(View)}.
     * @param activity as object
     * @return success or fail as boolean
     */
    private boolean logout(Activity activity) {
        Intent intent = new Intent(activity, Login.class);

        if (getActivity() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("MySet", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit().clear();
            editor.apply();
        }

        intent.putExtra("result_articles", UserManager.getArticles() != null);
        intent.putExtra("result_download", StationManager.getStation_list() != null
                && StationManager.getStationAmount() != 0);

        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}