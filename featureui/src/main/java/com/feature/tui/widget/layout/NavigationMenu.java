package com.feature.tui.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 15:26
 */
public class NavigationMenu extends LinearLayout {
    private Context context;
    public NavigationMenu(Context context) {
        this(context,null);
    }

    public NavigationMenu(Context context,  AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private LayoutInflater inflater;
    private  TextView titleTv;
    private  ImageView titleIconIv0;
    private  ImageView titleIconIv1;
    private  NavigationMenu.MenuItemAdapter menuAdapter;
    private NavigationMenu.OnMenuItemClickListener onMenuItemClickCb;
    private NavigationMenu.OnLoadUrlCallback onLoadUrlCb;


    public static final int ITEM_TYPE_DIVIDER_LINE = 1;
    public static final int ITEM_TYPE_CONTENT = 2;
    public static final int ITEM_VIEW_GROUP_COUNT = 5;

    public final void setTitle( String title) {
        this.titleTv.setText((CharSequence)title);
    }

    public final ImageView getTitleLeftIcon() {
        return this.titleIconIv0;
    }

    public final ImageView getTitleRightIcon() {
        return this.titleIconIv1;
    }

    public final void setMenuClickListener( NavigationMenu.OnMenuItemClickListener listener) {
        this.onMenuItemClickCb = listener;
    }

    public final void setImageLoadCallback( NavigationMenu.OnLoadUrlCallback listener) {
        this.onLoadUrlCb = listener;
    }

    public final void setMenus( List items) {
        this.menuAdapter.setList(items);
        this.menuAdapter.notifyDataSetChanged();
    }

    private void initView(){
        setOrientation(VERTICAL);
        DrawerLayout.LayoutParams layoutParams = new DrawerLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.START;
        setLayoutParams(layoutParams);

        inflater = LayoutInflater.from(context);
        View titleLayout = inflater.inflate(R.layout.drawer_navigation_title_layout, this, false);
        addView(titleLayout);
        titleTv = titleLayout.findViewById(R.id.tv_title);
        titleIconIv0 = titleLayout.findViewById(R.id.iv_icon0);
        titleIconIv1 = titleLayout.findViewById(R.id.iv_icon1);

        RecyclerView rcv = new RecyclerView(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv.setLayoutManager(layoutManager);
        rcv.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        menuAdapter = new MenuItemAdapter(context, inflater);
        rcv.setAdapter(menuAdapter);
        addView(
                rcv,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
    }

     class MenuItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<MenuItem> list = new ArrayList<>();
        private LayoutInflater inflater;
         MenuItemAdapter(Context context ,LayoutInflater inflater){
             this.inflater = inflater;
         }
        private int getItemPosition(int viewPosition) {
            return viewPosition - (viewPosition + 1) / ITEM_VIEW_GROUP_COUNT;
        }

         @Override
         public int getItemViewType(int viewPosition) {
             if ((viewPosition + 1) % ITEM_VIEW_GROUP_COUNT == 0) {
                 return  ITEM_TYPE_DIVIDER_LINE;
             } else {
                 return ITEM_TYPE_CONTENT;
             }
         }

        public void setList(List<MenuItem> items) {
            list.addAll(items);
        }

         @NonNull
         @Override
         public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == ITEM_TYPE_DIVIDER_LINE){
                return  new MenuDividerVH(inflater.inflate(R.layout.drawer_navigation_item_divider_layout, parent, false));
            }else {
                return  new  MenuVH(inflater.inflate(R.layout.drawer_navigation_item_layout, parent, false));
            }
         }

         @Override
         public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int viewPosition) {
             if (holder instanceof MenuVH) {
                 MenuItem item = list.get(getItemPosition(viewPosition));
                 ((MenuVH) holder).tvName.setText(item.name);
                 if (item.resID != 0) {
                     ((MenuVH) holder).ivIcon.setImageResource(item.resID);
                 } else {
                     if (onLoadUrlCb != null) {
                         onLoadUrlCb.onLoad(((MenuVH) holder).ivIcon, item.url);
                     }
                 }

                 setOnClickListener(new OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         if(onMenuItemClickCb != null){
                             onMenuItemClickCb.onClick(item);
                         }
                     }
                 });
             }
         }

         @Override
         public int getItemCount() {
             return list.size() + (list.size() - 1) / (ITEM_VIEW_GROUP_COUNT - 1);
         }
     }

    class MenuVH extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;
        public MenuVH(@NonNull View itemView) {
            super(itemView);
             tvName = itemView.findViewById(R.id.tv_name);
             ivIcon = itemView.findViewById(R.id.iv_icon);
        }
    }

    class MenuDividerVH extends RecyclerView.ViewHolder{

        public MenuDividerVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class MenuItem{

        String name;
        int resID;
        String url;
        public MenuItem(String name) {
            this.name = name;
        }

        public MenuItem(String name, int resID) {
            this.name = name;
            this.resID = resID;
        }

        public MenuItem(String name, int resID, String url) {
            this.name = name;
            this.resID = resID;
            this.url = url;
        }

    }


    public interface OnLoadUrlCallback {
        void onLoad(ImageView imageView, String uri);
    }

    public interface OnMenuItemClickListener {
        void onClick(MenuItem item);
    }
}
