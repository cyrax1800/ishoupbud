package com.project.ishoupbud.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.Uber.UberAPI;
import com.project.ishoupbud.api.Uber.model.UberPrices;
import com.project.ishoupbud.api.Uber.repositories.UberRepo;
import com.project.ishoupbud.api.model.FetchingShipmentDataHelper;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.model.map.Direction;
import com.project.ishoupbud.api.repositories.GoogleMapRepo;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.helper.DialogMessageHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductTransactionContainerAdapter;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 6/28/17.
 */

public class BayarActivity extends BaseActivity implements
        FetchingShipmentDataHelper.onFetchingListener {

    public static final int POSITION_REQUEST = 0;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ll_own_address) LinearLayout llAlamatUtama;
    @BindView(R.id.ll_chk_alamat_utama) LinearLayout llChkAlamatUtama;
    @BindView(R.id.chk_alamat_utama) RadioButton rbAlamatUtama;
    @BindView(R.id.tv_alamat) TextView tvAlamat;
    @BindView(R.id.ll_chk_alamat_lain) LinearLayout llChkAlamatLain;
    @BindView(R.id.chk_alamat_lain) RadioButton rbAlamatLain;
    @BindView(R.id.btn_pilih_lokasi) Button btnPilihLokasi;
    @BindView(R.id.ll_alamat_spesifik) LinearLayout llAlamatSpesifik;
    @BindView(R.id.et_address) EditText etAlamat;
    @BindView(R.id.tv_total_price) TextView tvTotalPrice;
    @BindView(R.id.tv_total_kirim) TextView tvTotalKirim;
    @BindView(R.id.tv_total_saldo) TextView tvTotalSaldo;
    @BindView(R.id.tv_total_belanja) TextView tvTotalBelanja;
    @BindView(R.id.rv_product_transaction_container) RecyclerView rvProduct;
    @BindView(R.id.btn_bayar) Button btnBayar;
    @BindView(R.id.total_progress_bar) ProgressBar progressBar;
    @BindView(R.id.ll_total_harga) LinearLayout llTotalHarga;

    ProductTransactionContainerAdapter<ShoppingCartContainer> productTransactionContainerAdapter;
    List<FetchingShipmentDataHelper> fetchingShipmentDataHelpers;

    User user;
    double longitude, latitude;
    double altLongitude, altLatitude;
    int totalItemInFetching;
    float totalItemPrice, totalSaldo, totalBelanja, totalShipmenentPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bayar);

        ButterKnife.bind(this);

        toolbar.setTitle("Pembayaran");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnPilihLokasi.setOnClickListener(this);
        btnBayar.setOnClickListener(this);
        llChkAlamatUtama.setOnClickListener(this);
        llChkAlamatLain.setOnClickListener(this);

        user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER), User.class);
        totalItemPrice = totalSaldo = totalBelanja = totalShipmenentPrice = 0;
        totalSaldo = user.saldo;
        if (user.longitude != null && user.latitude != null) {
            longitude = user.longitude;
            latitude = user.latitude;
            tvAlamat.setText(user.address);
        }


        fetchingShipmentDataHelpers = new ArrayList<>();
        List<ShoppingCartContainer> tmpShoppingCartContainer;
        tmpShoppingCartContainer = (List<ShoppingCartContainer>) GsonUtils.getObjectFromJson
                (getIntent().getStringExtra(ConstClass.CART_EXTRA)
                        , new TypeToken<List<ShoppingCartContainer>>() {
                        }.getType());

        productTransactionContainerAdapter = new ProductTransactionContainerAdapter<>(this);
        productTransactionContainerAdapter.setNew(tmpShoppingCartContainer);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        rvProduct.setLayoutManager(layoutManager);
        rvProduct.setAdapter(productTransactionContainerAdapter);

        for (int i = 0; i < tmpShoppingCartContainer.size(); i++) {
            fetchingShipmentDataHelpers.add(new FetchingShipmentDataHelper(i, this));
            totalItemPrice += tmpShoppingCartContainer.get(i).subTotal;
        }
        totalItemInFetching = tmpShoppingCartContainer.size();

        progressBar.setVisibility(View.VISIBLE);
        llTotalHarga.setVisibility(View.GONE);

        updateText();

        if (longitude == 0 && latitude == 0) {
            llAlamatUtama.setVisibility(View.GONE);
            rbAlamatUtama.setChecked(false);
            rbAlamatLain.setChecked(true);
            DialogMessageHelper.getInstance().show(this, "Harus memilih tempat tujuan " +
                    "barang dikirimkan", "Pilih", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callPilihLokasi();
                }
            });
        } else {
            rbAlamatUtama.setChecked(true);
            rbAlamatLain.setChecked(false);
            startFetching();
        }
        initProgressDialog("Checking out...");
    }

    public void callPilihLokasi() {
        Intent intent = new Intent(BayarActivity.this, SelectLocationActivity.class);
        startActivityForResult(intent, POSITION_REQUEST);
    }

    public void startFetching() {
        totalItemInFetching = productTransactionContainerAdapter.getItemCount();
        progressBar.setVisibility(View.VISIBLE);
        llTotalHarga.setVisibility(View.GONE);
        ShoppingCartContainer data;
        for (int i = 0; i < productTransactionContainerAdapter.getItemCount(); i++) {
            data = productTransactionContainerAdapter.getItemAt(i);
            data.isFetching = true;
            productTransactionContainerAdapter.set(i, data);
            fetchingShipmentDataHelpers.get(i).startFetch();
            getPath(i);
            getEstimatePrice(i);
        }
    }

    public void getPath(final int position) {
        final ShoppingCartContainer data = productTransactionContainerAdapter.getItemAt(position);
        HashMap<String, Object> map = new HashMap<>();
        map.put("origin_lat", data.vendor.latitude);
        map.put("origin_lng", data.vendor.longitude);
        map.put("dest_lat", latitude);
        map.put("dest_lng", longitude);
        final FetchingShipmentDataHelper fetcher = fetchingShipmentDataHelpers.get(position);
        if (fetcher.callGetTime != null && fetcher.callGetTime.isExecuted()) {
            fetcher.callGetTime.cancel();
        }
        fetcher.callGetTime = APIManager.getRepository(GoogleMapRepo.class)
                .getDirection(map);
        fetcher.callGetTime.enqueue(new APICallback<Direction>() {
            @Override
            public void onSuccess(Call<Direction> call, retrofit2.Response<Direction> response) {
                super.onSuccess(call, response);
                data.distance = response.body().routes[0].legs[0].distance.text;
                data.duration = response.body().routes[0].legs[0].durationInTraffic.text;
                productTransactionContainerAdapter.set(position, data);
                fetcher.doneTime();
            }

            @Override
            public void onError(Call<Direction> call, retrofit2.Response<Direction> response) {
                super.onError(call, response);
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void getEstimatePrice(final int position) {
        final ShoppingCartContainer data = productTransactionContainerAdapter.getItemAt(position);
        HashMap<String, Object> map = new HashMap<>();
        map.put("start_latitude", data.vendor.latitude);
        map.put("start_longitude", data.vendor.longitude);
        map.put("end_latitude", latitude);
        map.put("end_longitude", longitude);
        final FetchingShipmentDataHelper fetcher = fetchingShipmentDataHelpers.get(position);
        if (fetcher.callGetPrice != null && fetcher.callGetPrice.isExecuted()) {
            fetcher.callGetPrice.cancel();
        }
        fetcher.callGetPrice = UberAPI.getRepository(UberRepo.class).getEstimatePrice(map);
        fetcher.callGetPrice.enqueue(new APICallback<UberPrices>() {
            @Override
            public void onSuccess(Call<UberPrices> call, retrofit2.Response<UberPrices> response) {
                super.onSuccess(call, response);
                UberPrices uberData = response.body();
                int priceValue = Integer.MAX_VALUE;
                for (int i = 0; i < uberData.prices.size(); i++) {
                    priceValue = Math.min(uberData.prices.get(0).lowEstimate, priceValue);
                }
                data.shippingPrice = priceValue;
                productTransactionContainerAdapter.set(position, data);
                fetcher.donePrice();
            }

            @Override
            public void onFailure(Call<UberPrices> call, Throwable t) {
                super.onFailure(call, t);
            }

            @Override
            public void onError(Call<UberPrices> call, retrofit2.Response<UberPrices> response) {
                super.onError(call, response);
            }
        });
    }

    public void calculateTotal() {
        totalShipmenentPrice = 0;
        for (int i = 0; i < productTransactionContainerAdapter.getItemCount(); i++) {
            totalShipmenentPrice += productTransactionContainerAdapter.getItemAt(i).shippingPrice;
        }
        totalBelanja = totalShipmenentPrice + totalItemPrice;
        updateText();
    }

    public void updateText() {
        tvTotalBelanja.setText(Utils.indonesiaFormat(totalBelanja));
        tvTotalKirim.setText(Utils.indonesiaFormat(totalShipmenentPrice));
        tvTotalPrice.setText(Utils.indonesiaFormat(totalItemPrice));
        tvTotalSaldo.setText(Utils.indonesiaFormat(totalSaldo));
    }

    public void doCheckOut() {
        progressDialog.setMessage("Checkint out...");
        progressDialog.show();
        List<Integer> dataCartCheckout = new ArrayList<>();
        for (int i = 0; i < productTransactionContainerAdapter.getItemCount(); i++) {
            dataCartCheckout.add(productTransactionContainerAdapter.getItemAt(i).id);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("cart_id", dataCartCheckout);
        APIManager.getRepository(TransactionRepo.class).checkout(map)
                .enqueue(new APICallback<List<Transaction>>() {
                    @Override
                    public void onSuccess(Call<List<Transaction>> call,
                                          Response<List<Transaction>> response) {
                        super.onSuccess(call, response);
                        dismissDialog();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra(ConstClass.FROM_CHECKOUT_EXTRA, "keTransaksi");
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(Call<List<Transaction>> call,
                                          Throwable t) {
                        super.onFailure(call, t);
                        dismissDialog();
                    }

                    @Override
                    public void onError(Call<List<Transaction>> call,
                                        Response<List<Transaction>> response) {
                        super.onError(call, response);
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), "Saldo tidak cukup", Toast
                                .LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POSITION_REQUEST) {
            if (resultCode == -1) {
                altLongitude = longitude = data.getDoubleExtra(ConstClass.LONGITUDE_EXTRA, 0);
                altLatitude = latitude = data.getDoubleExtra(ConstClass.LATITUDE_EXTRA, 0);
                llAlamatSpesifik.setVisibility(View.VISIBLE);
                etAlamat.setText(data.getStringExtra(ConstClass.ADDRESS_EXTRA));
                rbAlamatLain.setChecked(true);
                rbAlamatUtama.setChecked(false);
                startFetching();
            } else {
                if (longitude == 0 && latitude == 0) {
                    DialogMessageHelper.getInstance().show(this, "Harus memilih tempat tujuan " +
                            "barang dikirimkan", "Pilih", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callPilihLokasi();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_chk_alamat_utama:
                if (rbAlamatUtama.isChecked()) return;
                longitude = user.longitude;
                latitude = user.latitude;
                rbAlamatUtama.setChecked(true);
                rbAlamatLain.setChecked(false);
                startFetching();
                break;
            case R.id.ll_chk_alamat_lain:
                if (rbAlamatLain.isChecked()) return;
                if (altLatitude == 0 && altLongitude == 0) {
                    callPilihLokasi();
                } else {
                    latitude = altLatitude;
                    longitude = altLongitude;
                }
                rbAlamatLain.setChecked(true);
                rbAlamatUtama.setChecked(false);
                startFetching();
                break;
            case R.id.btn_pilih_lokasi:
                callPilihLokasi();
                break;
            case R.id.btn_bayar:
                if (totalItemInFetching > 0) {
                    Toast.makeText(this, "Pengumpulan data belum siap, silahkan tunggu beberapa " +
                            "saat", Toast.LENGTH_SHORT).show();
                    return;
                } else if (longitude == 0 && latitude == 0) {
                    Toast.makeText(this, "Tujuan Pengiriman belum ditentukan", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                doCheckOut();
                break;
        }
    }

    @Override
    public void onCompleteFetch(int idx) {
        ShoppingCartContainer data = productTransactionContainerAdapter.getItemAt(idx);
        data.isFetching = false;
        productTransactionContainerAdapter.set(idx, data);
        totalItemInFetching--;

        if (totalItemInFetching == 0) {
            calculateTotal();
            progressBar.setVisibility(View.GONE);
            llTotalHarga.setVisibility(View.VISIBLE);
        }
    }
}
