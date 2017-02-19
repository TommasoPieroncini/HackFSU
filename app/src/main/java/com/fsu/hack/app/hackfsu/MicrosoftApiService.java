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
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @Headers({
            "Content-Type: multipart/form-data",
            "Ocp-Apim-Subscription-Key: 0ee1105babe649ba903b7c04b275e89e"
    })
    @POST("spid/v1.0/identificationProfiles/{identificationProfileId}/enroll")
    Call<ResponseBody> enroll(@Path("identificationProfileId") String id,
                              @Query("shortAudio") boolean shortAudio,
                              @Body byte[] body);
}
