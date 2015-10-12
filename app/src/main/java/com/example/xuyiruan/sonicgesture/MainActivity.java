package com.example.xuyiruan.sonicgesture;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;

import android.media.AudioRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private static final int SAMPLE_RATE = 8000;        //recorder sample rate
    private static int audio_buffer = 1024;
    byte[] audioData = new byte [1024];
    //initiate start/stop button
    private Button startBtn;
    //if startCount is odd, start to play music, if even, pause play music
    int startCount = 0;

    AudioRecord mRecorder;            //Media player instance
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up start/pause button
        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {

            //set up the mediaPlayer and link the music to raw (canon in d)
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio);

            //media player for playing music/soundclip
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);




            public void onClick(View view) {
                //update startCount for stop/pause button functionality
                startCount++;

                //system service to manage output device,
                if(startCount%2 == 0){
                    mediaPlayer.pause();
                }else{
                    audioManager.setSpeakerphoneOn(false);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);//set to play on front speaker
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);               //loop sound clip
                    startRecord();


                }
            }
        });
    }

    //Audio Recording method, reading audio into a float buffer
    private void startRecord()
    {
        mRecorder= new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_8BIT,audio_buffer);
        mRecorder.startRecording();
        mRecorder.read(audioData,0,1024);           //API 3 & above
        mRecorder.release();
        mRecorder.stop();


        PrintWriter writer = null;
        try {
            writer = new PrintWriter("sound.txt", "UTF-8");
            writer.println(audioData);

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
