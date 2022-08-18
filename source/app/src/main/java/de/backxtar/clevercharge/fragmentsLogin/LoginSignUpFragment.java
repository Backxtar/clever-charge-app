package de.backxtar.clevercharge.fragmentsLogin;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.CompletableFuture;

import de.backxtar.clevercharge.R;
import de.backxtar.clevercharge.managers.LoginManager;
import de.backxtar.clevercharge.services.DownloadService;
import de.backxtar.clevercharge.services.messageService.MessageService;
import de.backxtar.clevercharge.services.messageService.Popup;

/**
 * SignUp fragment.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class LoginSignUpFragment extends Fragment {

    /**
     * Sign up constructor
     */
    public LoginSignUpFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login_sign_up, container, false);
        FragmentManager manager = getParentFragmentManager();

        TextView haveAccount = view.findViewById(R.id.haveAccount);
        AppCompatEditText userName = view.findViewById(R.id.register_username);
        AppCompatEditText userPasswd = view.findViewById(R.id.register_password);
        AppCompatEditText userEmail = view.findViewById(R.id.register_email);
        AppCompatButton register = view.findViewById(R.id.register_btn);

        initHaveAccountListener(manager, haveAccount);
        initRegisterListener(manager, register, userName, userPasswd, userEmail);

        return view;
    }

    /**
     * Init listener for haveAccount.
     * @param manager as object
     * @param haveAccount as object
     */
    private void initHaveAccountListener(FragmentManager manager, TextView haveAccount) {
        haveAccount.setOnClickListener(v -> manager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                .replace(R.id.body_container_login, LoginManager.getSignIn(), null)
                .commit());
    }

    /**
     * Init listener for register button.
     * @param manager as object
     * @param register as object
     * @param userName as object
     * @param userPasswd as object
     * @param userEmail as object
     */
    private void initRegisterListener(FragmentManager manager, AppCompatButton register, AppCompatEditText userName,
                                      AppCompatEditText userPasswd, AppCompatEditText userEmail) {
        register.setOnClickListener(v -> {
            String user, passwd, email;

            if (userName.getText().toString().isEmpty() ||
                    userPasswd.getText().toString().isEmpty() ||
                    userEmail.getText().toString().isEmpty()) {
                MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.empty_fields), Gravity.TOP, Popup.ERROR);
                msgService.sendToast();
                return;
            }
            user = userName.getText().toString();
            passwd = userPasswd.getText().toString();
            email = userEmail.getText().toString();

            callApi(manager, user, passwd, email);
        });
    }

    /**
     * Call api to register the user
     * @param manager as object
     * @param user as String
     * @param passwd as String
     * @param email as String
     */
    private void callApi(FragmentManager manager, String user, String passwd, String email) {
        CompletableFuture.supplyAsync(() -> DownloadService.getResponse("register",
                new String[]{"username=" + user, "userpasswd=" + passwd, "useremail=" + email}))
                .whenComplete((apiResponse, throwable) -> {
                    if (apiResponse.getResponseCode() == 3) {
                        MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.user_exists), Gravity.TOP, Popup.ERROR);
                        msgService.sendToast();
                        return;
                    }
                    if (apiResponse.getResponseCode() != 1 || throwable != null) {
                        MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.register_failed), Gravity.TOP, Popup.ERROR);
                        msgService.sendToast();
                        return;
                    }
                    manager.beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out)
                            .replace(R.id.body_container_login, LoginManager.getSignIn(), null)
                            .commit();
                    MessageService msgService = new MessageService(getActivity(), getResources().getString(R.string.register_success), Gravity.TOP, Popup.SUCCESS);
                    msgService.sendToast();
                });
    }
}