package com.example.mp;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;


public class LoadSongsAsyncTask extends AsyncTask<File, String, ArrayList<File>> {

    private MainActivity mainActivity;

    private ArrayList<File> songFileList = new ArrayList<File>();


    public LoadSongsAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    /*
    @Override
    protected void onPreExecute() {

    }
    */

    @Override
    protected ArrayList<File> doInBackground(File ...args) {

        for (File arg : args)
            songFileList.addAll(Load.FileListByExtension(arg, mainActivity.songFileExtensions));

        return songFileList;
    }

    /*
    @Override
    protected void onProgressUpdate(String ...args) {

    }
    */

    @Override
    protected void onPostExecute(ArrayList<File> songFileList) {
        super.onPostExecute(songFileList);
        mainActivity.songFileList = songFileList;
        mainActivity.songNameArray = new String[songFileList.size()];
        for (int i=0; i<songFileList.size(); ++i) {
            mainActivity.songNameArray[i] = songFileList.get(i).getName();
            Log.i("onPostExecute", songFileList.get(i).getName());
        }

        mainActivity.songNameArrayAdapter = new ArrayAdapter<String>(mainActivity.getApplicationContext(), R.layout.songname_list_item, R.id.list_item, mainActivity.songNameArray);
        mainActivity.songListView.setAdapter(mainActivity.songNameArrayAdapter);
    }
}
