package com.example.xuyiruan.sonicgesture;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //initiate start/stop button
    private Button startBtn;
    //if startCount is odd, start to play music, if even, pause play music
    int startCount = 0;

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
                    mediaPlayer.start();
                }
            }
        });
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
