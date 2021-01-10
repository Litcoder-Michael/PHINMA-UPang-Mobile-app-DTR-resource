package com.example.capstoneprojectfinal;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    // setter
    public void saveImageURI(String uri) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ImageURI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image_uri", uri);

        editor.commit();
    }
    public void saveStudentID(String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("StudID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("stud_id", id);
        editor.commit();
    }


    // getter
    public String getImageURI() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ImageURI", Context.MODE_PRIVATE);
        return sharedPreferences.getString("image_uri", "");
    }
    public String getStudentID() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("StudID", Context.MODE_PRIVATE);
        return sharedPreferences.getString("stud_id", "");
    }
}
