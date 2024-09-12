package com.example.novelreader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainFragmentAdapter extends FragmentStateAdapter {

    public MainFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position) {
           case 0:
               return new BookMarkFragment();
           case 1:
               return new NovelWebSiteFragment();
           default:
               return new BookMarkFragment();
       }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
