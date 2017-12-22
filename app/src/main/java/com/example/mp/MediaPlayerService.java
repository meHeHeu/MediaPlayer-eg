package com.example.mp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;

public class MediaPlayerService extends Service {

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    private MediaPlayerBinder MP_Binder = new MediaPlayerBinder();

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public IBinder onBind(Intent intent) {
        return MP_Binder;
    }

    public void set(File song) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();
        } catch(IOException ie) {
            ie.printStackTrace();
        }
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public void playpause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}
