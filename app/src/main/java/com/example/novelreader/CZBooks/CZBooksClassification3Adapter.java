package com.example.novelreader.CZBooks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CZBooksClassification3Adapter extends FragmentStateAdapter {

    String url;

    public CZBooksClassification3Adapter(@NonNull FragmentActivity fragmentActivity, String Url) {
        super(fragmentActivity);
        this.url = Url;
    }

    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CZBooksBookListFragment.classificationDay(url);
            case 1:
                return CZBooksBookListFragment.classificationWeek(url);
            case 2:
                return CZBooksBookListFragment.classificationTotal(url);
            default:
                return CZBooksBookListFragment.classificationDay(url);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
