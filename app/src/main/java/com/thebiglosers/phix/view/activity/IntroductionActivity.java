package com.thebiglosers.phix.view.activity;


import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.thebiglosers.phix.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IntroductionActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Welcome to Phix");
        sliderPage1.setDescription("Your personal money management app ");
        sliderPage1.setImageDrawable(R.drawable.intro_image_one);
        sliderPage1.setBgColor(getResources().getColor(R.color.material_blue));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Fast Transactions");
        sliderPage2.setDescription("Phix helps you settle your bills damn fast!");
        sliderPage2.setImageDrawable(R.drawable.intro_image_two);
        sliderPage2.setBgColor(getResources().getColor(R.color.material_red));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Done");
        sliderPage3.setDescription("That's enough, lets start!");
        sliderPage3.setImageDrawable(R.drawable.intro_image_three);
        sliderPage3.setBgColor(getResources().getColor(R.color.material_green));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        // OPTIONAL METHODS
        // Override bar/separator color.

//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
//        showSkipButton(false);
//        setButtonsEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this, FirebaseLogin.class));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
}