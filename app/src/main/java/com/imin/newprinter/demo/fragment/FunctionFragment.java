package com.imin.newprinter.demo.fragment;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.IminApplication;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.bean.LabelTitleBean;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentFunctionBinding;
import com.imin.newprinter.demo.databinding.FragmentFunctionTestBinding;
import com.imin.newprinter.demo.utils.LabelTemplateUtils;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.view.FlowLayout;
import com.imin.newprinter.demo.view.SpacesItemDecoration;
import com.imin.newprinter.demo.view.TagAdapter;
import com.imin.newprinter.demo.view.TagFlowLayout;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FunctionTestFragmentViewModel;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;

public class FunctionFragment extends BaseFragment {

    private static final String TAG = "FunctionFragment";

    private BaseQuickAdapter<FunctionTestBean, BaseViewHolder> adapter;
    BaseQuickAdapter<LabelTitleBean, BaseViewHolder> adapter1,adapter2;
    private ArrayList<FunctionTestBean> list;

    private List<LabelTitleBean> labelTypeList;
    private String[] stringArray;
    private StaggeredGridLayoutManager layoutManager;
    private List<LabelTitleBean> titleBeans;

    private int selectTabPositon = 0;
    private com.imin.newprinter.demo.databinding.FragmentFunctionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFunctionBinding.inflate(inflater);
        initView();
        initData();
        return binding.getRoot();
    }



    private void initView() {
        binding.viewTitle.setTitle(Utils.isNingzLabel()?"":getString(R.string.function_test));
        binding.llyLabel.setVisibility(Utils.isNingzLabel()? View.VISIBLE:View.GONE);
        binding.recyclerView.setVisibility(Utils.isNingzLabel()?View.GONE:View.VISIBLE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        }else {
            layoutManager = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);

        }
        binding.ryV.setLayoutManager(layoutManager);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);//避免出现空隙
        binding.recyclerView.setHasFixedSize(true);
        // 优化滚动状态监听
        binding.ryV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments();
            }
        });
        binding.ryV.addItemDecoration(new SpacesItemDecoration(4,10));
    }


    LabelTitleBean selePosition = null;

    public void initData() {

        list = new ArrayList<>();
        list.add(new FunctionTestBean(getString(R.string.function_all), R.mipmap.ic_all));
        list.add(new FunctionTestBean(getString(R.string.function_qrcode), R.drawable.ic_qrcode));
        list.add(new FunctionTestBean(getString(R.string.function_barcode), R.drawable.ic_barcode));
        list.add(new FunctionTestBean(getString(R.string.function_text_pic), R.drawable.ic_text_pic));
        list.add(new FunctionTestBean(getString(R.string.function_tab), R.drawable.ic_table));
        list.add(new FunctionTestBean(getString(R.string.function_pic), R.drawable.ic_pic));
        list.add(new FunctionTestBean(getString(R.string.function_buffer), R.drawable.ic_buffer));
        list.add(new FunctionTestBean(getString(R.string.paper_feed), R.drawable.ic_line));

        adapter = new BaseQuickAdapter<FunctionTestBean, BaseViewHolder>(R.layout.item_funtion_test) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, FunctionTestBean functionTestBean) {
                ImageView ivImage = viewHolder.getView(R.id.iv_draw);
                TextView rvTitle = viewHolder.getView(R.id.tv_title);
                if (functionTestBean != null) {
                    rvTitle.setText(functionTestBean.getTitle());
                    ivImage.setImageResource(functionTestBean.getImageResource());
                }
            }

        };

        adapter.setNewData(list);
        binding.recyclerView.setAdapter(adapter);

        if (Utils.isNingzLabel()){
            initLabelPrintTest();
        }

        updatePrinterStatus(PrinterHelper.getInstance().getPrinterStatus());
        Log.d(TAG, "initData: ");

        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (Utils.isNingzLabel())return;

            FunctionTestBean bean = list.get(position);
            Log.d(TAG, "initViewObservable: " + bean.toString()
                    + ", " + bean.getFragment()
                    + ", position" + position
            );
            switchFragment(position);

        });



        binding.viewTitle.setRightCallback(v -> {
            Log.d(TAG, "setting: ");
            switchFragment(100);
        });
        Log.d(TAG, "initViewObservable: ");


    }

    private void initLabelPrintTest() {

        labelTypeList = new ArrayList<>();
        stringArray = getResources().getStringArray(R.array.label_type);
        for (int i = 0; i< stringArray.length; i++){
            LabelTitleBean titleBean = new LabelTitleBean();
            titleBean.setId(i);
            titleBean.setTitle(stringArray[i]);
            labelTypeList.add(titleBean);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            TagAdapter<LabelTitleBean> tagAdapter = new TagAdapter<LabelTitleBean>(labelTypeList) {
                @Override
                public View getView(FlowLayout parent, int position, LabelTitleBean s) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.item_label_title,
                            binding.tabLayout, false);
                    FrameLayout ivImage = view.findViewById(R.id.itemFy);
                    TextView rvTitle = view.findViewById(R.id.itemText);
                    rvTitle.setText(s.getTitle());
                    rvTitle.setTextColor(IminApplication.mContext.getColor(R.color.color_1D1D1F));
                    ivImage.setBackground(getContext().getDrawable(R.drawable.shape_rv_bg_corner_gray));
                    if (selePosition != null && s != null){
                        if (selePosition.getId() == s.getId()){
                            ivImage.setBackground(getContext().getResources().getDrawable(R.drawable.shape_rv_bg_corner_red));
                            rvTitle.setTextColor(getContext().getColor(R.color.white));
                        }
                    }else {
                        if (s != null && s.getId() == 0){
                            ivImage.setBackground(IminApplication.mContext.getResources().getDrawable(R.drawable.shape_rv_bg_corner_red));
                            rvTitle.setTextColor(IminApplication.mContext.getColor(R.color.white));
                        }
                    }
                    return view;
                }
            };

            binding.tabLayout.setAdapter(tagAdapter);

            binding.tabLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {

                    LabelTitleBean titleBean = labelTypeList.get(position);
                    selePosition = titleBean;
                    tagAdapter.notifyDataChanged();
//                    adapter2.setList(null);
//                    adapter2.setList(labelClick(position));
//                    ryV.removeAllViews();
//                    ryV.requestLayout();
                    setLabelRvAdapter(labelClick(titleBean.getId()));
                    // 重新设置LayoutManager（如果必要）
                    Log.d(TAG, "flowLayout  onItemClick:==> "+position+"        ");

                    return true;
                }
            });
        }else {
            adapter1 = new BaseQuickAdapter<LabelTitleBean, BaseViewHolder>(R.layout.item_label_title) {
                @Override
                protected void convert(@NonNull BaseViewHolder viewHolder, LabelTitleBean s) {
                    FrameLayout ivImage = viewHolder.getView(R.id.itemFy);
                    TextView rvTitle = viewHolder.getView(R.id.itemText);
                    rvTitle.setText(s.getTitle());
                    rvTitle.setTextColor(IminApplication.mContext.getColor(R.color.color_1D1D1F));
                    ivImage.setBackground(IminApplication.mContext.getResources().getDrawable(R.drawable.shape_rv_bg_corner_gray));
                    if (selePosition != null && s != null){
                        if (selePosition.getId() == s.getId()){
                            ivImage.setBackground(IminApplication.mContext.getResources().getDrawable(R.drawable.shape_rv_bg_corner_red));
                            rvTitle.setTextColor(IminApplication.mContext.getColor(R.color.white));
                        }
                    }else {
                        if (s != null && s.getId() == 0){
                            ivImage.setBackground(IminApplication.mContext.getResources().getDrawable(R.drawable.shape_rv_bg_corner_red));
                            rvTitle.setTextColor(IminApplication.mContext.getColor(R.color.white));
                        }
                    }
                }
            };


            adapter1.setNewData(labelTypeList);
            binding.ryH.setAdapter(adapter1);

            adapter1.setOnItemClickListener((adapter, view, position) -> {
                LabelTitleBean titleBean = labelTypeList.get(position);
                selePosition = titleBean;
                adapter1.notifyDataSetChanged();
                setLabelRvAdapter(labelClick(position));
                Log.d(TAG, "onItemClick: "+position);
            });


        }

        adapter2 = new BaseQuickAdapter<LabelTitleBean, BaseViewHolder>(R.layout.item_label_rv) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, LabelTitleBean s) {
                ImageView itemIv = viewHolder.getView(R.id.itemIv);
                TextView itemText = viewHolder.getView(R.id.itemText);

                if (s != null){
                    itemIv.setImageBitmap(s.getiMage());
                    itemText.setText(s.getTitle());
                }
            }
        };

        adapter2.setOnItemClickListener((adapter, view, position) -> {
            Log.d(TAG, "adapter2  onItemClick11: " + position + " , labelBitmapList.size()==> " + "  ,,titleBeans.size=>   "+titleBeans.size()+" " +
                    "    "+selectTabPositon+"    "+binding.ryV.getChildCount());
            if (adapter2.getData() != null){
                LabelTitleBean titleBean = adapter2.getData().get(position);
                if (titleBean != null){
                    Log.d(TAG, "adapter2  onItemClick000: "+position+" , labelBitmapList.size()==> "+"  ,,   "+titleBean.getHeight());
                    PrinterHelper.getInstance().labelPrintBitmap(titleBean.getiMage(),titleBean.getWidth(),titleBean.getHeight(),null);
                }
            }
            Log.d(TAG, "adapter2  onItemClick: " + position + " , labelBitmapList.size()==> " + "  ,,titleBeans.size=>   "+titleBeans.size()+" " +
                        "    "+selectTabPositon);
        });


        binding.ryV.setAdapter(adapter2);


        setLabelRvAdapter(labelClick(0));


    }

    private List<LabelTitleBean> labelClick(int position){
        selectTabPositon = position;
        titleBeans = new ArrayList<>();
        Bitmap bitmap1 = LabelTemplateUtils.printLabelSize40x30_CN1(false);
        Bitmap bitmap2 = LabelTemplateUtils.printLabelSize40x30_CN2(false);
        Bitmap bitmap3 = LabelTemplateUtils.printLabelSize40x30_CN3(false);
        Bitmap bitmap4 = LabelTemplateUtils.printLabelSize40x30_CN4(false);
        Bitmap bitmap5 = LabelTemplateUtils.printLabelSize40x50_CN1(false);
        Bitmap bitmap6 = LabelTemplateUtils.printLabelSize40x60_CN1(false);

        Bitmap bitmap7 = LabelTemplateUtils.printLabelSize50x30_CN1();
        Bitmap bitmap8 = LabelTemplateUtils.printLabelSize50x30_CN2();
        Bitmap bitmap9 = LabelTemplateUtils.printLabelSize50x30_CN3();

        if (position == 0){
            titleBeans.add(new LabelTitleBean(0,stringArray[1],40,30,bitmap1));
            titleBeans.add(new LabelTitleBean(1,stringArray[1],40,30,bitmap2));
            titleBeans.add(new LabelTitleBean(2,stringArray[1],40,30,bitmap3));
            titleBeans.add(new LabelTitleBean(3,stringArray[1],40,30,bitmap4));
            titleBeans.add(new LabelTitleBean(4,stringArray[2],40,50,bitmap5));
            titleBeans.add(new LabelTitleBean(5,stringArray[3],40,60,bitmap6));
            titleBeans.add(new LabelTitleBean(6,stringArray[4],50,30,bitmap7));
            titleBeans.add(new LabelTitleBean(7,stringArray[4],50,30,bitmap8));
            titleBeans.add(new LabelTitleBean(8,stringArray[4],50,30,bitmap9));

        }else if (position == 1){
            titleBeans.add(new LabelTitleBean(0,stringArray[1],40,30,bitmap1));
            titleBeans.add(new LabelTitleBean(1,stringArray[1],40,30,bitmap2));
            titleBeans.add(new LabelTitleBean(2,stringArray[1],40,30,bitmap3));
            titleBeans.add(new LabelTitleBean(3,stringArray[1],40,30,bitmap4));
        }else if (position == 2){
            titleBeans.add(new LabelTitleBean(0,stringArray[2],40,50,bitmap5));
        }else if (position == 3){
            titleBeans.add(new LabelTitleBean(0,stringArray[3],40,60,bitmap6));
        } else if (position == 4) {
            titleBeans.add(new LabelTitleBean(0,stringArray[4],50,30,bitmap7));
            titleBeans.add(new LabelTitleBean(1,stringArray[4],50,30,bitmap8));
            titleBeans.add(new LabelTitleBean(2,stringArray[4],50,30,bitmap9));
        }

        Log.d(TAG, "labelClick  onItemClick: " + position+ "  ,,titleBeans.size=>   "+titleBeans.size());
        return titleBeans;
    }

    private void setLabelRvAdapter(List<LabelTitleBean> list){
        adapter2.getData().clear();
        adapter2.setList(list);

    }

    private SwitchFragmentListener fragmentListener;

    public void setCallback(SwitchFragmentListener listener) {

        fragmentListener = listener;
        Log.d(TAG, "setCallback: " + (fragmentListener != null));
    }


    public void switchFragment(int num) {
        Log.d(TAG, "switchPager :num=  " + num + (fragmentListener != null));

        if (fragmentListener != null) {
            fragmentListener.switchFragment(num);
        }
    }

    public void updatePrinterStatus(int status) {
        boolean isNormal = (status == Utils.PRINTER_NORMAL);
        String printerStatusTip = Utils.getPrinterStatusTip(getContext(), status);
        Log.d(TAG, "updateStatus: " + status + ", isNormal= " + isNormal);

        Drawable drawable = IminApplication.mContext.getResources().getDrawable(isNormal ? R.drawable.bg_printer_normal : R.drawable.bg_printer_exception);
        if (binding.rlPrintStatus != null) {
            binding.rlPrintStatus.setBackground(drawable);
        }

        if (binding.tvPrinterStatus != null) {
            binding.tvPrinterStatus.setText(printerStatusTip);
        }
    }
}
