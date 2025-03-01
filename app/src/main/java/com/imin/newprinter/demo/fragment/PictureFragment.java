package com.imin.newprinter.demo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.feature.tui.dialog.builder.SeekbarDialogBuilder;
import com.feature.tui.dialog.builder.SingleChoiceDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.widget.buttonview.ButtonView;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CommonTestAdapter;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentPictureTestBinding;
import com.imin.newprinter.demo.utils.BitmapUtils;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class PictureFragment extends BaseListFragment<FragmentPictureTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "PictureFragment";
    private String[] contentList;
    private String[] values;

    private int mAlignment = 0;
    private int count = 1;
    private int printCount = 1;
    private int intervalTime = 0;


    private List<DialogItemDescription> qrAlignmentList;
    private Bitmap bitmap;
    private ImageView examplePic;
    private ButtonView print;
    private static final int PRINTER_BITMAP = 100;
    private PictureHandler pictureHandler;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_picture_test;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    protected RecyclerView.LayoutManager getRvLayoutManger() {
        return new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
    }

    @Override
    public void initData() {
        contentList = getResources().getStringArray(R.array.pic_list);
        values = new String[]{getAlignmentArray()[mAlignment], String.valueOf(count), String.valueOf(intervalTime),};
        super.initData();

        qrAlignmentList = getAlignmentList(mAlignment);

        pictureHandler = new PictureHandler(Looper.myLooper());
        examplePic = binding.getRoot().findViewById(R.id.iv_example_pic);
        vectorToBitmap();
    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        CustomDividerItemDecoration decoration = new CustomDividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL, 2);
        decoration.setDrawable(this.getActivity().getDrawable(R.drawable.shape_line));

        return decoration;
    }

    @Override
    protected CommonTestAdapter initAdapter() {
        ArrayList<FunctionTestBean> list = new ArrayList<>();

        for (int i = 0; i < contentList.length; i++) {
            list.add(new FunctionTestBean(contentList[i], values[i]));
        }

        return new CommonTestAdapter(R.layout.item_common, list);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        getRvAdapter().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FunctionTestBean item = getRvAdapter().getItem(position);
                Log.d(TAG, "onItemChildClick: " + item.getTitle());
                TextView tvValue = view.findViewById(R.id.tv_item_value);

                if (position == 0) {
                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);
                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qrAlignmentList, (dialog, i) -> {
                                        qrAlignmentList = getAlignmentList(i);
                                        mAlignment = i;
                                        String[] alignmentArray = getAlignmentArray();
                                        if (alignmentArray != null) {
                                            tvValue.setText(alignmentArray[i]);
                                        }
                                        dialog.dismiss();
                                    }
                            ).addAction(getString(R.string.action_cancel), ((dialog, i) -> {

                                dialog.dismiss();
                            }))
                            .create();
                    mSelectDialog.show();
                } else if ((position == 1)) {

                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(10)
                            .setSeeBarMinProcess(1)
                            .setSeeBarProcess(count)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                count = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", count= " + count);
                                printCount = count;
                                tvValue.setText(count + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                } else if ((position == 2)) {
                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());

                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(6)
                            .setSeeBarProcess(intervalTime)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                intervalTime = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", intervalTime= " + intervalTime);
                                tvValue.setText(intervalTime + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                }

            }
        });

        print = binding.getRoot().findViewById(R.id.print);
        print.setOnClickListener(v -> {
            Log.d(TAG, " count= " + count
                    + ", mAlignment= " + mAlignment
                    + ", intervalTime= " + intervalTime
            );

            print.setEnabled(false);
            pictureHandler.removeMessages(PRINTER_BITMAP);
            pictureHandler.sendEmptyMessage(PRINTER_BITMAP);
        });

        Button btn_menu = binding.getRoot().findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(view -> {
//            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic7236);
//            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.a1110);
//            PrinterHelper.getInstance().printBitmap(toGrayscale(bitmap1),null);
//            PrinterHelper.getInstance().printBitmap(toGrayscale(bitmap2),null);
//            PrinterHelper.getInstance().printBitmapColorChart(bitmap1,null);
//            PrinterHelper.getInstance().printBitmapColorChart(bitmap2,null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap1 = Glide.with(getContext()).asBitmap()
                                .load("https://oss.91xft.cn/x-orchard/console/common/20241129164235_iShot_2024-11-29_16.40.55.png")
                                .submit(160,160).get();
                        PrinterHelper.getInstance().printBitmapColorChart(bitmap1,null);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

        });

    }

    private static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }


    private void vectorToBitmap() {

        VectorDrawable vDrawable = (VectorDrawable) examplePic.getDrawable();
        int width = vDrawable.getIntrinsicWidth();
        int height = vDrawable.getIntrinsicHeight();

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vDrawable.setBounds(0, 0 , width, height);
        vDrawable.draw(canvas);
        int withPixel = PrinterHelper.getInstance().getPrinterPaperType() ==
                BitmapUtils.PRINTER_TYPE_58 ? BitmapUtils.WIDTH_58_PIXEL/2 : BitmapUtils.WIDTH_80_PIXEL/2;

        int w = (withPixel + 7) / 8 * 8;
        int h = bitmap.getHeight() * w / bitmap.getWidth();
        bitmap = BitmapUtils.resize(bitmap, w, h);

    }

    private String[] getAlignmentArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.alignment);
        return array;
    }

    private List<DialogItemDescription> getAlignmentList(int checkIndex) {
        String[] qrSizeArray = getAlignmentArray();
        List<DialogItemDescription> list = new ArrayList<>();
        for (int i = 0; i < qrSizeArray.length; i++) {
            DialogItemDescription item = new DialogItemDescription(qrSizeArray[i]);
            if (checkIndex == i) {
                item.setChecked(true);
            }
            list.add(item);
        }

        return list;
    }

    private void printBitmap() {

        PrinterHelper.getInstance().printBitmapWithAlign(bitmap, mAlignment, new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {
                Log.d(TAG, " printBitmapWithAlign    onRunResult ====>    " + isSuccess);
            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "  onReturnString ====>    " + result);
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });
        PrinterHelper.getInstance().printAndFeedPaper(100);
        PrinterHelper.getInstance().partialCut();
    }


    class PictureHandler extends Handler {
        public PictureHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what + ", printCount= " + printCount);

            switch (msg.what) {
                case PRINTER_BITMAP:

                    if (printCount > 0) {
                        printBitmap();
                        printCount--;
                        pictureHandler.sendEmptyMessageDelayed(PRINTER_BITMAP, intervalTime * 1000L);
                    } else {

                        print.setEnabled(true);
                        printCount = count;
                    }
                    break;

            }
        }
    }

}

