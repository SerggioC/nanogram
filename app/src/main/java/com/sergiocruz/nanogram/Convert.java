package com.sergiocruz.nanogram;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;

import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

public class Convert {
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = mSpace;
        }
    }


    /**
     * Prepares the shared element transition to the pager fragment,
     * as well as the other transitions that affect the flow.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void prepareTransitionsJava(Context context, Activity activity, final RecyclerView recyclerView, Fragment fragment) {
        TransitionInflater from = TransitionInflater.from(activity);
        Transition exitTransition = from.inflateTransition(R.transition.grid_exit_transition);
        exitTransition.setDuration(325);

        fragment.setExitTransition(exitTransition);

        // A similar mapping is set at the ArticlePagerFragment with a setEnterSharedElementCallback.
        //val exitSharedElementCallback =

        activity.setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        super.onMapSharedElements(names, sharedElements);
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(1);
                        if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.image_item));
                    }
                }
        );

        activity.postponeEnterTransition();
    }

}
