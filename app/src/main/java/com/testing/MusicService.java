package com.testing;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {

    public static MusicService instance;
    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIF_ID = 1;

    public static final String ACTION_PLAY_PAUSE = "com.testing.ACTION_PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.testing.ACTION_NEXT";
    public static final String ACTION_PREV = "com.testing.ACTION_PREV";
    public static final String ACTION_SEEK_BACK = "com.testing.ACTION_SEEK_BACK";
    public static final String ACTION_SEEK_FWD = "com.testing.ACTION_SEEK_FWD";

    private MediaPlayer player;
    private AudioManager audioManager;
    private long currentId = -1;
    private String currentTitle = "";
    private String currentArtist = "";
    private NotificationReceiver receiver;

    private final Handler notifHandler = new Handler(Looper.getMainLooper());
    private final Runnable notifUpdater = new Runnable() {
        @Override public void run() {
            if (player != null && currentId != -1) {
                updateNotificationContent();
                notifHandler.postDelayed(this, 1000L);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(channel);
        }

        receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY_PAUSE);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_SEEK_BACK);
        filter.addAction(ACTION_SEEK_FWD);
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_ID, buildNotification());
        return START_STICKY;
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) pendingFlags |= PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, pendingFlags);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_player);

        boolean playing = isPlaying();
        views.setTextViewText(R.id.notifTitle, currentTitle.isEmpty() ? "Music Player" : currentTitle);
        views.setTextViewText(R.id.notifArtist, currentArtist.isEmpty() ? "Siap memutar" : currentArtist);
        views.setImageViewResource(R.id.btnNotifPlay, playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);

        int cur = getCurrentPosition();
        int total = getDuration();
        if (total > 0) views.setProgressBar(R.id.notifProgress, total, cur, false);

        PendingIntent pPlayPause = PendingIntent.getBroadcast(this, 1, new Intent(ACTION_PLAY_PAUSE), pendingFlags);
        PendingIntent pNext = PendingIntent.getBroadcast(this, 2, new Intent(ACTION_NEXT), pendingFlags);
        PendingIntent pSeekBack = PendingIntent.getBroadcast(this, 3, new Intent(ACTION_SEEK_BACK), pendingFlags);
        PendingIntent pSeekFwd = PendingIntent.getBroadcast(this, 4, new Intent(ACTION_SEEK_FWD), pendingFlags);

        views.setOnClickPendingIntent(R.id.btnNotifPlay, pPlayPause);
        views.setOnClickPendingIntent(R.id.btnNotifNext, pNext);
        views.setOnClickPendingIntent(R.id.btnSeekBack, pSeekBack);
        views.setOnClickPendingIntent(R.id.btnSeekFwd, pSeekFwd);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setCustomContentView(views)
                .setCustomBigContentView(views)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pi)
                .setOngoing(playing)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotificationContent() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(NOTIF_ID, buildNotification());
    }

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_PLAY_PAUSE.equals(action)) togglePlay();
            else if (ACTION_SEEK_BACK.equals(action)) { seekTo(Math.max(0, getCurrentPosition() - 10000)); updateNotificationContent(); }
            else if (ACTION_SEEK_FWD.equals(action)) { seekTo(Math.min(getDuration(), getCurrentPosition() + 10000)); updateNotificationContent(); }
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }
    public long getCurrentId() { return currentId; }
    public String getCurrentTitle() { return currentTitle; }
    public int getCurrentPosition() { try { return player != null ? player.getCurrentPosition() : 0; } catch (Exception e) { return 0; } }
    public int getDuration() { try { return player != null ? player.getDuration() : 0; } catch (Exception e) { return 0; } }
    public boolean isPlaying() { try { return player != null && player.isPlaying(); } catch (Exception e) { return false; } }
    public void seekTo(int ms) { try { if (player != null) player.seekTo(ms); } catch (Exception ignored) { } }

    // ULTRA SIMPLE: Terima URI langsung dari SAF
    public void playUri(Uri uri, String title) {
        if (player != null) {
            try { player.release(); } catch (Exception ignored) {}
            player = null;
        }

        try {
            player = MediaPlayer.create(getApplicationContext(), uri);
            if (player == null) {
                Toast.makeText(this, "Gagal memutar file", Toast.LENGTH_SHORT).show();
                return;
            }

            player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override public void onCompletion(MediaPlayer mp) {
                    try { mp.seekTo(0); } catch (Exception ignored) {}
                }
            });

            player.start();
            currentId = 1;
            currentTitle = title;
            currentArtist = "Lagu Lokal";
            
            notifHandler.removeCallbacks(notifUpdater);
            notifHandler.post(notifUpdater);
            updateNotificationContent();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void togglePlay() {
        if (player == null) return;
        try {
            if (player.isPlaying()) {
                player.pause();
                notifHandler.removeCallbacks(notifUpdater);
            } else {
                player.start();
                notifHandler.post(notifUpdater);
            }
            updateNotificationContent();
        } catch (Exception ignored) { }
    }

    @Override
    public void onDestroy() {
        notifHandler.removeCallbacksAndMessages(null);
        if (receiver != null) unregisterReceiver(receiver);
        if (player != null) {
            try { player.reset(); } catch (Exception ignored) {}
            try { player.release(); } catch (Exception ignored) {}
            player = null;
        }
        instance = null;
        super.onDestroy();
    }
}
