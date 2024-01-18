package com.feature.tui.widget.recyclerview;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;

import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 11:13
 */
public class XUIRVDragItemTouchHelper {

    public XUIRVDragItemTouchHelper(Context context, RecyclerView recycler, List datas) {
        this(context, recycler, datas, null);
    }

    public XUIRVDragItemTouchHelper(Context context, RecyclerView recycler, List datas, DragCallBack callBack) {
        final int[] pos = new int[2];
        ItemTouchHelper helper = new ItemTouchHelper((new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFrlg = 0;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    dragFrlg = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                }

                return makeMovementFlags(dragFrlg, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int j = fromPosition; j < toPosition; j++) {
                        Collections.swap(datas, j, j + 1);
                    }
                } else {
                    for (int j = fromPosition; j >= toPosition + 1; j--) {
                        Collections.swap(datas, j, j - 1);
                    }
                }

                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter != null) {
                    adapter.notifyItemMoved(fromPosition, toPosition);
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    pos[0] = viewHolder.getAdapterPosition();
                    if (viewHolder != null) {
                        View itemView = viewHolder.itemView;
                        if (itemView != null) {
                            itemView.setBackgroundColor(context.getResources().getColor(R.color.xui_main_gray));
                        }
                    }

                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    //获取系统震动服务//震动70毫秒
                    vib.vibrate(70L);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                pos[1] = viewHolder.getAdapterPosition();//必须在 notifyDataSetChanged之前调用
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();  //完成拖动后刷新适配器，这样拖动后删除就不会错乱
                }
                if (callBack != null) {
                    callBack.onDragFinish(pos[0], pos[1]);
                }
            }
        }));
        helper.attachToRecyclerView(recycler);
    }

    public interface DragCallBack {
        void onDragFinish(int newPos, int oldPos);
    }

}
