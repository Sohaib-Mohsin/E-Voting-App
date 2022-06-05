package com.vote.E_Voting_App.Login_Status;

import android.content.Context;
import android.content.SharedPreferences;

public class Check_Login_Status {

    public static void Save_Login(Context context, String As){
        SharedPreferences.Editor editor = context.getSharedPreferences("Login_As", Context.MODE_PRIVATE).edit();
        editor.putString("As",As);
        editor.apply();
    }
    public static String Get_Login(Context context){
        SharedPreferences sharedPreferences =  context.getSharedPreferences("Login_As", Context.MODE_PRIVATE);
        return sharedPreferences.getString("As",null);
    }

}
