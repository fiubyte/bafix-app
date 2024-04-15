package com.fiubyte.bafix.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationManager {
    public static void showAndFadeOutView(final Context context, final View view, int duration) {
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000);

        Animation fadeOutAnimation = new AlphaAnimation(1, 0);
        fadeOutAnimation.setDuration(1000);

        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeInAnimation);

        view.postDelayed(() -> {
            view.startAnimation(fadeOutAnimation);
            view.setVisibility(View.GONE);
        }, duration);
    }
}
