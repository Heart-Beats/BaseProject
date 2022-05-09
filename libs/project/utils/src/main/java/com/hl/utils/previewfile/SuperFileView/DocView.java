package com.hl.utils.previewfile.SuperFileView;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

public class DocView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private static String TAG = "DocView";
    private TbsReaderView mTbsReaderView;
    private Context context;

    public DocView(Context context) {
        this(context, null, 0);
    }

    public DocView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DocView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        reset();
    }

    public void reset() {
        removeAllViews();
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }


    /**
     * 使用 X5 浏览文件
     */
    public void displayFile(File mFile) {

        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "temp");
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            //加载文件
            Bundle localBundle = new Bundle();
            Log.e(TAG, mFile.toString());
            localBundle.putString("filePath", mFile.toString());
            localBundle.putString("tempPath", tempFile.getAbsolutePath());

            if (this.mTbsReaderView == null) {
                this.mTbsReaderView = getTbsReaderView(context);
            }

            // QbSdk.setTbsListener(new TbsListener() {
            //     @Override
            //     public void onDownloadFinish(int i) {
            //         //tbs内核下载完成回调
            //         Log.d("X5core", "tbs内核下载完成");
            //     }
            //
            //     @Override
            //     public void onInstallFinish(int i) {
            //         //内核安装完成回调，
            //
            //         Log.d("X5core", "tbs内核安装完成");
            //     }
            //
            //     @Override
            //     public void onDownloadProgress(int i) {
            //         //下载进度监听
            //
            //         Log.d("X5core", "tbs内核正在下载中----->" + i);
            //     }
            // });

            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            } else {
                Log.e(TAG, "displayFile: 打开文件" + mFile.getName() + "失败");

                Toast.makeText(context, " 打开文件" + mFile.getName() + "失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        Log.d(TAG, "onCallBackAction--->" + "integer = " + integer + ", o = " + o + ", o1 = " + o1);
    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            //销毁界面的时候一定要加上，否则后面加载文件会发生异常。
            mTbsReaderView.onStop();
        }
    }
}
