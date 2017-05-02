package com.project.michael.base.views.adapters;

import android.support.v7.widget.RecyclerView;

/**
 * Created by michael on 5/2/17.
 */

public interface IBaseAdapter<Model> {
    public Model getItemAt(int index);
    public int getViewHolderPosition(RecyclerView.ViewHolder viewHolder);
}
