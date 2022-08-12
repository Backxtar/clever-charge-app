package de.backxtar.clevercharge.services;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * SpacingItemService object.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class SpacingItemService extends RecyclerView.ItemDecoration {

    /* Global variables */

    /**
     * Value for spacer.
     */
    private final int verticalSpaceHeight;
    //=======================================

    /**
     * Constructor for spacer.
     * @param verticalSpaceHeight as int
     */
    public SpacingItemService(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    /**
     * Get item offset and set spacer.
     * @param outRect as object
     * @param view as object
     * @param parent as object
     * @param state as object
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getAdapter() != null && parent.getAdapter().getItemCount() != 0) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
                outRect.bottom = verticalSpaceHeight;
            else outRect.bottom = 210;

            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = 35;
        }
    }
}
