package com.project.ishoupbud.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.AppRealmModul;
import com.project.ishoupbud.api.model.Category;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.ListProductActivity;
import com.project.ishoupbud.view.adapters.CategoryAdapter;
import com.project.michael.base.database.RealmDb;
import com.project.michael.base.utils.Settings;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.helpers.DividerItemDecoration;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by michael on 4/12/17.
 */

public class CategoriesDialogFragment extends DialogFragment {

    RecyclerView recyclerView;

    CategoryAdapter<Category> categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_categories,container,false);
        getDialog().setTitle("Select Categories");

        categoryAdapter = new CategoryAdapter<>(getContext(), RealmDb.getRealm().where(Category.class).findAll(), true);
        categoryAdapter.setOnClickListener(new BaseAdapter.OnClickListener<Category>() {
            @Override
            public boolean onClick(View v, List<Category> categories, Category category, int position) {
                Intent i = new Intent(getContext(), ListProductActivity.class);
                i.putExtra(ConstClass.CATEGORY_EXTRA, position);
                startActivity(i);
                dismiss();
                return false;
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryAdapter);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}