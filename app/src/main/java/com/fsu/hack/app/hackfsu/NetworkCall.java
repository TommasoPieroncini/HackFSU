package com.fsu.hack.app.hackfsu;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by tommaso on 2/18/17.
 */

public class NetworkCall extends AsyncTask<Call<ResponseBody>, Void, ResponseBody> {
    @Override
    protected ResponseBody doInBackground(Call<ResponseBody>... params) {
        try {
            Response<ResponseBody> response = params[0].execute();
            if (response.errorBody() != null) {
                return response.errorBody();
            } else {
                return response.body();
            }
        } catch (Exception e) {
            Log.e("TEST", "Failed network call: " + e.toString());
        }
        return null;
    }
}
