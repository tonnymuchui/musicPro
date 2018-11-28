package com.musicpro.musicpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btn_next,btn_previous,btn_pause;
    TextView songTextLable;
    SeekBar seekBar;

    static MediaPlayer myMediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekBar;
    String sName;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    btn_next = (Button)findViewById(R.id.next);
    btn_pause = (Button)findViewById(R.id.pause);
    btn_previous = (Button)findViewById(R.id.previou);
    songTextLable = (TextView)findViewById(R.id.songLable);
    seekBar = (SeekBar)findViewById(R.id.seekBar);

    getSupportActionBar().setTitle("Now playing");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    updateSeekBar = new Thread(){

        @Override
        public void run() {

            int totalDuration = myMediaPlayer.getDuration();
            int currentPosition = 0;

            while (currentPosition<totalDuration){
                try {

                    sleep(500);
                    currentPosition = myMediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }


            }

        }
    };

    if (myMediaPlayer!=null){

        myMediaPlayer.stop();
        myMediaPlayer.release();

    }

        Intent intent = getIntent();
    Bundle bundle = intent.getExtras();

    mySongs =(ArrayList) bundle.getParcelableArrayList("songs");
    sName = mySongs.get(position).getName().toString();
    String songName = intent.getStringExtra("songName");

    songTextLable.setText(songName);
    songTextLable.setSelected(true);
    position = bundle.getInt("pos",0);

        Uri uri = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        myMediaPlayer.start();
        seekBar.setMax(myMediaPlayer.getDuration());
        updateSeekBar.start();

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

       btn_pause.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               seekBar.setMax(myMediaPlayer.getDuration());
               if (myMediaPlayer.isPlaying()){
                   btn_pause.setBackgroundResource(R.drawable.icon_play);
                   myMediaPlayer.pause();
               }else {
                   btn_pause.setBackgroundResource(R.drawable.pause);
                   myMediaPlayer.start();
               }
           }
       });

       btn_next.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               myMediaPlayer.stop();
               myMediaPlayer.release();
               position = ((position+1)%mySongs.size());

               Uri u = Uri.parse(mySongs.get(position).toString());
               myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
               sName = mySongs.get(position).getName().toString();
               songTextLable.setText(sName);
               myMediaPlayer.start();
           }
       });

       btn_previous.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               myMediaPlayer.stop();
               myMediaPlayer.release();

               position = ((position-1)<0)?(mySongs.size()-1):(position-1);
               Uri u = Uri.parse(mySongs.get(position).toString());
               myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
               sName = mySongs.get(position).getName().toString();
               songTextLable.setText(sName);
               myMediaPlayer.start();
           }
       });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}
