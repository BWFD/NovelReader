package com.example.novelreader.hjwzw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class hjwzwFragmentAdapter extends FragmentStateAdapter {
    public hjwzwFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new hjwzwClassificationFragment();
            case 1:
                return new hjwzwSearchFragment();
            default:
                return new hjwzwClassificationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
