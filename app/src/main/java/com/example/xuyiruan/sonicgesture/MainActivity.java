package com.example.xuyiruan.sonicgesture;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final int HEIGHT = 127;
    /** 2PI**/
    public static final double TWOPI = 2 * 3.1415;
    public boolean start=true;
    public Button play;
    public AudioTrack audioTrack;
    public int Hz;
    public int waveLen;
    public int length;
    public byte[] wave;
    public int sampleRate;
    public AudioRecord audioRecord;
    HandlerThread thread;
    Handler handler;


    public static List<Integer> datas=new ArrayList<Integer>();
    public int bufferSize;
    public boolean isRecording;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hz=200;
        waveLen = 44100/ Hz;
        length = waveLen * Hz;
        sampleRate=44100;
        wave = new byte[length];
        wave=sin(wave, waveLen, length);
        //audioTrack.write(wave, 0, length);
        play = (Button)findViewById(R.id.button);
        play.setOnClickListener(new playButtonListener());

        bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
            System.out.println("error");
        audioTrack=new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, // CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);
        audioTrack.write(wave, 0, length);
        audioTrack.setLoopPoints(0, length / 4, -1);
        thread=new HandlerThread("jjj");
        thread.start();
        handler=new Handler(thread.getLooper());
    }
    class playButtonListener implements View.OnClickListener
    {
        public void onClick(View v)
        {
            if(start) {
                datas=new ArrayList<Integer>();
                isRecording=true;
                if(audioTrack!=null)
                {
                    audioTrack.play();
                }

                audioRecord.startRecording();
                handler.post(updateThread);
                play.setText("stop");
                start=false;
            }
            else
            {
                isRecording=false;
                handler.removeCallbacks(updateThread);
                audioTrack.pause();
                audioRecord.stop();
                play.setText("start");
                start=true;
            }
        }

    }
    Runnable updateThread = new Runnable()
    {
        public void run()
        {

            Complex[] x=new Complex[bufferSize];

            short[] buffer = new short[bufferSize];

            int count=0;

            while (isRecording)
            {

                int bufferReadResult=audioRecord.read(buffer, 0, bufferSize);
                count++;
                if(count<=10)
                {
                    count++;
                    System.out.println("count :"+count);
                }
                else
                {
                    count=0;
                    for (int i = 0; i < buffer.length; i++)
                    {

                        x[i]=new Complex(buffer[i],0);
                        System.out.println("buffer: "+buffer[i]);
                    }
                    Complex[] y=fft(x);
                    for(int i=0; i< y.length;i++)
                    {
                        int value=Integer.valueOf((int) Math.round(y[i].abs()));
                        value=value/1000;
                        System.out.println("value: "+value+" size: "+i+" data size: "+datas.size());
                        datas.add(value);
                    }
                }

            }

            /*
            for (int i = 0; i < buffer.length; i++)
            {

                x[i]=new Complex(buffer[i],0);
            }
            Complex[] y=fft(x);
            for(int i=0; i< y.length;i++)
            {
                int value=Integer.valueOf((int) Math.round(y[i].abs()));
                value=value/1000;
                System.out.println("value: "+value+" size: "+i+" data size: "+datas.size());
                datas.add(value);
            }
            */
        }
    };

    /**
     *  Author: Kefei Fu
     *  Function: do FFT on waveform generated by mic recorder.
     *
     */
    public static Complex[] fft(Complex[] x) {


        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static byte[] sin(byte[] wave, int waveLen, int length)
    {
        for (int i = 0; i < length; i++) {
            wave[i] = (byte) (HEIGHT * (1 - Math.sin(TWOPI
                    * ((i % waveLen) * 1.00 / waveLen))));
        }
        return wave;
    }
}
