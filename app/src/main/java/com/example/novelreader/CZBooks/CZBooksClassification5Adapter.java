package com.example.novelreader.CZBooks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CZBooksClassification5Adapter extends FragmentStateAdapter {
    String url;

    public CZBooksClassification5Adapter(@NonNull FragmentActivity fragmentActivity,String Url) {
        super(fragmentActivity);
        this.url = Url;
    }

    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CZBooksBookListFragment.classificationNew(url);
            case 1:
                return CZBooksBookListFragment.classificationDay(url);
            case 2:
                return CZBooksBookListFragment.classificationWeek(url);
            case 3:
                return CZBooksBookListFragment.classificationTotal(url);
            case 4:
                return CZBooksBookListFragment.classificationFinish(url);
            default:
                return CZBooksBookListFragment.classificationNew(url);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
