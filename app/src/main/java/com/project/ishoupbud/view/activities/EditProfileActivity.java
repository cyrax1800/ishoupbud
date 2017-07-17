package com.project.ishoupbud.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.repositories.UserRepo;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.FileUtils;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.ImageUtils;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.views.BaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by michael on 4/12/17.
 */

public class EditProfileActivity extends BaseActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_LOCATION = 3;

    private static final int SUCCESS = -1;
    private static final int CANCEL = 0;

    final CharSequence[] items = {"Take Photo", "Select Photo", "Cancel"};

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_profile_pic) ImageView ivProfile;
    @BindView(R.id.btn_edit_photo) Button btnChangePhoto;
//    @BindView(R.id.btn_save) Button btnSave;
    @BindView(R.id.btn_map) Button btnSelectMap;
    @BindView(R.id.et_name) EditText etName;
    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.etl_email) TextInputLayout etlEmail;
    @BindView(R.id.et_phone) EditText etPhoneNo;
    @BindView(R.id.et_address) EditText etAddress;
//    @BindView(R.id.et_password) EditText etPassword;
//    @BindView(R.id.etl_password) TextInputLayout etlPassword;

    AlertDialog mediaAlertDialog;

    User user;

    Boolean isChangePicture, isChangeName, isChangeEmail, isChangePhone, isChangeAddress;
    Uri imageUri;
    String mCurrentPhotoPath;
    String encodedImage;

    String picBinary, name, email, phone, address, password;
    Double longitude, latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnChangePhoto.setOnClickListener(this);
//        btnSave.setOnClickListener(this);
        btnSelectMap.setOnClickListener(this);

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

        Log.d(TAG, "onCreate: "+ SharedPref.getValueString(ConstClass.USER));

        initProgressDialog("Updating...");
        isChangePicture = isChangeName = isChangeEmail = isChangePhone = isChangeAddress = false;

        user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER), User.class);
        updateView();

    }

    public void updateView(){
        Glide.with(EditProfileActivity.this)
                .load(user.getMediumImage())
                .placeholder(TextImageCircleHelper.getInstance().getImage(user.name))
                .centerCrop()
                .crossFade()
                .into(ivProfile);

        etEmail.setText(user.email);
        etName.setText(user.name);
        etPhoneNo.setText(user.phone);
        etAddress.setText(user.address);
    }

    public boolean checkEmailValid(){
        if(!ValidationUtils.isEmailValid(email)){
            etlEmail.setError("Email is not valid");
            return false;
        }else{
            etlEmail.setError("");
        }
        return true;
    }

    public boolean checkFieldChanged(){
        name = etName.getText().toString();
        phone = etPhoneNo.getText().toString();
        address = etAddress.getText().toString();

        isChangeName = !name.equals(user.name);
        isChangePhone = !phone.equals(user.phone);
        isChangeAddress = !address.equals(user.address);

        if(isChangePicture || isChangeAddress || isChangeName || isChangePhone )
            return true;
        return false;
    }

    public RequestBody createRequestBodyFromObject(String obj){
//        if(obj == null) obj = "";
        return RequestBody.create(MediaType.parse("text/plain"), obj);
    }

    public void editProfile(){
        progressDialog.show();
        Map<String, RequestBody> map = new HashMap<>();
        map.put("name", createRequestBodyFromObject(name));
        map.put("phone", createRequestBodyFromObject(phone));
        map.put("address", createRequestBodyFromObject(address));
        map.put("gender", createRequestBodyFromObject("1"));
        if(latitude != null)
            map.put("latitude", createRequestBodyFromObject(String.valueOf(latitude)));
        if(longitude != null)
            map.put("longitude", createRequestBodyFromObject(String.valueOf(longitude)));
        MultipartBody.Part body;
        if(isChangePicture){
            File tmpfile = new File(mCurrentPhotoPath);
            byte[] bytes = ImageUtils.compressImage(tmpfile);
            RequestBody requestFile;
            if (bytes != null){
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
            }else{
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tmpfile);
            }
            body = MultipartBody.Part.createFormData("picture_url", tmpfile.getName(), requestFile);
        }else{
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            body = MultipartBody.Part.createFormData("pic", "", requestFile);
            if(user.picture_url != null){
                map.put("picture_url", createRequestBodyFromObject(user.picture_url));
            }
        }
        Call<GenericResponse<User>> editProfileCall = APIManager.getRepository(UserRepo.class).editProfile(map, body);
        editProfileCall.enqueue(new APICallback<GenericResponse<User>>() {
            @Override
            public void onSuccess(Call<GenericResponse<User>> call, Response<GenericResponse<User>> response) {
                super.onSuccess(call, response);
                progressDialog.dismiss();
                Intent data = new Intent();
                data.putExtra(ConstClass.USER, GsonUtils.getJsonFromObject(response.body().data, User.class));
                setResult(ProfileFragment.RESULT_CHANGES, data);
                finish();
            }

            @Override
            public void onUnprocessableEntity(Call<GenericResponse<User>> call, Response<GenericResponse<User>> response) {
                super.onUnprocessableEntity(call, response);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GenericResponse<User>> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Call<GenericResponse<User>> call, Response<GenericResponse<User>> response) {
                super.onError(call, response);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOCATION){
            if(resultCode == SUCCESS){
                address = data.getStringExtra(ConstClass.ADDRESS_EXTRA);
                latitude = data.getDoubleExtra(ConstClass.LATITUDE_EXTRA, 0);
                longitude = data.getDoubleExtra(ConstClass.LONGITUDE_EXTRA, 0);
                etAddress.setText(address);
            }
        }else if(requestCode == REQUEST_CAMERA){
            if(resultCode == SUCCESS){
                isChangePicture = true;
                mCurrentPhotoPath = imageUri.getPath();
                processImage();
            }
        }else if(requestCode == SELECT_FILE){
            if(resultCode == SUCCESS){
                isChangePicture = true;
                imageUri = data.getData();
                mCurrentPhotoPath = imageUri.getLastPathSegment().replace("file://","");
                processImage();
            }
        }
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
        Glide.with(EditProfileActivity.this)
                .load(file)
                .placeholder(TextImageCircleHelper.getInstance().getImage(user.name))
                .centerCrop()
                .crossFade()
                .skipMemoryCache(true)
                .into(ivProfile);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }else if(item.getItemId() == R.id.action_save){
            if(checkFieldChanged()){
                editProfile();
            }else{
                Toast.makeText(getApplicationContext(), "Nothing Change", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.dismiss();
        setResult(ProfileFragment.RESULT_NO_CHANGES);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_photo:
                mediaAlertDialog.show();
                break;
            case R.id.btn_map:
                Intent i = new Intent(this, SelectLocationActivity.class);
                startActivityForResult(i, REQUEST_LOCATION);
                break;
        }
    }
}
