package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 5/1/17.
 */

public class CategoryHolder extends RecyclerView.ViewHolder {

    public ImageView ivIcon;
    public TextView tvName;

    public CategoryHolder(View itemView) {
        super(itemView);

        ivIcon = (ImageView) itemView.findViewById(R.id.iv_category_icon);
        tvName = (TextView) itemView.findViewById(R.id.tv_category_name);
    }
}
