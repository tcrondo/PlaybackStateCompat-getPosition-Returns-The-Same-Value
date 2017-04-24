package com.tcrondo.playbackstatecompatgetpositionreturnsthesamevalue;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MediaBrowserCompat browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        browser = new MediaBrowserCompat(this,
                new ComponentName(this, MainService.class),
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        try {
                            printPlaybackPositionEverySecond();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
        browser.connect();
    }

    private void printPlaybackPositionEverySecond() throws RemoteException {
        final MediaControllerCompat mediaController = new MediaControllerCompat(this, browser.getSessionToken());
        mediaController.getTransportControls().play();
        disposables.add(Observable.interval(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                Log.d("Main", "current position: " + mediaController.getPlaybackState().getPosition());
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        browser.disconnect();
    }
}
