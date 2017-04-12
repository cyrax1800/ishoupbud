package com.project.ishoupbud.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.project.ishoupbud.R;

/**
 * Created by michael on 4/12/17.
 */

public class CategoriesDialogFragment extends DialogFragment {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_categories,container,false);
        getDialog().setTitle("Select Categories");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_categories);

        return rootView;
    }
}