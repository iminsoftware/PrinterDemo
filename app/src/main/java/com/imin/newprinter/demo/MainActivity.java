package com.imin.newprinter.demo;

import static me.goldze.mvvmhabit.utils.Utils.getContext;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.util.XUiDisplayHelper;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.ActivityMainBinding;
import com.imin.newprinter.demo.fragment.AllTestFragment;
import com.imin.newprinter.demo.fragment.BarcodeFragment;
import com.imin.newprinter.demo.fragment.BaseFragment;
import com.imin.newprinter.demo.fragment.BtConnectFragment;
import com.imin.newprinter.demo.fragment.DoubleQrCodeFragment;
import com.imin.newprinter.demo.fragment.FunctionFragment;
import com.imin.newprinter.demo.fragment.FunctionTestFragment;
import com.imin.newprinter.demo.fragment.IminBaseFragment;
import com.imin.newprinter.demo.fragment.PaperFeedFragment;
import com.imin.newprinter.demo.fragment.PictureFragment;
import com.imin.newprinter.demo.fragment.PrinterParameterFragment;
import com.imin.newprinter.demo.fragment.QrCodeFragment;
import com.imin.newprinter.demo.fragment.SettingFragment;
import com.imin.newprinter.demo.fragment.TableFormFragment;
import com.imin.newprinter.demo.fragment.TextFragment;
import com.imin.newprinter.demo.fragment.TransFragment;
import com.imin.newprinter.demo.fragment.WifiConnectFragment;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.MainViewModel;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements SwitchFragmentListener, TitleLayout.LeftCallback, IBinder.DeathRecipient {

    private static final String TAG = "PrintDemoMainActivity";
    private static final String ACTION_PRITER_STATUS_CHANGE = "com.imin.printerservice.PRITER_STATUS_CHANGE";


    private static final String ACTION_PRITER_STATUS = "status";


    private FragmentManager fragmentManager;
    private FunctionFragment functionTestFragment;

    private HashMap<Integer, IminBaseFragment> fragmentMap = new HashMap<>();
    private IminBaseFragment currentFragment;
    private BaseFragment preFragment;
    BaseFragment selectFragment;
    private BaseQuickAdapter<DialogItemDescription, BaseViewHolder> parameterLandAdapter;

    private RecyclerView rvParameter;
    private String[] contentArray;
    private List<DialogItemDescription> list;

    int requestPermissionCode = 10;
    private WifiConnectFragment wifiConnectFragment;
    private BtConnectFragment btConnectFragment;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Allow the content to extend into the status and navigation bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            // Set light status bar and navigation bar icons if supported
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }

            window.getDecorView().setSystemUiVisibility(flags);
        }

        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void registorUIChangeLiveDataCallBack() {
        super.registorUIChangeLiveDataCallBack();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestPermissionCode);
            return;
        }
        rvParameter = binding.getRoot().findViewById(R.id.rv_parameter);
        Log.d(TAG, "registorUIChangeLiveDataCallBack:===== " + PrinterHelper.getInstance().getPrinterSupplierName());
    }

    public void initData() {
        super.initData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PRITER_STATUS_CHANGE);
        registerReceiver(mReceiver, intentFilter);
        initViewData();
    }

    //初始化页面以及数据
    public void initViewData() {
        int screenWidth = XUiDisplayHelper.getScreenWidth(getContext());
        int screenHeight = XUiDisplayHelper.getScreenHeight(getContext());


        Log.d(TAG, "initData: screenWidth= " + screenWidth + ", h= " + screenHeight + "       " + PrinterHelper.getInstance().getPrinterSupplierName());
        if (screenWidth > screenHeight) {

            contentArray = getParameterArray();
            list = getParameterList(0, "");

            parameterLandAdapter = new BaseQuickAdapter<DialogItemDescription, BaseViewHolder>(R.layout.item_parameter_land) {

                @Override
                protected void convert(@NonNull BaseViewHolder holder, DialogItemDescription description) {
                    Log.d(TAG, "convert: " + description);
                    TextView content = holder.getView(R.id.tv_content);
                    TextView value = holder.getView(R.id.tv_item_value);
                    if (description != null) {
                        content.setText(description.getTitle());
                        value.setText(description.getDescription());
                    }
                }
            };

            parameterLandAdapter.setNewInstance(list);
            rvParameter.setAdapter(parameterLandAdapter);

        }

        fragmentManager = getSupportFragmentManager();

        updateFragment(-1);
        getPrinterParameter();
        binding.usbLy.setOnClickListener(view -> {
            updateFragment(-1);
            binding.usbIv.setImageResource(R.drawable.ic_check);
            binding.wifiIv.setImageResource(R.drawable.ic_uncheck);
            binding.btIv.setImageResource(R.drawable.ic_uncheck);
        });

        binding.wifiLy.setOnClickListener(view -> {
            updateFragment(10);
            binding.usbIv.setImageResource(R.drawable.ic_uncheck);
            binding.wifiIv.setImageResource(R.drawable.ic_check);
            binding.btIv.setImageResource(R.drawable.ic_uncheck);
        });

        binding.btLy.setOnClickListener(view -> {
            updateFragment(11);
            binding.usbIv.setImageResource(R.drawable.ic_uncheck);
            binding.wifiIv.setImageResource(R.drawable.ic_uncheck);
            binding.btIv.setImageResource(R.drawable.ic_check);
        });


    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();


    }

    private void updateFragment(int position) {
        Log.d(TAG, "updateFragment: num= " + position
                + ", fragment= " + (functionTestFragment != null)
        );
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (position == -1 || position == 10|| position == 11) {
            if (position == -1){
                if (Utils.isNingzLabel()) {
                    functionTestFragment = null;
                }
                if (functionTestFragment == null) {
                    functionTestFragment = new FunctionFragment();
                    functionTestFragment.setCallback(this);
                }
                preFragment = functionTestFragment;
            }else if (position == 10){
                if (wifiConnectFragment == null){
                    wifiConnectFragment = new WifiConnectFragment();
                }
                preFragment = wifiConnectFragment;
            }else {
                if (btConnectFragment == null){
                    btConnectFragment = new BtConnectFragment();
                }
                preFragment = btConnectFragment;
            }

            if (currentFragment != null){
                transaction.remove(currentFragment);
            }
            if (selectFragment != null){
                transaction.remove(selectFragment);
            }

            transaction.add(R.id.fl_main, preFragment, String.valueOf(position));
            transaction.addToBackStack(null);
            transaction.show(preFragment);
            transaction.commit();

            selectFragment = preFragment;
        } else {
            if (selectFragment != null){
                transaction.remove(selectFragment);
            }
            if (currentFragment != null){
                transaction.remove(currentFragment);
            }

            IminBaseFragment fragment = fragmentMap.get(position);
            Log.d(TAG, "updateFragment:fragment is not null " + (fragment != null));
            if (fragment == null) {

                switch (position) {
                    case 0:
                        fragment = new AllTestFragment();
                        break;
                    case 1:
                        fragment = new QrCodeFragment();
                        break;
                    case 2:
                        fragment = new BarcodeFragment();
                        break;
                    case 3:
                        fragment = new TextFragment();
                        break;
                    case 4:
                        fragment = new TableFormFragment();
                        break;
                    case 5:
                        fragment = new PictureFragment();
                        break;
                    case 6:
                        fragment = new TransFragment();
                        break;
                    case 7:
                        fragment = new PaperFeedFragment();
                        break;
                    case 8:
                        fragment = new PrinterParameterFragment();
                        break;
                    case 9:
                        fragment = new DoubleQrCodeFragment();
                        break;
                    case 100: // setting
                        fragment = new SettingFragment();
                        break;
                }

                if (fragment != null) {
                    fragment.setLeftCallback(this);
                    fragmentMap.put(position, fragment);
                }
            }


            transaction.replace(R.id.fl_main, fragment, String.valueOf(position));
            transaction.addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();

            currentFragment = fragment;

        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + fragmentManager.getBackStackEntryCount());
        if (fragmentManager.getBackStackEntryCount() <= 0) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }

        updateStatus(PrinterHelper.getInstance().getPrinterStatus());
    }

    @Override
    public void switchFragment(int num) {
        Log.d(TAG, "switchPager: " + num);

        updateFragment(num);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(PrinterHelper.getInstance().getPrinterStatus());
    }

    @Override
    public void backPre() {
        Log.d(TAG, "backPre: ");
        onBackPressed();
    }

    @Override
    public void nextPage(int num) {
        Log.d(TAG, "nextPage: " + num);
        updateFragment(num);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.e(TAG, "main onDestroy: ");
    }

    @Override
    public void binderDied() {
        Log.e(TAG, "binderDied: ");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionCode) {

        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(ACTION_PRITER_STATUS, -1);

            Log.d(TAG, "action= " + intent.getAction()
                    + ", status= " + status
            );

            updateStatus(status);
        }
    };

    public void updateStatus(int value) {
        boolean isNormal = (value == Utils.PRINTER_NORMAL);
        Log.d(TAG, "updateStatus: " + value);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String statusTip = Utils.getPrinterStatusTip(getContext(), value);
                Drawable drawable = getResources().getDrawable(isNormal ? R.drawable.bg_printer_normal : R.drawable.bg_printer_exception);
                if (binding.rlPrintStatus != null) {

                    binding.rlPrintStatus.setBackground(drawable);
                }
                if (binding.tvPrinterStatus != null) {

                    binding.tvPrinterStatus.setText(statusTip);
                }


                if (functionTestFragment != null) {
                    functionTestFragment.updatePrinterStatus(value);
                }
            }
        });
    }


    private String[] getParameterArray() {

        String[] array = this.getResources().getStringArray(Utils.isNingzLabel() ? R.array.printer_parameter_list_ds2_label : R.array.printer_parameter_list);
        return array;
    }

    private List<DialogItemDescription> getParameterList(int checkIndex, String value) {
        String[] array = contentArray;
        list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            DialogItemDescription item = new DialogItemDescription(array[i]);
            if (checkIndex == i && !TextUtils.isEmpty(value)) {
                item.setDescription(value);
            }
            list.add(item);
        }

        return list;
    }

    public void getPrinterParameter() {
        if (Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) {
            PrinterHelper.getInstance().getPrinterSerialNumber(new INeoPrinterCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {

                }

                @Override
                public void onReturnString(String result) throws RemoteException {

                    Log.d(TAG, "getPrinterSerialNumber: " + result);
                    updateParameterList(0, result);

                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {

                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {

                }
            });
        }

//        if (!Utils.isNingzLabel()){
        PrinterHelper.getInstance().getPrinterModelName(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterModelName: " + result);
                updateParameterList(Utils.isNingzLabel() ? 0 : 1, result);
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });
//        }

        if (Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) {
            PrinterHelper.getInstance().getPrinterThermalHead(new INeoPrinterCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {

                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    Log.d(TAG, "getPrinterThermalHead: " + result);
                    updateParameterList(2, result);

                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {

                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {

                }
            });
        }


        PrinterHelper.getInstance().getPrinterFirmwareVersion(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterFirmwareVersion: " + result);
                updateParameterList((Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) ? 3 : 1, result);

            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        String serviceVersion = PrinterHelper.getInstance().getServiceVersion();
        updateParameterList((Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) ? 4 : 2, serviceVersion);

        if (!Utils.isNingzLabel()) {
            String paperType = PrinterHelper.getInstance().getPrinterPaperType() + "";
            updateParameterList(5, paperType);
            Log.d(TAG, "initViewObservable version: " + serviceVersion + ", type= " + paperType);
        }

        if (Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) {
            PrinterHelper.getInstance().getPrinterPaperDistance(new INeoPrinterCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {

                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    Log.d(TAG, "getPrinterPaperDistance: " + result);
                    updateParameterList(6, result);
                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {

                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {

                }
            });
        }

    }

    private void updateParameterList(int checkIndex, String value) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                DialogItemDescription item = list.get(i);
                if (checkIndex == i && !TextUtils.isEmpty(value)) {
                    item.setDescription(value);
                }
            }
            parameterLandAdapter.setNewData(list);
        }

    }

    /**
     * 拦截点击事件，当输入法弹出时，点击非输入框位置就隐藏输入法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                boolean res = hideKeyboard(this, v.getWindowToken());
                if (res) {
//                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean hideKeyboard(Context context, IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {

                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void updateRv() {
        if (parameterLandAdapter != null && rvParameter != null) {
            parameterLandAdapter.setNewInstance(list);
            rvParameter.setAdapter(parameterLandAdapter);
        }

    }
}