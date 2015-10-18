//////////////////////////////////////////////
//
//  ECE 454 Capstone design Project
//  Author:  Yunhe Liu, Wenxuan Mao, Kefei Fu, Xuyi Ruan
//  2015 Fall - Sonic Gesture Project
//
//////////////////////////////////////////////

package com.example.xuyiruan.sonicgesture;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;
import android.media.AudioRecord;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {


    //initiate start/stop button
    private Button startBtn;
    // if startCount is odd, start to play music, if even, pause play music
    int startCount = 0;

    // Kefei: sine wave private instance
    public static final int HEIGHT = 127;
    /** 2PI**/
    public static final double TWOPI = 2 * 3.1415;
    public boolean start=true;
    public AudioTrack audioTrack;
    public int Hz;
    public int waveLen;
    public int length;
    public byte[] wave;
    public int sampleRate;



    // Wenxuan && Xuyi: varible for recording status
    public static boolean isRecording = false;
    AudioRecord mRecorder;
    public static int bufferSize = 1024;
    public static final int SAMPLE_RATE = 8000;    //recorder sample rate
    public static Thread rec_Thread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hz = 440;
        waveLen = 44100/ Hz;
        length = waveLen * Hz;
        sampleRate = 44100;
        wave = new byte[length];
        wave = sin(wave, waveLen, length);

        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);
        audioTrack.write(wave, 0, length);
        audioTrack.setLoopPoints(0, length / 4, -1);

        //set up start/pause button
        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //update startCount for stop/pause button functionality
                startCount++;
                //system service to manage output device,
                if (startCount % 2 == 0 ) {
                    start=true;
                    // Pause Audio Player
                    audioTrack.pause();
                    // STOP Recorder
                    stopRecord();
                } else {
                    start = false;
                    if (audioTrack != null)  {
                        audioTrack.play();
                    }
                    // start recorder
                    startRecord();
                }
            }
        });
    }

    /**
     *  Author: Wenxuan && Xuyi
     *  Function: Start the recorder with AudioRecord function
     *
     */
    private void startRecord() {

        // GET THE MINIUM BUFFERSIZE FOR Audio Recorder.
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // INITIALIZE an audioRecord object
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        mRecorder.startRecording();
        isRecording = true;

        System.out.println("recording started");
        // new thread for recorder to prevent UI freezed
        rec_Thread = new Thread(new Runnable() {
            public void run() {
                try {
                    writeToFile();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, "Recorder Thread");
        rec_Thread.start();
    }

    /**
     *  Author: Xuyi
     *  Function: stop the recorder and reset all relative varibles
     *
     */
    private void stopRecord() {

        isRecording = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        rec_Thread = null;
    }

    /**
     *  Author: Xuyi
     *  Function: collect sample data from MIC and write result to a file.
     *
     */
    private void writeToFile() throws FileNotFoundException {
        File file = new File(getFilesDir().getAbsolutePath() + "/example.txt");
        OutputStream os = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);

        short[] buffer = new short[bufferSize];

        while (isRecording)
        {
            //System.out.println("before buffer read");
            int bufferReadResult = mRecorder.read(buffer, 0, bufferSize);
            //System.out.println("after bufferRead");
            for (int i = 0; i < bufferReadResult; i++) {
                try {
                    //System.out.println("inside write");
                    dos.writeShort(buffer[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < buffer.length; i++){
                System.out.println("Buffer Data: " + buffer[i]);
            }
        }
    }

    /**
     *
     * Author: Kefei Fu
     * Function: sine wave generator
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
