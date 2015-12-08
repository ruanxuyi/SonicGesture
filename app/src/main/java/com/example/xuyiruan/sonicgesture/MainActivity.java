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
    public static final double Pi = 3.141592653589793238462643383279502884197;
    public static final double TWOPI =  Pi*2;
    public boolean start=true;
    public Button play;
    public Button sendEmail;
    public View view;
    public TextView text;
    public TextView statusText;
    public AudioTrack audioTrack;
    public int Hz;
    public int waveLen;
    public int length;
    public byte[] wave;
    public byte[] generatedSnd;
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
    public static int Count;
    public double intense1=0;
    public double intense2=0;
    public double dif=0;
    public double sig = 0;
    public String sig_str;
    public int band =2731 ;//3344

    public static boolean right;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Hz=14702;//real frequency 18001.75
        Hz = 14698;
        waveLen = 44100/ Hz;
        length = 44100*80;
        sampleRate=44100;
        wave = new byte[length];
        generatedSnd = new byte[2 * length];
        sin(wave, waveLen, length);
        //audioTrack.write(wave, 0, length);
        play = (Button)findViewById(R.id.button);
        play.setOnClickListener(new playButtonListener());

        sendEmail = (Button)findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new sendButtonListener());

        //view = findViewById(R.id.view);
        text =(TextView) findViewById(R.id.text);
        statusText = (TextView) findViewById(R.id.textView2);
        //bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = 8192;
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
            System.out.println("error");
        audioTrack=new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, // CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSnd.length,
                AudioTrack.MODE_STATIC);

        audioTrack.write(generatedSnd, 0, generatedSnd.length);
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
                Count=21;
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
                //cal();
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
                boolean detected = false;//NEWLY ADDED
                long lastTime = 0;

                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    for (int i = 0; i < bufferReadResult; i++)
                    {
                        //dos.writeShort(buffer[i]);
                        x[i] = new Complex(buffer[i], 0);
                    }



                    if(Count<2)
                    {
                        Count++;
                        System.out.print(Count);

                    }
                    else
                    {
                        realData=new ArrayList<Double>();
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

                        intense1=0;
                        intense2=0;
                        for(int i=band-30;i<band;i++)
                        {
                            intense1+=realData.get(i);

                            if(intense1<realData.get(i))
                                intense1=realData.get(i);

                        }
                        //TODO modified band calculation
                        for(int i=band+1;i<31+band;i++)
                        {
                            intense2+=realData.get(i);

                            if(intense2<realData.get(i))
                                intense2=realData.get(i);

                        }
                        intense1=intense1/30;
                        intense2=intense2/30;
                        dif=intense1/intense2;


                        if (intense1+intense2>=10000&&(!detected))
                        {
                            double dif_temp = dif;
                            //TODO Change Threshold of gesture
                            if (dif_temp < 1.25)//||dif>1.7)
                            {
                                lastTime = System.currentTimeMillis();

                                sig = dif;
                                sig_str = "closer"+dif;
                                detected = true;
                                //System.out.println("left");

                            } else if (dif_temp > 1.4)
                            {
                                lastTime = System.currentTimeMillis();

                                sig = dif;
                                sig_str = "further"+dif;
                                detected = true;
                                //System.out.println("right");
                            }
                            else
                            {
                                sig_str = "no gesture";
                            }

                        }
                        else if (intense1+intense2<10000)
                        {
                            sig_str = "no ultrasound";
                        }
                        if (System.currentTimeMillis()-lastTime>800&&detected)
                        {
                            System.out.println("here set false");
                            detected = false;
                        }

                        if(intense1>=1200)
                            right=!right;

                        Count=0;
                    }


                    /*
                    //realData=new ArrayList<Double>();
                    for (int i = 0; i < buffer.length; i++)
                    {
                        x[i] = new Complex(buffer[i], 0);
                    }
                    Complex[] y = fft(x);
                    for (int i = 0; i < y.length; i++) {
                        double value = y[i].abs();
                        //dos.writeDouble(value);
                        //realData.add(value);
                    }
                    */


                }
                audioRecord.stop();
                dos.close();
            } catch (Throwable t) {
                //text.setText("Recording Failed");
            }
        }
    };
    


    Runnable printDoge = new Runnable(){
        public void run(){
            //text.setText("checking position of doge !!!!");
            //System.out.println("checking position of doge !!!!");
            statusText.setText(sig_str);
            if (right) {
                text.setText("doge face right " +dif+"," +(int)intense1+","+(int)intense2);
                //setContentView(R.layout.dogeright);
            } else {
                text.setText("doge face left "+dif+"," +(int)intense1+","+(int)intense2);
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
            //intent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);

            //intent.putExtra(Intent.EXTRA_STREAM, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm"));

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
            double kth = -2 * k * Pi/ N;
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

    public void sin(byte[] wave, int waveLen, int length)
    {
        for (int i = 0; i < length; i++) {
            double angle = 2*Pi*i/waveLen;
            wave[i]=(byte)(Math.sin(angle)*127f);
            /*
            wave[i] = (byte) (HEIGHT * (1 - Math.sin(TWOPI
                    ((i % waveLen) * 1.00 / waveLen))));
                    */
        }
        int idx = 0;
        for (final double dVal : wave) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }

    }


}
