package com.fsu.hack.app.hackfsu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import okhttp3.MediaType;
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
    private Button reset;
    private MicrosoftApiService service;
    private String identificationId;
    private boolean audioRecorded = false;
    private byte[] bytes;
    private HashMap<String, String> accounts;
    private EditText name;
    private Button read;
    private String mostRecentOp;
    private Button getOpStatus;
    private ArrayList<String> accountIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        E/TEST: tommy: 895681ab-0b26-4b90-b4e1-0abc510b75f5
        E/TEST: jake: 2c261643-d8f1-44fb-b2e1-b23af77ff29d
        E/TEST: dave: 2f4929be-4abc-4407-a676-f6a1b46b9489
         */

        filePath = "/storage/emulated/legacy/Android/data/com.fsu.hack.app.hackfsu/cache/recorded_audio.wav";

        verifyButton = (Button) findViewById(R.id.verifyButton);
        enrollButton = (Button) findViewById(R.id.enrollButton);
        recognizeButton = (Button) findViewById(R.id.recognizeButton);
        recordButton = (Button) findViewById(R.id.recordButton);
        reset = (Button) findViewById(R.id.resetButton);
        name = (EditText) findViewById(R.id.editName);
        read = (Button) findViewById(R.id.readButton);
        getOpStatus = (Button) findViewById(R.id.getOpStatusButton);

        // Initialize Service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://westus.api.cognitive.microsoft.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MicrosoftApiService.class);
        accounts = new HashMap<>();
        accountIds = new ArrayList<>();


        // ONLY FOR TESTING PURPOSES
        accountIds.add("53d9aab9-eb7d-4ebd-88e8-3c22c859e28e");
        accountIds.add("e9eab3d7-c202-4c6c-89b8-2f93fb78fc06");
        accountIds.add("689b1327-2232-4d48-8749-971b71268012");
        accounts.put("53d9aab9-eb7d-4ebd-88e8-3c22c859e28e", "Tommy");
        accounts.put("e9eab3d7-c202-4c6c-89b8-2f93fb78fc06", "Jake");
        accounts.put("689b1327-2232-4d48-8749-971b71268012", "David");
        //

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationId = null;
                enrollButton.setText("Get New ID");
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String s : accounts.keySet()) {
                    Log.e("TEST", s);
                }
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
                        retrofit2.Response<ResponseBody> r = new NetworkCall().execute(id_call).get();
                        Log.e("TEST", "CODE: " + String.valueOf(r.code()));
                        if (r.body() != null) {
                            response = new JSONObject(r.body().string());
                        } else {
                            Toast.makeText(getBaseContext(), r.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
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
                    } else if (name.isDirty()) {
                        Toast.makeText(getBaseContext(), "Insert your name in the box!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Send Audio for enrollment
                        //.
                        //.
                        //.
                        // Retrieve Audio
                        try {
                            bytes = FileUtils.readFileToByteArray(new File(filePath));
                        } catch (Exception e) {
                            Log.e("TEST", "Failed reading file: " + e.toString());
                        }
                        RequestBody requestBody = RequestBody
                                .create(MediaType.parse("application/octet-stream"), bytes);
                        Call<ResponseBody> id_call = service.enroll(identificationId, true, requestBody);
                        try {
                            retrofit2.Response<ResponseBody> r = new NetworkCall().execute(id_call).get();
                            Log.e("TEST", "CODE: " + String.valueOf(r.code()));
                            if (r.code() != 202) {
                                Toast.makeText(getBaseContext(), r.errorBody().string(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Well done! You are enrolled.", Toast.LENGTH_SHORT).show();
                                mostRecentOp = r.headers().get("Operation-Location").split("/")[r.headers().get("Operation-Location").split("/").length - 1];
                                Toast.makeText(getBaseContext(), mostRecentOp, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("TEST", "Failed to execute request: " + e.toString());
                        }
                        accounts.put(identificationId, name.getText().toString());
                        accountIds.add(identificationId);
                        audioRecorded = false;
                        identificationId = null;
                        name.setText("");
                        enrollButton.setText("Get New ID");
                    }
                }

                recognizeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!audioRecorded) {
                            Toast.makeText(getBaseContext(), "Record an audio first!", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                bytes = FileUtils.readFileToByteArray(new File(filePath));
                            } catch (Exception e) {
                                Log.e("TEST", "Failed reading file: " + e.toString());
                            }
                            RequestBody requestBody = RequestBody
                                    .create(MediaType.parse("application/octet-stream"), bytes);
                            Call<ResponseBody> id_call = service.identify(accountIds, true, requestBody);
                            String response = null;
                            try {
                                retrofit2.Response<ResponseBody> r = new NetworkCall().execute(id_call).get();
                                Log.e("TEST", "CODE: " + String.valueOf(r.code()));
                                if (r.code() != 202) {
                                    Toast.makeText(getBaseContext(), r.errorBody().string(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Well done! Your request succeeded.", Toast.LENGTH_SHORT).show();
                                    response = r.headers().get("Operation-Location");
                                    mostRecentOp = response.split("/")[response.split("/").length - 1];
                                    Toast.makeText(getBaseContext(), response.split("/")[response.split("/").length - 1], Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("TEST", "Failed to execute request: " + e.toString());
                            }
                        }
                    }
                });

                getOpStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostRecentOp != null) {
                            getOpStatus(mostRecentOp);
                        } else {
                            Toast.makeText(getBaseContext(), "opcode not set!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void getOpStatus(String response) {
        Log.e("GETTINGOPSTATUS", "GETTINGOPSTATUS: " + response);
        Call<ResponseBody> opCall = service.getOpStatus(response.split("/")[response.split("/").length - 1]);
        try {
            retrofit2.Response<ResponseBody> resp_opCall = new NetworkCall().execute(opCall).get();
            Log.e("TEST", "CODE: " + String.valueOf(resp_opCall.code()));
            if (resp_opCall.code() != 200) {
                Toast.makeText(getBaseContext(), resp_opCall.errorBody().string(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), resp_opCall.body().string(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("TEST", "Failed to execute request: " + e.toString());
        }
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