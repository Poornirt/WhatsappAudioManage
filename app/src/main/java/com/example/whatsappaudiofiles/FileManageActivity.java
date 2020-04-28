package com.example.whatsappaudiofiles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.whatsappaudiofiles.adapter.AudioAdapter;
import com.example.whatsappaudiofiles.jdo.Audio;
import com.facebook.stetho.Stetho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileManageActivity extends AppCompatActivity{

    private static final String TAG = "FileManageActivity";
    private String mNameOfAudio;
    private List<Audio> mAudioList;
    private Audio mAudio;
    private TextView mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);
        mMessage = findViewById(R.id.message);
        mAudio = new Audio();
        mAudioList = new ArrayList<>();
        if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
            Uri receiverUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            Log.d(TAG, "Uri: " + receiverUri);
            getFilePathFromUri(receiverUri);
        }
        showAlertDialog();
    }


    private void showAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setPadding(10, 10, 10, 10);
        alert.setView(editText);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNameOfAudio = editText.getText().toString();
                mAudio.setName(mNameOfAudio);
                mAudioList.add(mAudio);
                new DatabaseHelper(FileManageActivity.this).insertContacts(mAudioList);
                mMessage.setText("Successfully Added");
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }


    private void getFilePathFromUri(Uri pUri) {
        String lFileExtension;
        InputStream lInputStream = null;
        try {
            lInputStream = getContentResolver().openInputStream(pUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lFileExtension = getContentResolver().getType(pUri);
        File lFilePath = createTempFile(lInputStream, lFileExtension);
        Date date = new Date(lFilePath.lastModified());
        mAudio.setDate(String.valueOf(date));
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this,Uri.parse(lFilePath.getAbsolutePath()));
        mAudio.setDuration(generateMinutesAndSeconds(Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));
        Log.d(TAG, "Uri: " + lFilePath.getAbsolutePath());
    }


    private String generateMinutesAndSeconds(long pDuration){
        String lDurationOfAudio="";
        long minutes=(pDuration/1000)/60;
        int seconds= (int) ((pDuration/1000)%60);
        lDurationOfAudio+=minutes;
        lDurationOfAudio+=":";
        lDurationOfAudio+=seconds;
        return lDurationOfAudio;
    }

    private File createTempFile(InputStream pInputStream, String pFileExtension) {
        int read;
        File lFile = new File(getExternalCacheDir(), "LocalAudio"+ System.currentTimeMillis() + ".ogg");
        byte[] byteT = new byte[1024];
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(lFile);
            while ((read = pInputStream.read(byteT)) != -1) {
                outputStream.write(byteT, 0, read);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mAudio.setPath(String.valueOf(lFile));
        return lFile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }
}
