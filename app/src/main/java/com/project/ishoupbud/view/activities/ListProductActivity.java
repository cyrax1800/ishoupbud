package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Category;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.CategoryAdapter;
import com.project.ishoupbud.view.adapters.ProductAdapter;
import com.project.ishoupbud.view.adapters.ProductPagingAdapter;
import com.project.ishoupbud.view.dialog.CategoriesDialogFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.RealmDb;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.helpers.GridSpacingItemDecoration;
import com.project.michael.base.views.listeners.EndlessScrollListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/13/17.
 */

public class ListProductActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_product) RecyclerView rvProduct;
    @BindView(R.id.fab_scrol_up) FloatingActionButton fabMoveUp;
//    @BindView(R.id.btn_categories) Button btnCategories;
//    @BindView(R.id.nestedScroll) NestedScrollView nestedScrollView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.spinner_category) Spinner spinnerCategory;
    @BindView(R.id.et_keyword) EditText etSearch;
    @BindView(R.id.btn_search) Button btnSearch;
    @BindView(R.id.tv_blank_info) TextView tvBlankInfo;

    ProductPagingAdapter<Product> productAdapter;
    private ArrayAdapter<String> categoryNameList;

    CategoriesDialogFragment categoriesDialogFragment;
    EndlessScrollListener endlessScrollListener;

    int categoryID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        ButterKnife.bind(this);

        fabMoveUp.hide();
//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > oldScrollY) {
//                    fabMoveUp.show();
//                } else {
//                    fabMoveUp.hide();
//                }
//            }
//        });

        categoryID = getIntent().getIntExtra(ConstClass.CATEGORY_EXTRA, 0);
        etSearch.setText(getIntent().getStringExtra(ConstClass.KEYWORD_EXTRA));

        toolbar.setTitle("List Product");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        categoryNameList = new ArrayAdapter<>(ListProductActivity.this,
                android.R.layout.simple_list_item_1);
        CategoryAdapter<Category> tmpCategory = new CategoryAdapter<>(ListProductActivity.this,
                RealmDb.getRealm().where(Category.class).findAll(), true);
        for(int i = 0;i < tmpCategory.getItemCount(); i++){
            categoryNameList.add(tmpCategory.getItem(i).name);
        }

        spinnerCategory.setAdapter(categoryNameList);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCategory.setSelection(categoryID);

//        categoriesDialogFragment = new CategoriesDialogFragment();

        productAdapter = new ProductPagingAdapter<>();
        productAdapter.setOnClickListener(new BaseAdapter.OnClickListener<Product>() {
            @Override
            public boolean onClick(View v, List<Product> products, Product product, int position) {
                Intent i = new Intent(getApplicationContext(), ProductActivity.class);
                i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product,Product.class));
                startActivity(i);
                return false;
            }
        });
//        productAdapter.setNew(Product.getDummy(11));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);
        rvProduct.addItemDecoration(new GridSpacingItemDecoration(2, Utils.dpToPx(16),true));
        rvProduct.setAdapter(productAdapter);


//        btnCategories.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        fabMoveUp.setOnClickListener(this);

        if(getIntent().hasExtra(ConstClass.PRODUCT_EXTRA)){
            @SuppressWarnings("unchecked")
            List<Product> tmpProduct = (List<Product>) GsonUtils.getObjectFromJson
                    (getIntent().getStringExtra(ConstClass.PRODUCT_EXTRA),
                            new TypeToken<List<Product>>() {}.getType());
            productAdapter.setNew(tmpProduct);
            int totalPage = getIntent().getIntExtra(ConstClass.PAGING_EXTRA,1);
            rvProduct.removeOnScrollListener(endlessScrollListener);
            endlessScrollListener = new EndlessScrollListener(productAdapter, totalPage, 4) {
                @Override
                public void LoadMore(int currentPage) {
                    super.LoadMore(currentPage);
                    doSearch(currentPage);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void resetPageCount(int page) {
                    super.resetPageCount(page);
                }

                @Override
                public void resetPageCount() {
                    super.resetPageCount();
                }
            };
            rvProduct.addOnScrollListener(endlessScrollListener);
        }else{
            fetchProduct();
        }

    }

    public void fetchProduct(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("category_id", categoryID + 1);
        map.put("keyword", etSearch.getText().toString());
        Call<GenericResponse<List<Product>>> categoryProductCall = APIManager.getRepository
                (ProductRepo.class).getProductFilter(map);
        categoryProductCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                productAdapter.setNew(response.body().data);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
            }

            @Override
            public void onNotFound(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onNotFound(call, response);
            }
        });
    }

    public void doSearch(final int page){
        showDialog("Searching Product");
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("perpage", 10);
        if(categoryID > 0){
            map.put("category_id", categoryID + 1);
        }
        map.put("keyword", etSearch.getText().toString());
        Call<GenericResponse<List<Product>>> categoryProductCall = APIManager.getRepository
                (ProductRepo.class).getProductFilter(map);
        categoryProductCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                dismissDialog();
                if(response.body().data.size() == 0){
                    tvBlankInfo.setVisibility(View.VISIBLE);
                }else{
                    tvBlankInfo.setVisibility(View.GONE);
                }
                if(page == 1){
                    productAdapter.setNew(response.body().data);
                    endlessScrollListener.setTotalPage(response.body().pagination.total);
                }else{
                    productAdapter.addAll(response.body().data);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissDialog();
            }

            @Override
            public void onNotFound(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onNotFound(call, response);
                dismissDialog();
            }
        });
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
            case R.id.btn_categories:
                categoriesDialogFragment.show(getSupportFragmentManager(),"categoriesDF");
                break;
            case R.id.fab_scrol_up:
//                nestedScrollView.smoothScrollTo(0, 0);
                break;
            case R.id.btn_search:
                doSearch(1);
                break;
        }
    }
}
