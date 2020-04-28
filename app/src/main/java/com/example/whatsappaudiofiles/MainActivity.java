package com.example.whatsappaudiofiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.whatsappaudiofiles.adapter.AudioAdapter;
import com.example.whatsappaudiofiles.jdo.Audio;
import com.facebook.stetho.Stetho;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioAdapter.AudioFileClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private List<Audio> mAudioList;
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekbar;
    private Handler mHandler;
    private Runnable mRunnable;
    private ImageView mPlayOrPause, mStop;
    private int mPosition;
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        Stetho.initializeWithDefaults(this);
        mRecyclerView = findViewById(R.id.recycler_view_audio);
        mAudioList = fetchListOfAudio();
        setAdapter();
    }

    private List<Audio> fetchListOfAudio() {
        return new DatabaseHelper(MainActivity.this).getAllAudio();
    }

    private void setAdapter() {
        AudioAdapter audioAdapter = new AudioAdapter(MainActivity.this, mAudioList, this);
        mRecyclerView.setAdapter(audioAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    private void playAudio() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mAudioList.get(mPosition).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });
    }

    private void stopAudio() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mHandler.removeCallbacks(mRunnable);
        mAlertDialog.dismiss();
    }

    @Override
    public void onAudioClick(int pPosition) {
        mPosition = pPosition;
        showAlert();
        playAudio();
        mSeekbar.setProgress(0);
        mSeekbar.setMax(mMediaPlayer.getDuration());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + mMediaPlayer.getCurrentPosition());
                mSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 1000);
            }
        };
        //mHandler.postDelayed(mRunnable, 0);

        MainActivity.this.runOnUiThread(mRunnable);
    }

    private void showAlert() {
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null, false);
        mAlertDialog = mAlertDialogBuilder.create();
        mSeekbar = view.findViewById(R.id.seekbar);
        mPlayOrPause = view.findViewById(R.id.play_or_pause);
        mStop = view.findViewById(R.id.stop);
        mAlertDialog.setView(view);
        mAlertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(mAlertDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 800;
        mAlertDialog.getWindow().setAttributes(layoutParams);

        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopAudio();
            }
        });
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
        mPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mPlayOrPause.setImageResource(R.drawable.play_icon);
                    } else {
                        mMediaPlayer.start();
                        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
                        mPlayOrPause.setImageResource(R.drawable.pause_icon);
                    }
                }
            }
        });

    }

}
