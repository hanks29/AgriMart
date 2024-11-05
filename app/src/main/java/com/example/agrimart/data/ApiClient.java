package com.example.agrimart.data;

import androidx.lifecycle.MutableLiveData;

import com.example.agrimart.data.model.Commune;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Province;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit=null;



    public static Retrofit getClient(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl("https://vietnam-administrative-division-json-server-swart.vercel.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
