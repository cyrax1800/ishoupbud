package com.project.ishoupbud.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/**
 * Created by michael on 7/18/17.
 */

public class ZBarScanService extends IntentService {
    public static final String TAG = "tmp-ZBar-Service";
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String RESULT_DATA_KEY = "barcodeResult";
    public static final String RECEIVER = "receiver";
    public static final String IMAGE_DATA_EXTRA = "image-data";
    public static final String CAMERA_WIDTH = "width";
    public static final String CAMERA_HEIGHT = "height";
    public static byte[] Data;

    protected ResultReceiver mReceiver;

    public ZBarScanService() {
        super("ZBarScanService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ZBarScanService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mReceiver = intent.getParcelableExtra(RECEIVER);
        int width = intent.getIntExtra(CAMERA_WIDTH, 0);
        int height = intent.getIntExtra(CAMERA_HEIGHT, 0);
        Image barcodeImg = new Image(width, height, "Y800");
        barcodeImg.setData(Data);

        ImageScanner scanner = new ImageScanner();
        int[] symbol = new int[]{Symbol.UPCA,Symbol.UPCE, Symbol.EAN8, Symbol.EAN13,
                Symbol.CODE39, Symbol.CODE128};
        for(int i = 0; i< symbol.length;i++){
            scanner.setConfig(symbol[i], Config.X_DENSITY, 3);
            scanner.setConfig(symbol[i], Config.Y_DENSITY, 3);
        }

        int result = scanner.scanImage(barcodeImg);

        if (result != 0) {
            SymbolSet syms = scanner.getResults();
            for (Symbol sym : syms) {
                Log.i(TAG, "onPreviewFrame: " + sym.getData());
                final String barcodeText = sym.getData();
                deliverResultToReceiver(SUCCESS_RESULT, barcodeText);
                break;
            }
            return;
        }
        deliverResultToReceiver(FAILURE_RESULT, "");
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
