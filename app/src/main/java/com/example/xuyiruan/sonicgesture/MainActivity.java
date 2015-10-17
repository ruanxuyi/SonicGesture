package com.example.xuyiruan.sonicgesture;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;

import android.media.AudioRecord;
import android.widget.SeekBar;
import android.widget.Toast;

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

    //Kefei sine wave private instance
    public static int HEIGHT = 127; //amplititude of the sound, AKA the volumn
    /** 2PI**/
    public static final double TWOPI = 2 * 3.1415;
    public boolean start=true;

    public AudioTrack audioTrack;
    public int Hz; //frequency of the sound
    public int waveLen;
    public int length;
    public byte[] wave;
    public int sampleRate;


    AudioRecord mRecorder;            //Media player instance
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //volume control module
        SeekBar volumeSeekBar = null;
        volumeSeekBar = (SeekBar)findViewById(R.id.volumeSeekBar);



        //System.out.println("Here");
        try{
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar volumnSeekBar) {}

                @Override
                public void onStartTrackingTouch(SeekBar volumnSeekBar) {}

                @Override
                public void onProgressChanged(SeekBar volumnSeekBar, int progress, boolean fromUser) {
                    HEIGHT = (int)(1.27*progress);
                    //System.out.println("Height is" + HEIGHT);
                    Hz=110;
                    waveLen = 44100/ Hz;
                    length = waveLen * Hz;
                    sampleRate=44100;
                    wave = new byte[length];
                    wave=sin(wave, waveLen, length);//please read helper method for details
                    audioTrack=new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO, // CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);
                    audioTrack.write(wave, 0, length);
                    audioTrack.setLoopPoints(0, length / 4, -1);
                }
            });

        }
        catch (Exception e)
        {
            System.out.print("error with volumeSeekBar");
        }




        //set up start/pause button
        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {



            public void onClick(View view) {
                //update startCount for stop/pause button functionality
                startCount++;

                //system service to manage output device,
                if(startCount%2 == 0){
                    audioTrack.pause();

                    //audioRecord.stop();

                    start=true;
                }else{
                    if(audioTrack!=null)
                    {
                        audioTrack.play();
                    }
                    //audioRecord.startRecording();

                    start=false;
                    //startRecord();


                }
            }
        });
    }

    //Audio Recording method, reading audio into a float buffer
    //not functioning yet
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

    /**
     * sine wave generator
     * @param wave
     * @param waveLen
     * @param length
     * @return
     */

    public static byte[] sin(byte[] wave, int waveLen, int length)
    {


        for (int i = 0; i < length; i++) {
            wave[i] = (byte) (HEIGHT * (1 - Math.sin(TWOPI
                    * ((i % waveLen) * 1.00 / waveLen))));
        }
        return wave;
    }
}
