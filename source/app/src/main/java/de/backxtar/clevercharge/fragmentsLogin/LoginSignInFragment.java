package de.backxtar.clevercharge.fragmentsLogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.concurrent.CompletableFuture;
import de.backxtar.clevercharge.MainActivity;
import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.managers.LoginManager;
import de.backxtar.clevercharge.managers.UserManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.messageService.MessageService;
import de.backxtar.clevercharge.services.messageService.Popup;

/**
 * SignIn Fragment.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class LoginSignInFragment extends Fragment {

    /**
     * Login constructor
     */
    public LoginSignInFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login_sign_in, container, false);
        FragmentManager manager = getParentFragmentManager();

        if (getActivity() != null) {
            TextView noAccount = view.findViewById(R.id.noAccount);
            AppCompatEditText userName = view.findViewById(R.id.login_username);
            AppCompatEditText userPasswd = view.findViewById(R.id.login_password);
            CheckBox remember_me = view.findViewById(R.id.remember_me);
            AppCompatButton login = view.findViewById(R.id.login_btn);

            initNoAccountListener(manager, noAccount);
            initLoginListener(userName, userPasswd, remember_me, login);
        }
        return view;
    }

    /**
     * Init listener for no account.
     * @param manager as object
     * @param noAccount as object
     */
    private void initNoAccountListener(FragmentManager manager, TextView noAccount) {
        noAccount.setOnClickListener(v -> manager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                .replace(R.id.body_container_login, LoginManager.getSignUp(), null)
                .commit());
    }

    /**
     * Init listener for login button.
     * @param userName as object
     * @param userPasswd as object
     * @param remember_me as object
     * @param login as object
     */
    private void initLoginListener(AppCompatEditText userName, AppCompatEditText userPasswd,
                                   CheckBox remember_me, AppCompatButton login) {

        login.setOnClickListener(v -> {
            String user, passwd;

            if (userName.getText() == null || userName.getText().toString().isEmpty() ||
                userPasswd.getText() == null || userPasswd.getText().toString().isEmpty()) {
                MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.empty_fields), Gravity.TOP, Popup.ERROR);
                msgService.sendToast();
                return;
            }
            user = userName.getText().toString();
            passwd = userPasswd.getText().toString();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            callApi(intent, user, passwd, remember_me);
        });
    }

    /**
     * Call api to login the user
     * @param user as String
     * @param passwd as String
     * @param remember_me as object
     */
    private void callApi(Intent intent, String user, String passwd, CheckBox remember_me) {
        CompletableFuture.supplyAsync(() -> DownloadService.getResponse("login",
                new String[]{"username=" + user, "userpasswd=" + passwd}))
                .whenComplete((apiResponse, throwable) -> {
                    if (throwable != null ||
                            apiResponse.getResponseCode() != 1 ||
                            apiResponse.getDefect_stations_map() == null ||
                            apiResponse.getFavorites() == null) {
                        MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.login_failed), Gravity.TOP, Popup.ERROR);
                        msgService.sendToast();
                        return;
                    }
                    UserManager.setApi_data(apiResponse);

                    if (remember_me.isChecked()) {
                        SharedPreferences sharedPref;
                        if (getActivity() != null) {
                            sharedPref = getActivity().getSharedPreferences("MySet", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit()
                                    .putString("USER_NAME", user)
                                    .putString("USER_PASSWD", passwd)
                                    .putBoolean("REMEMBER_ME", true)
                                    .putInt("RANGE", 10);
                            editor.apply();
                        }
                    }
                    startActivity(intent);

                    if (getActivity() != null)
                        getActivity().finish();
                });
    }
}