package com.project.ishoupbud.view.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.helper.DialogMessageHelper;
import com.project.ishoupbud.helper.googleCamera.ui.camera.CameraSource;
import com.project.ishoupbud.helper.googleCamera.ui.camera.CameraSourcePreview;
import com.project.ishoupbud.service.ZBarScanService;
import com.project.ishoupbud.utils.ConstClass;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.PermissionsUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;

import android.widget.RelativeLayout;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/9/17.
 */

public class ScanBarcodeActivity extends BaseActivity {

//    @BindView(R.id.camera_view) SurfaceView surfaceView;
    @BindView(R.id.scan_container) RelativeLayout overlay;
    @BindView(R.id.camera_view) CameraSourcePreview mPreview;
    @BindView(R.id.rg_flash) RadioGroup rgFlash;
    @BindView(R.id.btn_submit_barcode) Button btnSubmit;
    @BindView(R.id.et_barcode) EditText etBarcode;
    @BindView(R.id.cb_fast_scan) CheckBox cbFastScan;

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    private ZBarScanResultReceiver mResultReceiver;

    Boolean foundedBarcode;
    Boolean useGoogleScanner;
    Boolean isStartService;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        foundedBarcode = false;
        useGoogleScanner = true;
        isStartService = false;

        ButterKnife.bind(this);

        barcodeDetector =
                new BarcodeDetector.Builder(this)
//                        .setBarcodeFormats(Barcode.UPC_A|Barcode.UPC_E|Barcode.EAN_8|Barcode.EAN_13|Barcode.CODE_128|Barcode.CODE_39)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        mResultReceiver = new ZBarScanResultReceiver(new Handler());

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false);
        } else {
            PermissionsUtils.shouldShowRequestPermission(this,PermissionsUtils.PERMISSION_CAMERA);
        }

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                if(!useGoogleScanner) return;
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if(foundedBarcode) return;
                    foundedBarcode = true;
                    btnSubmit.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "receiveDetections: " + barcodes.valueAt(0).displayValue);
                            fetchBarcode(barcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        rgFlash.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_flash_on || checkedId == R.id.rb_flash_off){
                    try {
                        cameraSource.setFlashMode((checkedId == R.id.rb_flash_on)?
                                Camera.Parameters.FLASH_MODE_TORCH:
                                Camera.Parameters.FLASH_MODE_OFF);
                    } catch (Exception e) {
                        Log.d(TAG, "onCheckedChanged: " + e.toString());
                    }
                }
            }
        });

        cbFastScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useGoogleScanner = !isChecked;
            }
        });

        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_ENTER) &&
                        (event.getAction() == KeyEvent.ACTION_UP)){
                    if(etBarcode.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Barcode field cannot be blank",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    foundedBarcode = true;
                    fetchBarcode(etBarcode.getText().toString());
                }
                return false;
            }
        });

        startCameraSource();

        btnSubmit.setOnClickListener(this);
        initProgressDialog("Searching Product..");
    }

    private void startCameraSource() throws SecurityException {

        if (cameraSource != null) {
            try {
                mPreview.start(cameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.UPC_A|Barcode.UPC_E|Barcode.EAN_8|Barcode.EAN_13|Barcode.CODE_128|Barcode.CODE_39)
                /*.setBarcodeFormats(Barcode.ALL_FORMATS)*/.build();

        CameraSource.Builder builder =
                new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(60f)
                .setPreviewCallBack(previewCb);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }
        cameraSource = builder.build();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int previewWidth = mPreview.getMeasuredWidth(),
                previewHeight = mPreview.getMeasuredHeight();

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams =
                (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
    }

    //TODO Cek if found then go to Product detail, if not then go to not found page
    private void fetchBarcode(final String barcode){
        progressDialog.show();
        Call<GenericResponse<Product>> getProduct = APIManager.getRepository(ProductRepo.class).getProductByBarcode(barcode);
        getProduct.enqueue(new APICallback<GenericResponse<Product>>() {
            @Override
            public void onSuccess(Call<GenericResponse<Product>> call, Response<GenericResponse<Product>> response) {
                super.onSuccess(call, response);
                progressDialog.dismiss();
                Product product = response.body().data;
                Intent i = new Intent(ScanBarcodeActivity.this, ProductActivity.class);
                i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product, Product.class));
                startActivity(i);
                finish();
            }

            @Override
            public void onNotFound(Call<GenericResponse<Product>> call, Response<GenericResponse<Product>> response) {
                super.onNotFound(call, response);
                progressDialog.dismiss();
                DialogMessageHelper.getInstance().show(
                        ScanBarcodeActivity.this,
                        "Not Found",
                        "Product with barcode " + barcode + " is not found. please try another product or contect developer for adding the product",
                        "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                foundedBarcode = false;
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(progressDialog.isShowing()) progressDialog.dismiss();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mPreview.stop();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionsUtils.REQUEST_CAMERA){
            if(PermissionsUtils.checkSelfPermission(this,PermissionsUtils.PERMISSION_CAMERA)) {
                Log.d(TAG, "onRequestPermissionsResult: ");
                createCameraSource(true, false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit_barcode:
                if(etBarcode.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Barcode field cannot be blank",Toast.LENGTH_SHORT).show();
                    return;
                }
                foundedBarcode = true;
                fetchBarcode(etBarcode.getText().toString());
                break;
        }
    }

    public void startZBarProses(int width, int height, byte[] data){
        isStartService = true;
        Intent intent = new Intent(this, ZBarScanService.class);
        intent.putExtra(ZBarScanService.RECEIVER, mResultReceiver);
        ZBarScanService.Data = data;
        intent.putExtra(ZBarScanService.CAMERA_WIDTH, width);
        intent.putExtra(ZBarScanService.CAMERA_HEIGHT, height);
        startService(intent);
    }

    class ZBarScanResultReceiver extends ResultReceiver {
        public ZBarScanResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            isStartService = false;
            if(resultCode == ZBarScanService.SUCCESS_RESULT){
                if(foundedBarcode) return;
                foundedBarcode = true;
                fetchBarcode(resultData.getString(ZBarScanService.RESULT_DATA_KEY));
            }
        }
    }

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if(useGoogleScanner) return;
            if(foundedBarcode) return;
            if(isStartService) return;
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();


            startZBarProses(size.width, size.height, data);
        }
    };
}