package com.testing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    private static final int REQ_PICK_AUDIO = 1001;

    private LinearLayout rootLayout, playerPanel;
    private TextView tvEmpty, tvNow, tvTimeCurrent, tvTimeTotal, tvPlayingStatus, tvHeaderTitle, tvHeaderSub;
    private Button btnPlay, btnPrev, btnNext, btnTheme, btnOwner, btnPick;
    private SeekBar seekBar;
    private boolean isUserSeeking = false;
    private boolean isDarkMode = false;
    private SharedPreferences prefs;

    private final Handler ui = new Handler(Looper.getMainLooper());
    private final Runnable refresh = new Runnable() {
        @Override public void run() {
            updatePlayerUI();
            ui.postDelayed(this, 1000L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("appPrefs", MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("dark_mode", false);

        rootLayout     = (LinearLayout) findViewById(R.id.rootLayout);
        playerPanel    = (LinearLayout) findViewById(R.id.playerPanel);
        tvEmpty        = (TextView) findViewById(R.id.tvEmpty);
        tvNow          = (TextView) findViewById(R.id.tvNow);
        tvTimeCurrent  = (TextView) findViewById(R.id.tvTimeCurrent);
        tvTimeTotal    = (TextView) findViewById(R.id.tvTimeTotal);
        tvPlayingStatus= (TextView) findViewById(R.id.tvPlayingStatus);
        tvHeaderTitle  = (TextView) findViewById(R.id.tvHeaderTitle);
        tvHeaderSub    = (TextView) findViewById(R.id.tvHeaderSub);

        btnPlay        = (Button) findViewById(R.id.btnPlay);
        btnPrev        = (Button) findViewById(R.id.btnPrev);
        btnNext        = (Button) findViewById(R.id.btnNext);
        btnTheme       = (Button) findViewById(R.id.btnTheme);
        btnOwner       = (Button) findViewById(R.id.btnOwner);
        btnPick        = (Button) findViewById(R.id.btnPick);
        seekBar        = (SeekBar) findViewById(R.id.seekBar);

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                try {
                    startActivityForResult(intent, REQ_PICK_AUDIO);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "File picker tidak tersedia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                isDarkMode = !isDarkMode;
                prefs.edit().putBoolean("dark_mode", isDarkMode).apply();
                applyTheme();
            }
        });

        btnOwner.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showOwnerDialog();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (MusicService.instance != null) MusicService.instance.togglePlay();
                updatePlayerUI();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser) tvTimeCurrent.setText(fmt(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar sb) { isUserSeeking = true; }
            @Override public void onStopTrackingTouch(SeekBar sb) {
                isUserSeeking = false;
                if (MusicService.instance != null) MusicService.instance.seekTo(sb.getProgress());
            }
        });

        startService(new Intent(this, MusicService.class));
        applyTheme();

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2002);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception ignored) {}
                
                String fileName = queryName(uri);
                if (MusicService.instance != null) {
                    MusicService.instance.playUri(uri, fileName);
                }
                updatePlayerUI();
            }
        }
    }

    private String queryName(Uri uri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return cursor.getString(idx);
            }
        } catch (Exception ignored) {} finally { if (cursor != null) cursor.close(); }
        return "Lagu Terpilih";
    }

    private void showOwnerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_owner, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        instance = null;
        ui.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void applyTheme() {
        int bg = isDarkMode ? R.color.bgMainDark : R.color.bgMain;
        int textMain = isDarkMode ? R.color.textMainDark : R.color.textMain;
        int textMuted = isDarkMode ? R.color.textMutedDark : R.color.textMuted;

        rootLayout.setBackgroundColor(getResources().getColor(bg));
        playerPanel.setBackgroundResource(isDarkMode ? R.drawable.bg_player_panel_premium_dark : R.drawable.bg_player_panel_premium);
        
        tvNow.setTextColor(getResources().getColor(textMain));
        tvHeaderTitle.setTextColor(getResources().getColor(textMain));
        tvHeaderSub.setTextColor(getResources().getColor(textMuted));
        tvTimeCurrent.setTextColor(getResources().getColor(textMuted));
        tvTimeTotal.setTextColor(getResources().getColor(textMuted));
        tvEmpty.setTextColor(getResources().getColor(textMuted));

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(bg));
            if (Build.VERSION.SDK_INT >= 23) {
                View decor = window.getDecorView();
                if (isDarkMode) decor.setSystemUiVisibility(0);
                else decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        btnTheme.setText(isDarkMode ? "LIGHT" : "DARK");
    }

    private void updatePlayerUI() {
        MusicService svc = MusicService.instance;
        if (svc == null) return;

        boolean hasTrack = svc.getCurrentId() != -1;
        btnPlay.setEnabled(hasTrack);

        tvNow.setText(hasTrack ? svc.getCurrentTitle() : "No Track Selected");
        
        if (hasTrack) {
            boolean playing = svc.isPlaying();
            btnPlay.setText(playing ? "PAUSE" : "PLAY");
            tvPlayingStatus.setText(playing ? "Now Playing" : "Paused");
            tvPlayingStatus.setTextColor(getResources().getColor(playing ? R.color.colorPrimary : (isDarkMode ? R.color.textMutedDark : R.color.textMuted)));

            int cur = svc.getCurrentPosition();
            int total = svc.getDuration();
            if (!isUserSeeking) {
                seekBar.setMax(total);
                seekBar.setProgress(cur);
                tvTimeCurrent.setText(fmt(cur));
            }
            tvTimeTotal.setText(fmt(total));
        } else {
            btnPlay.setText("PLAY");
            tvPlayingStatus.setText("Pilih lagu lewat tombol PILIH LAGU");
            tvPlayingStatus.setTextColor(getResources().getColor(isDarkMode ? R.color.textMutedDark : R.color.textMuted));
            seekBar.setProgress(0);
            tvTimeCurrent.setText("00:00");
            tvTimeTotal.setText("00:00");
        }
    }

    private static String fmt(int ms) {
        if (ms < 0) ms = 0;
        int s = ms / 1000;
        return String.format("%02d:%02d", s / 60, s % 60);
    }

    @Override protected void onResume() { super.onResume(); ui.post(refresh); }
    @Override protected void onPause() { super.onPause(); ui.removeCallbacks(refresh); }
}
