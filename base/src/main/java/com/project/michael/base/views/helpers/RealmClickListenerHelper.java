package com.project.michael.base.views.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.project.michael.base.models.RealmModel;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.adapters.RealmBaseAdapter;
import com.project.michael.base.views.listeners.ClickEventListener;
import com.project.michael.base.views.listeners.LongClickEventListener;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;

/**
 * Created by michael on 5/1/17.
 */

public class RealmClickListenerHelper<Model extends RealmObject> {

    private RealmBaseAdapter<Model> mBaseAdapter;

    private Map<Integer, EventListener> clickListenerMap = new HashMap<>();

    public RealmClickListenerHelper(RealmBaseAdapter<Model> baseAdapter){
        this.mBaseAdapter = baseAdapter;
    }

    public void addEventListener(int viewId, EventListener listener){
        clickListenerMap.put(viewId, listener);
    }

    public void attachToView(final RecyclerView.ViewHolder viewHolder){
        ViewGroup viewGroup = ((ViewGroup)viewHolder.itemView);
        int totalChild = viewGroup.getChildCount();
        for(int i = 0; i < totalChild; i++){
            final View view = viewGroup.getChildAt(i);
            final EventListener eventListener = clickListenerMap.get(view.getId());
            if(eventListener != null){
                if(eventListener instanceof ClickEventListener){
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = mBaseAdapter.getViewHolderPosition(viewHolder);
                            ((ClickEventListener)eventListener).onClick(
                                    view,
                                    mBaseAdapter.getItemAt(position),
                                    position);
                        }
                    });
                }else if(eventListener instanceof LongClickEventListener){
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int position = mBaseAdapter.getViewHolderPosition(viewHolder);
                            ((LongClickEventListener)eventListener).onLongClick(
                                    view,
                                    mBaseAdapter.getItemAt(position),
                                    position);
                            return false;
                        }
                    });
                }
            }
        }
    }
}