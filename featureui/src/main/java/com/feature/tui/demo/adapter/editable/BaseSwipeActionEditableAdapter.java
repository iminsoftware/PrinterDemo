package com.feature.tui.demo.adapter.editable;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.editable.inner.Selectable;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.recyclerview.XUIRVItemSwipeAction;
import com.feature.tui.widget.recyclerview.XUISwipeAction;
import com.feature.tui.widget.recyclerview.XUISwipeViewHolder;

import java.util.List;

public abstract class BaseSwipeActionEditableAdapter<M extends Selectable, VH extends XUISwipeViewHolder>
        extends BaseEditableAdapter<M, VH> {
    private final XUISwipeAction mDeleteAction;
    private final Context context;
    private XUIRVItemSwipeAction swipeAction;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onBindViewHolder(VH vh, int position) {
        super.onBindViewHolder(vh, position);
        onBindSwipeAction(vh, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onBindViewHolder(VH vh, int position, List<Object> payloads) {
        super.onBindViewHolder(vh, position, payloads);
        onBindSwipeAction(vh, payloads);
    }

    private void onBindSwipeAction(VH vh, List<Object> payloads) {
        if (!vh.hasAction()) {
            vh.addSwipeAction(mDeleteAction);
        }
    }

    public boolean isOpenDelete() {
        return swipeAction.mSelected != null;
    }

    public void clear() {
        swipeAction.clear();
    }

    protected boolean isSwipeDeleteWhenOnlyOneAction() {
        return false;
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        swipeAction = new XUIRVItemSwipeAction(isSwipeDeleteWhenOnlyOneAction(), new SwipeActionCallback());
        swipeAction.attachToRecyclerView(recyclerView);
    }

    public BaseSwipeActionEditableAdapter(Context context) {
        super(context);
        this.context = context;
        XUISwipeAction.ActionBuilder actionBuilder = new XUISwipeAction.ActionBuilder().textSize(XUiDisplayHelper.sp2px(this.context, 14)).textColor(Color.WHITE).paddingStartEnd(XUiDisplayHelper.dp2px(this.context, 14));
        int bgColor = context.getResources().getColor(R.color.xui_config_color_error);
        mDeleteAction = actionBuilder.text(context.getString(R.string.xui_delete)).backgroundColor(bgColor).build();
    }

    public class SwipeActionCallback
            extends XUIRVItemSwipeAction.Callback {
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            remove(viewHolder.getAdapterPosition());
        }

        public int getSwipeDirection(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return checkEditMode() | !getEnableEditMode() ? XUIRVItemSwipeAction.SWIPE_NONE : XUIRVItemSwipeAction.SWIPE_LEFT;
        }

        public void onClickAction(XUIRVItemSwipeAction swipeAction, RecyclerView.ViewHolder selected, XUISwipeAction action) {
            if (action == BaseSwipeActionEditableAdapter.this.mDeleteAction) {
                if (selected.getAdapterPosition() >= 0) {
                    remove(selected.getAdapterPosition());
                }
            } else {
                swipeAction.clear();
            }
        }
    }

}
