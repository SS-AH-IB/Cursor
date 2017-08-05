package com.sai.triode.cursor;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> arrayList,artList;
    ArrayAdapter<String> arrayAdapter;
    private static final int MY_PERMISSION_READ_EXTERNAL_STORAGE=1;
    ListView listView;
    ImageView imageView;
    //MediaMetadataRetriever mediaMetadataRetriever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView)findViewById(R.id.img);
        listView=(ListView)findViewById(R.id.lv);
        arrayList=new ArrayList<>();
        artList=new ArrayList<>();
        getMusic();
        getArt();
        Collections.sort(arrayList);
        Collections.sort(artList);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s=arrayList.get(i);
                String s1="",s2="";
                int flag=0;
                for(int j=7;j<s.length();j++)
                {
                    if(flag==1)
                        s1+=s.charAt(j);
                    else {
                        s2+=s.charAt(j);
                    }
                    if(s.charAt(j)=='$')
                        flag=1;
                }
                s2=s2.substring(0,s2.length()-1);
                Toast.makeText(getApplicationContext(),s2,Toast.LENGTH_SHORT).show();
                ContentResolver contentResolver=getContentResolver();
                Uri uri= MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor cursor=contentResolver.query(uri,new String[]{MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ALBUM_ART},"album=?",new String[]{s2},null);
                cursor.moveToFirst();
                int col=cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
                String s3=cursor.getString(col);
                Bitmap bm= BitmapFactory.decodeFile(/*s1*/s3);
                ImageView image=(ImageView)findViewById(R.id.img);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setImageBitmap(bm);
            }
        });
    }

    private void getArt() {
        ContentResolver contentResolver = getContentResolver();
        Uri albumArtUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumArtCursor =contentResolver.query(albumArtUri,null,null,null,null);
        if(albumArtCursor!=null&&albumArtCursor.moveToFirst()) {
            int albumArtCol = albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int albumCol=albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            do {
                String albumArt = albumArtCursor.getString(albumArtCol);
                String album=albumArtCursor.getString(albumCol);
                artList.add(album+"$"+albumArt);
            } while (albumArtCursor.moveToNext());
        }
    }

    public void getMusic(){
        ContentResolver contentResolver=getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=contentResolver.query(songUri,null,null,null,null);
        if(songCursor!=null && songCursor.moveToFirst())
        {
            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum=songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songBookmark=songCursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK);
            int songYear=songCursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
            int songData=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDateAdded=songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
            int songDisplayName=songCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int songDuration=songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songISMusic=songCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int songTrack=songCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            String preAlbum="";
            do {
                String currentTitle=songCursor.getString(songTitle);
                String currentArtist=songCursor.getString(songArtist);
                String cAlbum=songCursor.getString(songAlbum);
                String cBk=songCursor.getString(songBookmark);
                String cY=songCursor.getString(songYear);
                String cDa=songCursor.getString(songData);
                String cDaA=songCursor.getString(songDateAdded);
                String cDiN=songCursor.getString(songDisplayName);
                String cDu=songCursor.getString(songDuration);
                String cIM=songCursor.getString(songISMusic);
                String CTr=songCursor.getString(songTrack);

                if(!cAlbum.equals(preAlbum)) {
                    arrayList.add("Album: " + cAlbum + "$\nTitle: " + currentTitle + "\nArtist: " + currentArtist + "\nBookmark: " + cBk + "\nYear: " + cY + "\nData: " +
                            cDa + "\nDate Added: " + cDaA + "\nDisplay name: " + cDiN + "\nDuration: " + cDu + "\nTrack: " + CTr + "\nISMusic: " + cIM);
                    preAlbum = cAlbum;
                }
            }while (songCursor.moveToNext());
        }
    }
}
