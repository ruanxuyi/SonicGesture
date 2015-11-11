package com.example.xuyiruan.sonicgesture;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final int HEIGHT = 127;
    /** 2PI**/
    public static final double TWOPI = 2 * 3.1415;
    public boolean start=true;
    public Button play;
    public Button sendEmail;
    public View view;
    public TextView text;
    public AudioTrack audioTrack;
    public int Hz;
    public int waveLen;
    public int length;
    public byte[] wave;
    public int sampleRate;
    public AudioRecord audioRecord;
    HandlerThread thread;
    Handler handler1;
    Handler handler2=new Handler();


    public static List<Integer> datas=new ArrayList<Integer>();
    public ArrayList<Double> realData=new ArrayList<Double>();
    public int bufferSize=512;
    public boolean isRecording;
    private Object tmp = new Object() ;

    public static boolean right;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hz=18000;
        waveLen = 44100/ Hz;
        length = waveLen * Hz;
        sampleRate=44100;
        wave = new byte[length];
        wave=sin(wave, waveLen, length);
        //audioTrack.write(wave, 0, length);
        play = (Button)findViewById(R.id.button);
        play.setOnClickListener(new playButtonListener());

        sendEmail = (Button)findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new sendButtonListener());

        view = findViewById(R.id.view);
        text =(TextView) findViewById(R.id.text);

        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
            System.out.println("error");
        audioTrack=new AudioTrack(
                AudioManager.STREAM_VOICE_CALL,
                sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, // CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(wave, 0, length);
        audioTrack.setLoopPoints(0, length / 4, -1);
        thread=new HandlerThread("jjj");
        thread.start();
        handler1 =new Handler(thread.getLooper());

        right=true;
    }
    class playButtonListener implements View.OnClickListener
    {
        public void onClick(View v)
        {
            if (start) {
                realData=new ArrayList<Double>();

                datas=new ArrayList<Integer>();
                isRecording=true;
                if(audioTrack!=null)
                {
                    audioTrack.play();
                }
                //audioRecord.startRecording();
                handler1.post(updateThread);
                handler2.post(printDoge);
                play.setText("stop");
                start=false;
            }
            else
            {
                isRecording=false;
                handler1.removeCallbacks(updateThread);
                //handler2.removeCallbacks(printDoge);
                audioTrack.pause();
                //audioRecord.stop();
                play.setText("start");
                cal();
                //play();
                start=true;
            }
        }

    }
    Runnable updateThread = new Runnable()
    {
        public void run()
        {
            /*
            Complex[] x=new Complex[bufferSize];
            short[] buffer = new short[bufferSize];
            int count=0;
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    long l = buffer[i];
                    realData.add(l);
                }
                count++;
                if (count <= 30) {
                    System.out.println("count :" + count);
                } else {
                    datas = new ArrayList<Integer>();
                    count = 0;
                    for (int i = 0; i < buffer.length; i++) {
                        x[i] = new Complex(buffer[i], 0);
                    }
                    Complex[] y = fft(x);

                    for (int i = 0; i < y.length; i++) {
                        int value = Integer.valueOf((int) Math.round(y[i].abs()));
                        value = value / 1000;
                        //System.out.println("value: "+value+" size: "+i+" data size: "+datas.size()+" buffer size: "+buffer.length);
                        datas.add(value);
                    }
                    right = !right;
                }
            }
            */
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.txt");
            // Delete any previous recording.
            if (file.exists())
                file.delete();
            // Create the new file.
            try {
                file.createNewFile();
            } catch (IOException e) {
                text.setText("file failed created");
                throw new IllegalStateException("Failed to create " + file.toString());
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create " + file.toString());
            }
            try {
                // Create a DataOuputStream to write the audio data into the saved file.
                OutputStream os = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                DataOutputStream dos = new DataOutputStream(bos);
                short[] buffer = new short[bufferSize];
                audioRecord.startRecording();

                Complex[] x=new Complex[bufferSize];

                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    /*
                    for (int i = 0; i < bufferReadResult; i++)
                    {
                        dos.writeShort(buffer[i]);
                        //x[i] = new Complex(buffer[i], 0);
                    }
                    */

                    for (int i = 0; i < buffer.length; i++)
                    {
                        x[i] = new Complex(buffer[i], 0);
                    }
                    Complex[] y = fft(x);
                    for (int i = 0; i < y.length; i++) {
                        double value = y[i].abs();
                        //dos.writeDouble(value);
                        realData.add(value);
                    }

                }
                audioRecord.stop();
                dos.close();
            } catch (Throwable t) {
                //text.setText("Recording Failed");
            }
        }
    };

    public void cal()
    {
        double intense=0;
        for(int i=3000;i<8192;i++)
        {
            intense+=realData.get(i);
        }
        intense=intense/8192;
        text.setText(Double.toString(intense));
        if(intense>=1000)
            right=!right;


    }

    public void play() {
        // Get the file we want to playback.
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");
        // Get the length of the audio stored in the file (16 bit so 2 bytes per short)
        // and create a short array to store the recorded audio.
        int musicLength = (int)(file.length()/2);
        short[] music = new short[musicLength];
        try {
            // Create a DataInputStream to read the audio data back from the saved file.
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            // Read the file into the music array.
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();
                i++;
            }
            // Close the input streams.
            dis.close();
            // Create a new AudioTrack object using the same parameters as the AudioRecord
            // object used to create the file.
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    11025,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    musicLength*2,
                    AudioTrack.MODE_STREAM);
            // Start playback
            audioTrack.play();
            // Write the music buffer to the AudioTrack object
            audioTrack.write(music, 0, musicLength);
            audioTrack.stop() ;
        } catch (Throwable t) {
            text.setText("playback failed");
        }
    }


    Runnable printDoge = new Runnable(){
        public void run(){
            text.setText("checking position of doge !!!!");
            //System.out.println("checking position of doge !!!!");
            if(right)
            {
                text.setText("doge face right");
                //setContentView(R.layout.dogeright);
            }
            else
            {
                text.setText("doge face left");
                //setContentView(R.layout.dogeleft);
            }
            handler2.postDelayed(printDoge, 1);
        }
    };


    class sendButtonListener implements View.OnClickListener
    {
        public void onClick(View v)
        {
            String[] reciver = new String[] { "wmao7@wisc.edu" };
            String[] mySbuject = new String[] { "test audio" };
            String myCc = "cc";
            String mybody = realData.toString();
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
            intent.putExtra(android.content.Intent.EXTRA_CC, myCc);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);

            intent.putExtra(Intent.EXTRA_STREAM, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm"));

            startActivity(Intent.createChooser(intent, "mail test"));

        }
    }


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
