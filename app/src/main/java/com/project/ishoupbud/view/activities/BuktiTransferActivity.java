package com.project.ishoupbud.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/13/17.
 */

public class BuktiTransferActivity extends BaseActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_LOCATION = 3;

    private static final int SUCCESS = -1;
    private static final int CANCEL = 0;

    final CharSequence[] items = {"Take Photo", "Select Photo", "Cancel"};

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_total_top_up) TextView tvTotalTopUp;
    @BindView(R.id.tv_current_saldo) TextView tvCurrentSaldo;
    @BindView(R.id.tvPathImage) TextView tvPathImage;
    @BindView(R.id.btn_upload) Button btnUploadPhoto;
    @BindView(R.id.btn_cancel) Button btnCancel;
    @BindView(R.id.btn_confirmation) Button btnSubmit;
    @BindView(R.id.tv_saldo_konfirmasi) TextView tvTextKonfirmasi;
    AlertDialog mediaAlertDialog;

    Transaction transaction;
    User user;

    Uri imageUri;
    String mCurrentPhotoPath;
    String encodedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti_transfer);

        ButterKnife.bind(this);

        toolbar.setTitle("Upload Bukti Transfer");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            imageUri = Uri.fromFile(photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    }
                } else if (items[item].equals("Select Photo")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        mediaAlertDialog = builder.create();

        btnUploadPhoto.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        transaction = GsonUtils.getObjectFromJson(getIntent().getStringExtra(
                ConstClass.TRANSACTION_EXTRA), Transaction.class);
        user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER), User.class);

        tvTextKonfirmasi.setVisibility(View.GONE);
        if(transaction.status == 1){
            tvPathImage.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
            tvTextKonfirmasi.setVisibility(View.VISIBLE);
        }

        tvTotalTopUp.setText("Total Top-up: " + Utils.indonesiaFormat(transaction.nominal));
        tvCurrentSaldo.setText("Current Saldo: " + Utils.indonesiaFormat(user.saldo));
    }

    public void processImage(){
        Log.d(TAG, "processImage: " + mCurrentPhotoPath);
        File file = new File(mCurrentPhotoPath);
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Compress
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if(bitmap != null){ // compress
            Log.d(TAG, "processImage: Image tidak Null");
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            encodedImage = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
        }
//        Glide.with(EditProfileActivity.this)
//                .load(file)
//                .placeholder(TextImageCircleHelper.getInstance().getImage(user.name))
//                .centerCrop()
//                .crossFade()
//                .skipMemoryCache(true)
//                .into(ivProfile);
        Log.d(TAG, "processImage: " + encodedImage );
        Log.d(TAG, "processImage: " + file.length() );
        Log.d(TAG, "processImage: " + encodedImage.length() );
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == SUCCESS){
                mCurrentPhotoPath = imageUri.getPath();
                processImage();
            }
        }else if(requestCode == SELECT_FILE){
            if(resultCode == SUCCESS){
                imageUri = data.getData();
                mCurrentPhotoPath = imageUri.getLastPathSegment().replace("file://","");
                processImage();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_upload:
                mediaAlertDialog.show();
                break;
            case R.id.btn_cancel:
                break;
            case R.id.btn_confirmation:
                break;
        }
    }
}
