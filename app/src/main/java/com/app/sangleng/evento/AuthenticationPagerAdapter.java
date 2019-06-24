package com.app.sangleng.evento;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class AuthenticationPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public AuthenticationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
