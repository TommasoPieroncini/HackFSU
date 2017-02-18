package com.fsu.hack.app.hackfsu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;

public class MainActivity extends AppCompatActivity {
    String filePath;
    int color ;
    int requestCode = 0;
    Button recordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath = getExternalCacheDir().getAbsolutePath() + "/recorded_audio.wav";
        Toast.makeText(getBaseContext(), filePath, Toast.LENGTH_LONG).show();


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