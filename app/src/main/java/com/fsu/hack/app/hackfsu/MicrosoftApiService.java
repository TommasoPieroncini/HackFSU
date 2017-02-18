package com.fsu.hack.app.hackfsu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by tommaso on 2/18/17.
 */

public interface MicrosoftApiService {
    @Headers({
            "Content-Type: application/json",
            "Ocp-Apim-Subscription-Key: 0ee1105babe649ba903b7c04b275e89e"
    })
    @POST("spid/v1.0/identificationProfiles/")
    Call<ResponseBody> getIdNumber(@Body HashMap<String, String> locale);

}
