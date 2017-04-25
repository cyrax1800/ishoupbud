package com.project.ishoupbud.helper;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.HashMap;

/**
 * Created by michael on 4/25/17.
 */

public class TextImageCircleHelper {

    private static TextImageCircleHelper instance;

    private ColorGenerator generator = ColorGenerator.MATERIAL;

    private TextDrawable.IBuilder builder = TextDrawable.builder().round();

    public HashMap<String,TextDrawable> textDrawables = new HashMap<>();

    public void init(){
        for(int i = 'A' ; i <= 'Z' ; i++){
            textDrawables.put(String.valueOf(((char)i)),builder.build(String.valueOf(((char)i)),generator.getColor(i)));
        }
    }

    public TextDrawable getImage(String str){
        String key = String.valueOf(str.charAt(0)).toUpperCase();
        if(!textDrawables.containsKey(key)){
            return builder.build(key,generator.getRandomColor());
        }
        return textDrawables.get(key);
    }

    public static TextImageCircleHelper getInstance() {
        if(instance == null){
            instance = new TextImageCircleHelper();
        }
        return instance;
    }
}
