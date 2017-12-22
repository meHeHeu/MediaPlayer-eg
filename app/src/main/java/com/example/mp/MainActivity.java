package com.example.mp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    /* Fields */

    private final int STORAGE_PERMISSION_CODE = 1;

    public String[] songFileExtensions = {".flac", ".mp3", ".mp4", ".ogg", ".wav"};

    ArrayList<File> songFileList;
    String[] songNameArray;
    ArrayAdapter<String> songNameArrayAdapter;

    // service
    private MediaPlayerService mediaPlayer = new MediaPlayerService();

    // needed for seekBar update
    Handler handler;
    Runnable runnable;

    // interface fields
    @BindView(R.id.playpauseButton)
    Button playpauseButton;
    @BindView(R.id.stopButton)
    Button stopButton;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.songNameTV)
    TextView songNameTV;
    @BindView(R.id.songListView)
    ListView songListView;


    /* Activity life cycle settings */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mediaPlayer.set(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.set(seekBar.getProgress());
            }
        });

        handler = new Handler();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MainActivity.this, "Permission already granted!", Toast.LENGTH_SHORT).show();
        else
            requestStoragePermission();

        LoadSongsAsyncTask lsat = new LoadSongsAsyncTask(this);
        lsat.execute(Environment.getExternalStorageDirectory());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }


    /* Service settings */

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mediaPlayer = ((MediaPlayerService.MediaPlayerBinder)service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mediaPlayer = null;
    }


    /* Interface callbacks */

    @OnClick(R.id.playpauseButton)
    public void playpauseButton_OnClick(Button b) {
        if(songNameTV.getText().equals(""))
            return;
        if (mediaPlayer.isPlaying())
            b.setText(R.string.playLabel);
        else
            b.setText(R.string.pauseLabel);
        mediaPlayer.playpause();
        playCycle();
    }

    @OnClick(R.id.stopButton)
    public void stopButton(Button b) {
        songNameTV.setText(R.string.none);
        playpauseButton.setText(R.string.playLabel);
        seekBar.setProgress(0);
        mediaPlayer.reset();
    }

    @OnItemClick(R.id.songListView)
    public void songListView_OnItemClick(int position) {
        String songName = (String)songListView.getItemAtPosition(position);

        Toast.makeText(MainActivity.this, songName, Toast.LENGTH_SHORT).show();

        songNameTV.setText(songName);
        boolean isPlaying = mediaPlayer.isPlaying();
        mediaPlayer.set(songFileList.get(position));
        seekBar.setMax(mediaPlayer.getDuration());
        if(isPlaying)
            mediaPlayer.playpause();
    }

    public void playCycle() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }


    /* Permissions */

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission needed, because this example is about it")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
