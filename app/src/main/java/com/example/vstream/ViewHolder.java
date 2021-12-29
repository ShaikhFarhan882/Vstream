package com.example.vstream;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;


public class ViewHolder extends RecyclerView.ViewHolder {

    TextView vTitle;
    PlayerView playerView;
    SimpleExoPlayer exoPlayer;



    public ViewHolder(@NonNull View itemView) {
        super(itemView);

    }

    public void setExoplayer(Application application, String videoTitle,final String videoURL){
        vTitle = itemView.findViewById(R.id.dashboardVideoTitle);
        playerView = itemView.findViewById(R.id.exoPlayerView);


        try
        {

            //Playing the video from firebase to the exoplayer.
            vTitle.setText(videoTitle);
            Uri videouri = Uri.parse(videoURL);
            exoPlayer = new SimpleExoPlayer.Builder(application).build();
            playerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(videouri);
            exoPlayer.addMediaItems(Collections.singletonList(mediaItem));
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(false);


        }

        catch (Exception ex){
            Log.d("Exoplayer Has Crashed",ex.getMessage().toString());
        }
    }


}
