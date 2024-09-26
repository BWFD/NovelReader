package com.example.novelreader.CZBooks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class CZBooksFragmentAdapter extends FragmentStateAdapter  {

    public CZBooksFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CZBooksClassificationFragment();
            case 1:
                return new CZBooksSearchFragment();
            default:
                return new CZBooksClassificationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
