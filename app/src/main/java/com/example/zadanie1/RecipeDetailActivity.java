package com.example.zadanie1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Locale;
import android.media.MediaPlayer;
import android.widget.Button;

public class RecipeDetailActivity extends AppCompatActivity {
    private Button btnPlayVoice;
    private MediaPlayer voicePlayer;
    private TextView tvName, tvIngredients, tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ПРИМЕНЯЕМ СОХРАНЕННЫЙ ЯЗЫК ПЕРЕД setContentView
        applySavedLanguage();

        setContentView(R.layout.activity_recipe_detail);

        tvName = findViewById(R.id.tvDetailName);
        tvIngredients = findViewById(R.id.tvDetailIngredients);
        tvSteps = findViewById(R.id.tvDetailSteps);

        // ПЕРВЫЙ intent - для получения данных
        Intent intent = getIntent();
        int nameId = intent.getIntExtra("name_id", 0);
        int ingredientsId = intent.getIntExtra("ingredients_id", 0);
        int stepsId = intent.getIntExtra("steps_id", 0);

        tvName.setText(nameId);
        tvIngredients.setText(ingredientsId);
        tvSteps.setText(stepsId);

        btnPlayVoice = findViewById(R.id.btnPlayRecipeVoice);
        btnPlayVoice.setOnClickListener(v -> playRecipeVoice());

        Button btnWatchVideo = findViewById(R.id.btnWatchVideo);
        btnWatchVideo.setOnClickListener(v -> {
            // ИСПРАВЛЕНО: используем другое имя - videoIntent
            Intent videoIntent = new Intent(RecipeDetailActivity.this, VideoActivity.class);
            startActivity(videoIntent);
        });
    }

    private void playRecipeVoice() {
        String languageCode = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("app_language", "en");

        int voiceResId;
        switch (languageCode) {
            case "ru":
                voiceResId = R.raw.omlet_ru;
                break;
            case "de":
                voiceResId = R.raw.omlet_de;
                break;
            default:
                voiceResId = R.raw.omlet_en;
                break;
        }

        // Используем обновленный SoundManager
        SoundManager.playSound(this, voiceResId);


        // Останавливаем предыдущее воспроизведение
        if (voicePlayer != null) {
            voicePlayer.release();
        }

        // Воспроизводим
        voicePlayer = MediaPlayer.create(this, voiceResId);
        voicePlayer.setOnCompletionListener(mp -> {
            mp.release();
            voicePlayer = null;
        });
        voicePlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voicePlayer != null) {
            voicePlayer.release();
            voicePlayer = null;
        }
        SoundManager.stopSound(); // Останавливаем фоновые звуки
    }

    private void applySavedLanguage() {
        String languageCode = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("app_language", "en");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}