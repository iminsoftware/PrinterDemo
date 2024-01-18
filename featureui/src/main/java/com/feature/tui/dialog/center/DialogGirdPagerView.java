package com.feature.tui.dialog.center;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.feature.tui.R;
import com.feature.tui.util.XUiDisplayHelper;

import java.util.ArrayList;

public class DialogGirdPagerView
        extends LinearLayout {
    private LinearLayout slidingPointView;
    private int currentSelect;
    private ArrayList<RecyclerView> list;
    private ViewPager viewPager = new ViewPager(getContext());
    private boolean showSliding = true;

    private PagerAdapter adapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (list == null)
                return 0;
            return list.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            RecyclerView view = list.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }
    };

    public DialogGirdPagerView(Context context, boolean showSliding) {
        this(context, null, 0, showSliding);
    }

    public DialogGirdPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, true);
    }

    public DialogGirdPagerView(Context context, AttributeSet attrs, int defStyleAttr, boolean showSliding) {
        super(context, attrs, defStyleAttr);
        this.showSliding = showSliding;
        setOrientation(VERTICAL);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_194)
        );
        addView(viewPager, layoutParams);
        viewPager.setAdapter(adapter);
    }

    public void setListData(ArrayList<RecyclerView> list) {
        this.list = list;
        viewPager.setOffscreenPageLimit(list.size());
        if (showSliding) {
            setSlidCount(list.size());
        }
        adapter.notifyDataSetChanged();
    }

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    private void bindSliding(ViewPager pager) {
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            public void onPageSelected(int position) {
                moveToPoint(position);
            }

        });
    }

    private void moveToPoint(int index) {
        if (currentSelect == index) {
            return;
        }
        slidingPointView.getChildAt(currentSelect).setSelected(false);
        slidingPointView.getChildAt(index).setSelected(true);
        currentSelect = index;
    }

    private void setSlidCount(int num) {
        int temp = num;
        if (temp < 1) {
            temp = 0;
        }
        addPointLayout();
        addPoints(temp);
    }

    private void addPointLayout() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(getContext(), R.dimen.dp_10);
        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(getContext(), R.dimen.dp_10);
        LinearLayout linearLayout = slidingPointView = new LinearLayout(getContext());
        if (linearLayout != null) {
            linearLayout.setOrientation(HORIZONTAL);
        }
        addView(slidingPointView, layoutParams);
        bindSliding(viewPager);
    }

    private void addPoints(int num) {
        for (int i = 0; i < num; i++) {
            slidingPointView.addView(createPointView());
        }
        slidingPointView.getChildAt(0).setSelected(true);
    }

    private View createPointView() {
        View view = new View(getContext());
        view.setBackgroundResource(R.drawable.dialog_sliding_point);
        LayoutParams layoutParams = new LayoutParams(XUiDisplayHelper.getDimensionPixelToId(this.getContext(), R.dimen.dp_8), XUiDisplayHelper.getDimensionPixelToId(this.getContext(), R.dimen.dp_8));
        layoutParams.setMarginStart(XUiDisplayHelper.getDimensionPixelToId(getContext(), R.dimen.dp_4));
        layoutParams.setMarginEnd(XUiDisplayHelper.getDimensionPixelToId(getContext(), R.dimen.dp_4));
        view.setLayoutParams(layoutParams);
        return view;
    }

}
