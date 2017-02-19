package com.fsu.hack.app.hackfsu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
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
    private Button reset;
    private MicrosoftApiService service;
    private String identificationId;
    private boolean audioRecorded = false;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePath = getExternalCacheDir().getAbsolutePath() + "/" + filename;

        verifyButton = (Button) findViewById(R.id.verifyButton);
        enrollButton = (Button) findViewById(R.id.enrollButton);
        recognizeButton = (Button) findViewById(R.id.recognizeButton);
        recordButton = (Button) findViewById(R.id.recordButton);
        reset = (Button) findViewById(R.id.resetButton);

        // Initialize Service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://westus.api.cognitive.microsoft.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MicrosoftApiService.class);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationId = null;
                enrollButton.setText("Get New ID");
            }
        });

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
                audioRecorded = true;
            }
        });

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (identificationId == null) {
                    // Get Identification number
                    //.
                    //.
                    //.
                    // Request Body
                    HashMap<String, String> kv = new HashMap<>();
                    kv.put("locale", "en-us");

                    // Initialize Call
                    Call<ResponseBody> id_call = service.getIdNumber(kv);

                    // Get Response Body
                    JSONObject response = null;
                    try {
                        String r = (new NetworkCall().execute(id_call).get()).string();
                        response = new JSONObject(r);
                        identificationId = response.get("identificationProfileId").toString();
                    } catch (Exception e) {
                        Log.e("TEST", "Failed to execute request: " + e.toString());
                    }

                    Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                    if (identificationId != null) {
                        enrollButton.setText("Enroll");
                    }
                } else {
                    if (!audioRecorded) {
                        Toast.makeText(getBaseContext(), "Record an audio first!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Send Audio for enrollment
                        //.
                        //.
                        //.
                        // Retrieve Audio
                        try {
                            bytes = FileUtils.readFileToByteArray(new File(getExternalCacheDir().getAbsolutePath(), filename));
                        } catch (Exception e) {
                            Log.e("TEST", "Failed reading file: " + e.toString());
                        }

                        Call<ResponseBody> id_call = service.enroll(identificationId, true, bytes);
                        JSONObject response = null;
                        try {
                            String r = (new NetworkCall().execute(id_call).get()).string();
                            Toast.makeText(getBaseContext(), r, Toast.LENGTH_LONG).show();
                            response = new JSONObject(r);
                        } catch (Exception e) {
                            Log.e("TEST", "Failed to execute request: " + e.toString());
                        }
                        Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                        audioRecorded = false;
                    }
                }
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