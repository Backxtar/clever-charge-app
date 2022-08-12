package de.backxtar.clevercharge.services;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.backxtar.clevercharge.R;

/**
 * MessageService object.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class MessageService {

    /* Global variables */

    /**
     * Current activity.
     */
    private Activity activity;

    /**
     * Message to be send.
     */
    private String message;

    /**
     * Position of the toast.
     */
    int gravity;

    /**
     * Is error message or success message.
     */
    boolean error;
    //========================

    /**
     * MessageService constructor.
     * @param activity as object
     * @param message as String
     * @param gravity as int
     * @param error as boolean
     */
    public MessageService(Activity activity, String message, int gravity, boolean error) {
        this.activity = activity;
        this.message = message;
        this.gravity = gravity;
        this.error = error;
    }

    /**
     * Send toast to top location.
     */
    public void sendToast() {
        if (activity != null)
            activity.runOnUiThread(() -> {
                LayoutInflater inflater = LayoutInflater.from(activity);
                View view;

                if (!error) view = inflater.inflate(R.layout.toast_success, null);
                else view = inflater.inflate(R.layout.toast_fail, null);

                TextView msg = view.findViewById(R.id.message_toast);
                msg.setText(message);

                Toast toast = new Toast(activity);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(gravity, 0, 0);
                toast.show();
            });
    }
}
