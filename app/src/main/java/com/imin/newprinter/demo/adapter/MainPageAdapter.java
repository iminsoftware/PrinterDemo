package com.imin.newprinter.demo.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.imin.newprinter.demo.fragment.BaseFragment;

import java.util.List;

/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */
public class MainPageAdapter extends FragmentStatePagerAdapter {
    private final List<BaseFragment> fragments;
    private final FragmentManager fragmentManager;
    // 保存已实例化的Fragment视图，Key=position, Value=Fragment的tag
    private final SparseArray<String> fragmentTags = new SparseArray<>();

    public MainPageAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragmentManager = fm;
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments==null?null:fragments.get(position);
    }

    // 核心优化点1：通过Tag复用Fragment视图
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 尝试通过Tag查找已有Fragment
        String tag = fragmentTags.get(position);
        BaseFragment fragment = null;
        if (tag != null) {
            fragment = (BaseFragment) fragmentManager.findFragmentByTag(tag);
        }

        if (fragment == null) {
            fragment = fragments.get(position);
            // 核心优化点2：使用事务添加Fragment
            fragmentManager.beginTransaction()
                    .add(container.getId(), fragment, generateTag(position))
                    .commitNowAllowingStateLoss();
            fragmentTags.put(position, generateTag(position));
        } else {
            // 复用已有Fragment的视图
            View view = fragment.getView();
            if (view != null && view.getParent() == null) {
                container.addView(view);
            }
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 核心优化点3：避免直接removeView，交由Fragment事务处理
        BaseFragment fragment = (BaseFragment) object;
        fragmentManager.beginTransaction()
                .detach(fragment)
                .commitNowAllowingStateLoss();
    }

    // 核心优化点4：生成唯一Tag标识Fragment
    private String generateTag(int position) {
        return String.valueOf(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((BaseFragment) object).getView() == view;
    }

    // 核心优化点5：清除Fragment缓存引用
    public void clearCache() {
        fragmentTags.clear();
    }
}
