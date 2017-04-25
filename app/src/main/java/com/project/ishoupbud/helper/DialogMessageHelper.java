package com.project.ishoupbud.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.project.michael.base.utils.StringUtils;

/**
 * Created by michael on 4/25/17.
 */

public class DialogMessageHelper {

    private static DialogMessageHelper instance;

    private AlertDialog dialog;

    public void show(Context context, String message){
        show(context, null, message);
    }

    public void show(Context context, String title, String message){
        show(context, title, message, null, null);
    }

    public void show(Context context, String title, String message,
                     String positiveButton, DialogInterface.OnClickListener listener){
        show(context, title, message, positiveButton, listener, null, null);

    }

    public void show(Context context, String message,
                     String positiveButton, DialogInterface.OnClickListener listener){
        show(context, null, message, positiveButton, listener, null, null);

    }

    public void show(Context context, String title, String message,
                     String positiveButton, DialogInterface.OnClickListener positiveListener,
                     String negativeButton, DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(message);
        if(!StringUtils.isNullOrEmpty(title)){
            builder.setTitle(title);
        }
        if(!StringUtils.isNullOrEmpty(positiveButton)){
            builder.setPositiveButton(positiveButton,positiveListener);
        }
        if(!StringUtils.isNullOrEmpty(negativeButton)){
            builder.setNegativeButton(negativeButton,negativeListener);
        }

        dialog = builder.create();
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
        dialog = null;
    }

    public static DialogMessageHelper getInstance() {
        if(instance == null){
            instance = new DialogMessageHelper();
        }
        return instance;
    }
}
