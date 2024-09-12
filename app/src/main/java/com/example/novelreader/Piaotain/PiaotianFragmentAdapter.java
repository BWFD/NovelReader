package com.example.novelreader.Piaotain;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PiaotianFragmentAdapter extends FragmentStateAdapter  {

    public PiaotianFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PiaotianClassificationFragment();
            case 1:
                return new PiaotianMonthRankedFragment();
            case 2:
                return new PiaotianSearchFragment();
            default:
                return new PiaotianClassificationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

