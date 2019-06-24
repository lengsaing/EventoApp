package com.app.sangleng.evento;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class AuthenticateActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // create an instance of pager fragment adapter
        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());

        // Add login & register fragments to pager fragment adapter
        pagerAdapter.addFragment(new LoginFragment());
        pagerAdapter.addFragment(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);
    }
}