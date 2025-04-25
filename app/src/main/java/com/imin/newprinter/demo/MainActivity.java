package com.imin.newprinter.demo;

import static android.net.NetworkCapabilities.TRANSPORT_WIFI;
import static me.goldze.mvvmhabit.utils.Utils.getContext;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.imin.newprinter.demo.adapter.MainPageAdapter;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.ActivityMainBinding;
import com.imin.newprinter.demo.fragment.AllFragment;
import com.imin.newprinter.demo.fragment.AllTestFragment;
import com.imin.newprinter.demo.fragment.BarcodeFragment;
import com.imin.newprinter.demo.fragment.BaseFragment;
import com.imin.newprinter.demo.fragment.BtConnectFragment;
import com.imin.newprinter.demo.fragment.DoubleQrCodeFragment;
import com.imin.newprinter.demo.fragment.FunctionFragment;
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
import com.imin.newprinter.demo.fragment.WirelessPrintingFragment;
import com.imin.newprinter.demo.utils.NetworkUtils;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.utils.WifiScannerSingleton;
import com.imin.newprinter.demo.view.OnSingleClickListener;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.IWirelessPrintResult;
import com.imin.printer.PrinterHelper;
import com.imin.printer.enums.ConnectType;
import com.imin.printer.enums.WirelessConfig;
import com.imin.printer.wireless.WirelessPrintStyle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwitchFragmentListener, TitleLayout.LeftCallback, IBinder.DeathRecipient {

    private static final String TAG = "PrintDemo_MainActivity";
    private static final String ACTION_PRITER_STATUS_CHANGE = "com.imin.printerservice.PRITER_STATUS_CHANGE";
    private static final String ACTION_PRITER_STATUS_CHANGE1 = "com.imin.printerservice.PRITER_CONNECT_STATUS_CHANGE";

    private static final String ACTION_PRITER_STATUS = "status";


//    private FunctionFragment functionTestFragment;

    private LinkedHashMap<Integer, IminBaseFragment> fragmentMap = new LinkedHashMap<>();
    private IminBaseFragment currentFragment;
    private BaseFragment preFragment;
    BaseFragment selectFragment;

    //    private RecyclerView rvParameter;
    private String[] contentArray;

    public static int requestPermissionCode = 10;
    private WifiConnectFragment wifiConnectFragment;
    private BtConnectFragment btConnectFragment;
    private com.imin.newprinter.demo.databinding.ActivityMainBinding binding;
    //    private WifiScannerSingleton wifiScanner;
    public static String connectType = "", connectContent = "", connectAddress = "", ipConnect = "", btContent = "";
    private WirelessPrintingFragment wirelessPrintingFragment;
    private AllFragment allFragment;
    private int selectCurrentItem = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
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


        setContentView(binding.getRoot());

        initView();
        initData();
    }

    private void initView() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.BLUETOOTH_SCAN
                    , Manifest.permission.BLUETOOTH
                    , Manifest.permission.BLUETOOTH_CONNECT}, requestPermissionCode);

        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PRITER_STATUS_CHANGE);
        intentFilter.addAction(ACTION_PRITER_STATUS_CHANGE1);
        registerReceiver(mReceiver, intentFilter);
        getPrinterParameter();
//        functionTestFragment = new FunctionFragment();
//        functionTestFragment.setCallback(this);
        allFragment = new AllFragment();
        allFragment.setUserVisibleHint(false);
        wifiConnectFragment = new WifiConnectFragment();
        wifiConnectFragment.setCallback(this);
        wifiConnectFragment.setUserVisibleHint(false);
        btConnectFragment = new BtConnectFragment();
        btConnectFragment.setCallback(this);
        btConnectFragment.setUserVisibleHint(false);
        wirelessPrintingFragment = new WirelessPrintingFragment();
        wirelessPrintingFragment.setCallback(this);
        wirelessPrintingFragment.setUserVisibleHint(false);
        List<BaseFragment> fragmentList = new ArrayList<>();

        fragmentList.add(allFragment);
        fragmentList.add(wifiConnectFragment);
        fragmentList.add(btConnectFragment);
        fragmentList.add(wirelessPrintingFragment);
        MainPageAdapter mainPageAdapter = new MainPageAdapter(getSupportFragmentManager(), fragmentList);
        binding.vp.setAdapter(mainPageAdapter);


        binding.vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                switch (position) {
                    case 0:
//                        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
//                                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
//                                .setConfig(ConnectType.USB.getTypeName()), new IWirelessPrintResult.Stub() {
//                            @Override
//                            public void onResult(int i, String s) throws RemoteException {
//
//                            }
//
//                            @Override
//                            public void onReturnString(String s) throws RemoteException {
//
//                            }
//                        });
                        break;
                    case 1:

                        wifiConnectFragment.updateUi();
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.vp.setOffscreenPageLimit(4);
        binding.vp.setCurrentItem(selectCurrentItem);
        binding.usbLy.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                selectCurrentItem = 0;
                binding.vp.setCurrentItem(selectCurrentItem);
                binding.usbIv.setImageResource(R.drawable.ic_check);
                binding.wifiIv.setImageResource(R.drawable.ic_uncheck);
                binding.btIv.setImageResource(R.drawable.ic_uncheck);


                PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                        .setConfig(ConnectType.USB.getTypeName()), new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {

                    }
                });

            }
        });

        binding.wifiLy.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.d(TAG, "isWifiConnected  = " + NetworkUtils.isWifiConnected(v.getContext()));
                if (btConnectFragment != null){
                    btConnectFragment.cancelSearchBlueTooth();
                }
//                if (i == 0) {
//                    ipConnect = s;
//                    connectAddress = s;
//                    connectType = "WIFI";
//                    connectContent = s;
//                    selectCurrentItem = 4;
//                    wirelessPrintingFragment.updateStatus();
//
//                } else {
//                    ipConnect = "";
//                    connectAddress = "";
//                    connectType = "WIFI";
//                    connectContent = "";
//                    selectCurrentItem = 1;
//
//                }
//                binding.vp.setCurrentItem(selectCurrentItem);
//
//                binding.usbIv.setImageResource(R.drawable.ic_uncheck);
//                binding.wifiIv.setImageResource(R.drawable.ic_check);
//                binding.btIv.setImageResource(R.drawable.ic_uncheck);
//                if (!NetworkUtils.isWifiConnected(v.getContext())) {
//                    PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
//                            .setWirelessStyle(WirelessConfig.DISCONNECT_WIFI), new IWirelessPrintResult.Stub() {
//                        @Override
//                        public void onResult(int i, String s) throws RemoteException {
//                            Log.d(TAG, "DISCONNECT_WIFI==:  " + s + "  , i= " + i);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    MainActivity.ipConnect = "";
//                                    selectCurrentItem = 1;
//                                    binding.vp.setCurrentItem(selectCurrentItem);
//                                    binding.usbIv.setImageResource(R.drawable.ic_uncheck);
//                                    binding.wifiIv.setImageResource(R.drawable.ic_check);
//                                    binding.btIv.setImageResource(R.drawable.ic_uncheck);
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onReturnString(String s) throws RemoteException {
//
//                        }
//                    });
//                } else {

                    PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                            .setWirelessStyle(WirelessConfig.CURRENT_CONNECT_WIFI_IP), new IWirelessPrintResult.Stub() {
                        @Override
                        public void onResult(int i, String s) throws RemoteException {
                            Log.d(TAG, "CURRENT_CONNECT_WIFI_IP  = " + i + "    " + s);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (i == 0) {
                                        ipConnect = s;
                                        connectAddress = s;
                                        connectType = "WIFI";
                                        connectContent = s;
                                        selectCurrentItem = 4;
                                        wirelessPrintingFragment.updateStatus();

                                    } else {
                                        ipConnect = "";
                                        connectAddress = "";
                                        connectType = "WIFI";
                                        connectContent = "";
                                        selectCurrentItem = 1;

                                    }
                                    binding.vp.setCurrentItem(selectCurrentItem);

                                    binding.usbIv.setImageResource(R.drawable.ic_uncheck);
                                    binding.wifiIv.setImageResource(R.drawable.ic_check);
                                    binding.btIv.setImageResource(R.drawable.ic_uncheck);
                                }
                            });


                        }

                        @Override
                        public void onReturnString(String s) throws RemoteException {

                        }
                    });
//                }

            }
        });

        binding.btLy.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.CURRENT_CONNECT_BT_MAC), new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "CURRENT_CONNECT_BT_MAC  = " + i + "    " + s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 0) {
                                    btContent = s;
                                    connectAddress = s;
                                    connectType = "BT";
                                    selectCurrentItem = 4;
                                    wirelessPrintingFragment.updateStatus();

                                } else {
                                    btContent = "";
                                    connectAddress = "";
                                    connectType = "BT";
                                    selectCurrentItem = 2;

                                }
                                binding.vp.setCurrentItem(selectCurrentItem);
                                binding.usbIv.setImageResource(R.drawable.ic_uncheck);
                                binding.wifiIv.setImageResource(R.drawable.ic_uncheck);
                                binding.btIv.setImageResource(R.drawable.ic_check);
                            }
                        });

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {

                    }
                });


            }
        });


    }


    public void disConnectWirelessPrint() {
        if (wirelessPrintingFragment != null) {
            wirelessPrintingFragment.disConnect();
        }
    }

    public void initData() {


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            finish();
        } else {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
            FragmentManager.BackStackEntry topEntry = getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1);
            String currentTag = topEntry.getName();
            Log.d(TAG, "onBackPressed  currentTag= " + currentTag);
            getSupportFragmentManager().popBackStack();
        }

        updateStatus(PrinterHelper.getInstance().getPrinterStatus());
    }


    @Override
    public void switchFragment(int num) {
        Log.d(TAG, "switchPager: " + num);
//        if (num>=100){
//            if (Utils.isPortrait()){
//                binding.viewTitle.setVisibility(View.GONE);
//                binding.clConnect.setVisibility(View.GONE);
//                binding.rlPrintStatus.setVisibility(View.GONE);
//            }
//            functionTestFragment.updateFragment(num-100);
//        }else {
//            if (Utils.isPortrait()) {
//                if (binding.viewTitle != null) {
//                    binding.viewTitle.setVisibility(View.VISIBLE);
//                }
//
//                binding.clConnect.setVisibility(View.VISIBLE);
//                binding.rlPrintStatus.setVisibility(View.VISIBLE);
//            }
        selectCurrentItem = num;
        binding.vp.setCurrentItem(selectCurrentItem);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(PrinterHelper.getInstance().getPrinterStatus());
        binding.vp.setCurrentItem(selectCurrentItem);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        wifiScanner.stopWifiScan();
    }

    @Override
    public void backPre() {
        Log.d(TAG, "backPre: ");
        onBackPressed();
    }

    @Override
    public void nextPage(int num) {
        Log.d(TAG, "nextPage: " + num);
//        updateFragment(num);
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
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startWifiScan();
                Log.d(TAG, "startWifiScan: ");
            } else {
                Toast.makeText(this, "Location permission required for WiFi scanning",
                        Toast.LENGTH_LONG).show();
            }
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

                if (allFragment != null) {
                    allFragment.updatePrinterStatus(value);
                }
                if (wifiConnectFragment != null) {
                    wifiConnectFragment.updatePrinterStatus(value);
                }
//if (functionTestFragment != null) {
//                    functionTestFragment.updatePrinterStatus(value);
//                }

            }
        });
    }


    private String[] getParameterArray() {

        String[] array = this.getResources().getStringArray(Utils.isNingzLabel() ? R.array.printer_parameter_list_ds2_label : R.array.printer_parameter_list);
        return array;
    }


    public void getPrinterParameter() {

        Log.d(TAG, "getPrinterParameter1111111:===== " + (Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()));

        if (Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) {
//            binding.lySerial.setVisibility(View.VISIBLE);
            binding.lyThermal.setVisibility(View.VISIBLE);
//            binding.lyDistancee.setVisibility(View.VISIBLE);
            Log.d(TAG, "getPrinterParameter2222:===== ");
//            PrinterHelper.getInstance().getPrinterSerialNumber(new INeoPrinterCallback() {
//                @Override
//                public void onRunResult(boolean isSuccess) throws RemoteException {
//
//                }
//
//                @Override
//                public void onReturnString(String result) throws RemoteException {
//
//                    Log.d(TAG, "getPrinterSerialNumber: " + result);
////                    updateParameterList(0, result);
//                    binding.tvSerialNumber.setText(result);
//
//
//
//                }
//
//                @Override
//                public void onRaiseException(int code, String msg) throws RemoteException {
//
//                }
//
//                @Override
//                public void onPrintResult(int code, String msg) throws RemoteException {
//
//                }
//            });


            PrinterHelper.getInstance().getPrinterThermalHead(new INeoPrinterCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {

                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    Log.d(TAG, "getPrinterThermalHead: " + result);
//                    updateParameterList(2, result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvThermalHead.setText(result);
                        }
                    });


                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {

                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {

                }
            });

//            PrinterHelper.getInstance().getPrinterPaperDistance(new INeoPrinterCallback() {
//                @Override
//                public void onRunResult(boolean isSuccess) throws RemoteException {
//
//                }
//
//                @Override
//                public void onReturnString(String result) throws RemoteException {
//                    Log.d(TAG, "getPrinterPaperDistance: " + result);
////                    updateParameterList(6, result);
//                    binding.tvDistance.setText(result);
//                }
//
//                @Override
//                public void onRaiseException(int code, String msg) throws RemoteException {
//
//                }
//
//                @Override
//                public void onPrintResult(int code, String msg) throws RemoteException {
//
//                }
//            });
        } else {
            binding.lySerial.setVisibility(View.GONE);
            binding.lyThermal.setVisibility(View.GONE);
            binding.lyDistancee.setVisibility(View.GONE);
        }


        Log.d(TAG, "getPrinterParameter1111111:===== ");
        PrinterHelper.getInstance().getPrinterModelName(new INeoPrinterCallback() {
            @Override

            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterModelName: " + result);
//                updateParameterList(Utils.isNingzLabel() ? 0 : 1, result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvModelName.setText(result);
                    }
                });


            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().getPrinterFirmwareVersion(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterFirmwareVersion: " + result);
//                updateParameterList((Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) ? 3 : 1, result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvFirmwareVersion.setText(result);
                    }
                });


            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });


        String serviceVersion = PrinterHelper.getInstance().getServiceVersion();
        binding.tvServiceVersion.setText(serviceVersion);
//        updateParameterList((Utils.getPrinterType() != Utils.PrinterFirmwareBy.JIMMY && !Utils.isNingzLabel()) ? 4 : 2, serviceVersion);

        if (!Utils.isNingzLabel()) {
            binding.lyHeadType.setVisibility(View.VISIBLE);
            String paperType = PrinterHelper.getInstance().getPrinterPaperType() + "";

            binding.tvThermalHeadType.setText(paperType);
            Log.d(TAG, "initViewObservable version: " + serviceVersion + ", type= " + paperType);
        } else {
            binding.lyHeadType.setVisibility(View.GONE);
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


    ArrayList<String> wifiList = new ArrayList<>();


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "initViewObservable version: " + newConfig);
    }
}