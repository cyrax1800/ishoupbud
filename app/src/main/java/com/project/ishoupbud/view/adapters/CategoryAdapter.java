package com.project.ishoupbud.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Category;
import com.project.ishoupbud.view.holders.CategoryHolder;
import com.project.michael.base.views.adapters.RealmBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmObject;

/**
 * Created by michael on 5/1/17.
 */

public class CategoryAdapter<Model extends RealmObject> extends RealmBaseAdapter<Model>{

    public List<Drawable> drawableList = new ArrayList<>();

    public CategoryAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Model> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_all));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_beverage));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_food));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_snacks));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_cosmetic));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_personal_care));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_household));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_stationary));
        drawableList.add(ContextCompat.getDrawable(context, R.drawable.ic_category_else));
        setNew(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return super.onPostCreateViewHolder(new CategoryHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryHolder categoryHolder = (CategoryHolder)holder;
        Category category = (Category)mModelList.get(position);

        categoryHolder.tvName.setText(category.name);
        categoryHolder.ivIcon.setImageDrawable(drawableList.get(position));

    }
}
