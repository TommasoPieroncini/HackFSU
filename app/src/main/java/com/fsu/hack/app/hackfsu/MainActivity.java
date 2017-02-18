package com.fsu.hack.app.hackfsu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String filename = "recorded_audio.wav";
    private String filePath;
    private int color ;
    private int requestCode = 0;
    private Button recordButton;
    private Button enrollButton;
    private Button verifyButton;
    private Button recognizeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath = getExternalCacheDir().getAbsolutePath() + "/" + filename;

        verifyButton = (Button) findViewById(R.id.verifyButton);
        enrollButton = (Button) findViewById(R.id.enrollButton);
        recognizeButton = (Button) findViewById(R.id.recognizeButton);
        recordButton = (Button) findViewById(R.id.recordButton);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = getResources().getColor(R.color.colorPrimaryDark);
                AndroidAudioRecorder.with(MainActivity.this)
                        .setFilePath(filePath)
                        .setColor(color)
                        .setRequestCode(requestCode)
                        .setChannel(AudioChannel.MONO)
                        .setSampleRate(AudioSampleRate.HZ_16000)
                        .record();
            }
        });

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalCacheDir().getAbsolutePath(), filename);
                int size = (int) file.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
                    bf.read(bytes, 0, bytes.length);
                    bf.close();
                } catch (Exception e) {
                    Log.e("TEST", "Error: " + e.toString());
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://westus.api.cognitive.microsoft.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MicrosoftApiService service = retrofit.create(MicrosoftApiService.class);
                HashMap<String, String> kv = new HashMap<>();
                kv.put("locale", "en-us");
                Call<ResponseBody> id_call = service.getIdNumber(kv);
                String response = "";
                try {
                    response = (new NetworkCall().execute(id_call).get()).string();
                } catch (Exception e) {
                    Log.e("TEST", "Failed to execute request: " + e.toString());
                }
                Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Result ok", Toast.LENGTH_SHORT).show();
                // Great! User has recorded and saved the audio file
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Result not-ok", Toast.LENGTH_SHORT).show();
                // Oops! User has canceled the recording
            }
        }
    }
}