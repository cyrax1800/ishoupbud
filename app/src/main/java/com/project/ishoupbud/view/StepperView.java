package com.project.ishoupbud.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.project.ishoupbud.R;

/**
 * Created by michael on 4/30/17.
 */

public class StepperView extends LinearLayout implements View.OnClickListener{

    ImageButton ibtnPlusStepper, ibtnMinusStepper;
    EditText etStepperCount;

    private boolean canMinus;
    private int value;

    private String beforeText;
    public OnValueChangeListener onValueChangeListener;

    public StepperView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.StepperView, 0, 0);
        canMinus = a.getBoolean(R.styleable.StepperView_canMinus, false);
        value = a.getInt(R.styleable.StepperView_value, 0);
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_stepper, this, true);

        ibtnPlusStepper = (ImageButton) getChildAt(0);
        etStepperCount = (EditText) getChildAt(1);
        ibtnMinusStepper = (ImageButton) getChildAt(2);

        ibtnPlusStepper.setOnClickListener(this);
        ibtnMinusStepper.setOnClickListener(this);

        etStepperCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(beforeText)) return;
                if(s.length() == 0) value = 0;
                else{
                    value = Integer.valueOf(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateText();

    }

    public StepperView(Context context) {
        this(context, null);
    }

    private void updateText(){
        etStepperCount.setText(String.valueOf(value));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        updateText();
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener){
        this.onValueChangeListener = onValueChangeListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_plus_stepper:
                setValue(value + 1);
                if(onValueChangeListener != null) onValueChangeListener.onValueChangeByButtonClick(getValue());
                break;
            case R.id.btn_minus_stepper:
                if(!canMinus && (value == 0)) return;
                setValue(value - 1);
                if(onValueChangeListener != null) onValueChangeListener.onValueChangeByButtonClick(getValue());
                break;
        }
    }

    public interface OnValueChangeListener{
        public void onValueChangeByButtonClick(int value);
    }
}
