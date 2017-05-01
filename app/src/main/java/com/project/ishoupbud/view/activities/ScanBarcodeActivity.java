package com.project.ishoupbud.view.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.helper.DialogMessageHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.PermissionsUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/9/17.
 */

public class ScanBarcodeActivity extends BaseActivity {

    @BindView(R.id.camera_view) SurfaceView surfaceView;
    @BindView(R.id.rg_flash) RadioGroup rgFlash;
    @BindView(R.id.btn_submit_barcode) Button btnSubmit;
    @BindView(R.id.et_barcode) EditText etBarcode;

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    Camera camera;

    Boolean foundedBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        foundedBarcode = false;

        ButterKnife.bind(this);

        barcodeDetector =
                new BarcodeDetector.Builder(this)
//                        .setBarcodeFormats(Barcode.UPC_A|Barcode.UPC_E|Barcode.EAN_8|Barcode.EAN_13|Barcode.CODE_128|Barcode.CODE_39)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        surfaceView.getHolder().setFixedSize(metrics.widthPixels, metrics.widthPixels);

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(metrics.widthPixels, metrics.widthPixels)
                .setAutoFocusEnabled(true)
                .build();

        final Activity activity= this;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("tmp", "surfaceCreated:  asdsahbdasibfi");
                try {
                    int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
                    if (rc == PackageManager.PERMISSION_GRANTED) {
                        if (!Utils.hasLollipop()) {
                            camera=getCamera(cameraSource);
                        }
                        cameraSource.start(surfaceView.getHolder());
                        camera=getCamera(cameraSource);
                    } else {
                        PermissionsUtils.shouldShowRequestPermission(activity,PermissionsUtils.PERMISSION_CAMERA);
//                        requestCameraPermission();
                    }
                } catch (IOException ie) {
                    Log.e("tmp", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
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
                        Camera.Parameters param = camera.getParameters();
                        param.setFlashMode((checkedId == R.id.rb_flash_off)?Camera.Parameters.FLASH_MODE_OFF:Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(param);
                    } catch (Exception e) {
                        Log.d(TAG, "onCheckedChanged: " + e.toString());
                    }
                }
            }
        });

        btnSubmit.setOnClickListener(this);
        initProgressDialog("Searching Product..");
    }

    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
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
                foundedBarcode = false;
                progressDialog.dismiss();
                DialogMessageHelper.getInstance().show(
                        ScanBarcodeActivity.this,
                        "Not Found",
                        "Product with barcode " + barcode + " is not found. please try another product or contect developer for adding the product",
                        "Ok", null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(progressDialog.isShowing()) progressDialog.dismiss();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionsUtils.REQUEST_CAMERA){
            try {
                if(PermissionsUtils.checkSelfPermission(this,PermissionsUtils.PERMISSION_CAMERA)) {
                    Log.d(TAG, "onRequestPermissionsResult: ");
                    if (!Utils.hasLollipop()) {
                        camera=getCamera(cameraSource);
                    }
                    cameraSource.start(surfaceView.getHolder());
                    camera=getCamera(cameraSource);
                }
            } catch (IOException ie) {
                Log.e("tmp", ie.getMessage());
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
}
