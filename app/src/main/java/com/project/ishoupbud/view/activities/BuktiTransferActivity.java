package com.project.ishoupbud.view.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.fragment.SaldoTransactionFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.ImageUtils;
import com.project.michael.base.utils.StringUtils;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

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

        if(transaction.status == 1){
            tvPathImage.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
            tvTextKonfirmasi.setVisibility(View.VISIBLE);
        }else if(transaction.status == 4){
            tvTextKonfirmasi.setText("Status: Dibatalkan");
            tvPathImage.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
            tvTextKonfirmasi.setVisibility(View.VISIBLE);
        }else{
            tvTextKonfirmasi.setText("Status: Pending");
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
        tvPathImage.setText(mCurrentPhotoPath);
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

    public void doConfirmTopUp(){
        if(StringUtils.isNullOrEmpty(mCurrentPhotoPath)){
            Toast.makeText(this, "Tidak ada foto untuk di upload", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("Sedang mengupload gambar..");
        MultipartBody.Part body;
        File tmpfile = new File(mCurrentPhotoPath);
        byte[] bytes = ImageUtils.compressImage(tmpfile);
        RequestBody requestFile;
        if (bytes != null){
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
        }else{
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tmpfile);
        }
        body = MultipartBody.Part.createFormData("image", tmpfile.getName(), requestFile);
        APIManager.getRepository(TransactionRepo.class).konfirmTopUp(transaction.id, body)
                .enqueue(new APICallback<GenericResponse<Transaction>>() {
                    @Override
                    public void onSuccess(Call<GenericResponse<Transaction>> call, retrofit2.Response<GenericResponse<Transaction>> response) {
                        super.onSuccess(call, response);
                        dismissDialog();
                        Toast.makeText(BuktiTransferActivity.this, "Upload telah selesai",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Call<GenericResponse<Transaction>> call, retrofit2.Response<GenericResponse<Transaction>> response) {
                        super.onError(call, response);
                        dismissDialog();
                        Toast.makeText(BuktiTransferActivity.this, "Ada masalah, silahkan diulangi",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<GenericResponse<Transaction>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissDialog();
                        Toast.makeText(BuktiTransferActivity.this, "Ada masalah, silahkan diulangi",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void batalTransaksi(){
        showDialog("Batal Transaksi");
        APIManager.getRepository(TransactionRepo.class).cancel(transaction.id)
                .enqueue(new APICallback<Response>() {
                    @Override
                    public void onSuccess(Call<Response> call, retrofit2.Response<Response>
                            response) {
                        super.onSuccess(call, response);
                        dismissDialog();
                        Intent i = new Intent();
                        i.putExtra(ConstClass.TRANSACTION_EXTRA, transaction.id);
                        setResult(SaldoTransactionFragment.BATAL_TRASASKSI, i);
                        finish();
                    }

                    @Override
                    public void onError(Call<Response> call, retrofit2.Response<Response>
                            response) {
                        super.onError(call, response);
                        dismissDialog();
                        Toast.makeText(BuktiTransferActivity.this, "Ada masalah, silahkan diulangi",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissDialog();
                        Toast.makeText(BuktiTransferActivity.this, "Ada masalah, silahkan diulangi",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
                mCurrentPhotoPath = getRealPathFromUri(this, imageUri);
                if(mCurrentPhotoPath == null){
                    if(imageUri.getLastPathSegment().startsWith("file")){
                        mCurrentPhotoPath = imageUri.getLastPathSegment().replace("file://","");
                    }else{
                        mCurrentPhotoPath = imageUri.getPath();
                    }
                }
                processImage();
            }
        }
    }

    public String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (IllegalArgumentException e) {
            return null;
        }catch (NullPointerException e){
            return null;
        }finally {
            if (cursor != null) {
                cursor.close();
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
                batalTransaksi();
                break;
            case R.id.btn_confirmation:
                doConfirmTopUp();
                break;
        }
    }
}
