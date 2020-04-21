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


        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getString(R.string.intro_one_title));
        sliderPage1.setDescription(getString(R.string.intro_one_desc));
        sliderPage1.setImageDrawable(R.drawable.intro_image_one);
        sliderPage1.setBgColor(getResources().getColor(R.color.material_blue));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.intro_second_title));
        sliderPage2.setDescription(getString(R.string.intro_second_desc));
        sliderPage2.setImageDrawable(R.drawable.intro_image_two);
        sliderPage2.setBgColor(getResources().getColor(R.color.material_red));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getString(R.string.done));
        sliderPage3.setDescription(getString(R.string.intro_third_desc));
        sliderPage3.setImageDrawable(R.drawable.intro_image_three);
        sliderPage3.setBgColor(getResources().getColor(R.color.material_green));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(this, FirebaseLogin.class));
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