package com.example.mp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private final int STORAGE_PERMISSION_CODE = 1;

    public String[] songFileExtensions = {".flac", ".mp3", ".mp4", ".ogg", ".wav"};

    ArrayList<File> songFileList;
    String[] songNameArray;
    ArrayAdapter<String> songNameArrayAdapter;

    private MediaPlayerService mediaPlayer = new MediaPlayerService();

    @BindView(R.id.playpauseButton)
    Button playpauseButton;
    @BindView(R.id.stopButton)
    Button stopButton;
    @BindView(R.id.seekBar)
    ProgressBar seekBar;
    @BindView(R.id.songNameTV)
    TextView songNameTV;
    @BindView(R.id.songListView)
    ListView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MainActivity.this, "Permission already granted!", Toast.LENGTH_SHORT).show();
        else
            requestStoragePermission();

        LoadSongsAsyncTask lsat = new LoadSongsAsyncTask(this);
        lsat.execute(Environment.getExternalStorageDirectory());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mediaPlayer = ((MediaPlayerService.MediaPlayerBinder)service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mediaPlayer = null;
    }

    @OnClick(R.id.playpauseButton)
    public void playpauseButton_OnClick(Button b) {
        if (mediaPlayer.isPlaying())
            b.setText(R.string.playLabel);
        else
            b.setText(R.string.pauseLabel);
        mediaPlayer.playpause();
    }

    @OnClick(R.id.stopButton)
    public void stopButton(Button b) {
        songNameTV.setText(R.string.none);
        playpauseButton.setText(R.string.playLabel);
        mediaPlayer.reset();
    }

    @OnItemClick(R.id.songListView)
    public void songListView_OnItemClick(int position) {
        String t = (String)songListView.getItemAtPosition(position);
        Toast.makeText(MainActivity.this, t, Toast.LENGTH_SHORT).show();
        songNameTV.setText(t);
        playpauseButton.setText(R.string.playLabel);
        mediaPlayer.set(songFileList.get(position));
    }

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
