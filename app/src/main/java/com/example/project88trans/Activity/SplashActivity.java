package com.example.project88trans.Activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        ImageView companyName = findViewById(R.id.companyName);

        // Posisi awal: kecil, transparan, di bawah
        logo.setVisibility(View.VISIBLE);
        logo.setScaleX(0.3f);
        logo.setScaleY(0.3f);
        logo.setAlpha(0f);
        logo.setTranslationY(500f); // mulai jauh di bawah

        // ⿡ Naik tinggi sambil membesar + fade in
        ObjectAnimator riseAndGrow = ObjectAnimator.ofPropertyValuesHolder(
                logo,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -150f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f)
        );
        riseAndGrow.setDuration(900);

        // ⿢ Jatuh dengan bounce
        ObjectAnimator fallBounce = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, 0f);
        fallBounce.setInterpolator(new BounceInterpolator());
        fallBounce.setDuration(1000);

        // ⿣ Geser logo ke kiri
        ObjectAnimator moveLeft = ObjectAnimator.ofFloat(logo, View.TRANSLATION_X, -200f);
        moveLeft.setDuration(600);

        // ⿤ Nama perusahaan masuk dari kanan + fade in
        AnimatorSet textIn = new AnimatorSet();
        textIn.playTogether(
                ObjectAnimator.ofFloat(companyName, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(companyName, View.TRANSLATION_X, 400f, -150f)
        );
        textIn.setDuration(800);

        // Jalankan animasi berurutan
        AnimatorSet sequence = new AnimatorSet();
        sequence.playSequentially(riseAndGrow, fallBounce, moveLeft, textIn);
        sequence.start();

        sequence.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
   });
}
}