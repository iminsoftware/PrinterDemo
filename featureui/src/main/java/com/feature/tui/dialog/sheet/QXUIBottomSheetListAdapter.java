package com.feature.tui.dialog.sheet;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QXUIBottomSheetListAdapter extends RecyclerView.Adapter<QXUIBottomSheetListAdapter.VH> {

    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_FOOTER = 2;
    public static final int ITEM_TYPE_NORMAL = 3;

    @Nullable
    private View mHeaderView;
    @Nullable
    private View mFooterView;
    private List<QXUIBottomSheetListItemModel> mData = new ArrayList<>();
    private final boolean mNeedMark;
    private final boolean mGravityCenter;
    private int mCheckedIndex = -1;
    private OnItemClickListener mOnItemClickListener;

    public QXUIBottomSheetListAdapter(boolean needMark, boolean gravityCenter) {
        mNeedMark = needMark;
        mGravityCenter = gravityCenter;
    }

    public void setCheckedIndex(int checkedIndex) {
        mCheckedIndex = checkedIndex;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(@Nullable View headerView,
                        @Nullable View footerView,
                        List<QXUIBottomSheetListItemModel> data) {
        mHeaderView = headerView;
        mFooterView = footerView;
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null) {
            if (position == 0) {
                return ITEM_TYPE_HEADER;
            }
        }
        if (position == getItemCount() - 1) {
            if (mFooterView != null) {
                return ITEM_TYPE_FOOTER;
            }
        }
        return ITEM_TYPE_NORMAL;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new VH(mHeaderView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new VH(mFooterView);
        }

        final VH vh = new VH(new TextView(
                parent.getContext()));
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int adapterPosition = vh.getAdapterPosition();
                    int dataPos = mHeaderView != null ? adapterPosition - 1 : adapterPosition;
                    mOnItemClickListener.onClick(vh, dataPos, mData.get(dataPos));
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (holder.getItemViewType() != ITEM_TYPE_NORMAL) {
            return;
        }
        if (mHeaderView != null) {
            position--;
        }
        QXUIBottomSheetListItemModel itemModel = mData.get(position);
        TextView itemView = (TextView) holder.itemView;
        itemView.setText(itemModel.text);
    }

    @Override
    public int getItemCount() {
        return mData.size() + (mHeaderView != null ? 1 : 0) + (mFooterView != null ? 1 : 0);
    }

    public static class VH extends RecyclerView.ViewHolder {

        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onClick(VH vh, int dataPos, QXUIBottomSheetListItemModel model);
    }
}
