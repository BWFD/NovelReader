package com.example.novelreader.Piaotain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.novelreader.R;
import com.example.novelreader.dao.PiaotianClassification;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PiaotianSearchFragment extends Fragment {
    View view;
    private Button btnShowMenu;

    private LinearLayout listView;
    private ScrollView scrollView;
    private List<PiaotianClassification> dataList= new ArrayList<>();
    private boolean notLoading;
    private int page;
    Spinner spinner;
    String classification;
    EditText editText;
    TextView nofound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_piaotian_search, container, false);
        notLoading = false;
        page = 1;

        listView = view.findViewById(R.id.searchList);
        listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        editText = view.findViewById(R.id.searchText);
        nofound = view.findViewById(R.id.NoFound);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 處理搜尋邏輯
                    nofound.setVisibility(View.INVISIBLE);
                    if(String.valueOf(editText.getText()).length() < 2) {
                        Toast.makeText(getActivity(), "請輸入至少兩個字元", Toast.LENGTH_LONG).show();
                    }
                    else {
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        }

                        listView.removeAllViews();
                        getListAndUpdate();
                        notLoading = false;
                        page = 1;
                    }

                    // 執行搜尋操作
                    return true;
                }
                return false;
            }
        });


        String[] items = {"書名", "作者"};

        spinner = view.findViewById(R.id.piaotianSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("書名")) {
                    classification = "articlename";
                }
                else {
                    classification = "author";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 不選擇任何項目時的行為
            }
        });

        Button submitButton = view.findViewById(R.id.searchSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nofound.setVisibility(View.INVISIBLE);
                if(String.valueOf(editText.getText()).length() < 2) {
                    Toast.makeText(getActivity(), "請輸入至少兩個字元", Toast.LENGTH_LONG).show();
                }
                else {

                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }

                    listView.removeAllViews();
                    getListAndUpdate();
                    notLoading = false;
                    page = 1;
                }
            }
        });

        scrollView = view.findViewById(R.id.searchScroll);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
                if(notLoading) {
                    getListAndUpdate();
                    notLoading = false;
                }
            }
        });

        return view;
    }

    public void getListAndUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String keyword = String.valueOf(editText.getText());

                if(!keyword.equals("")) {
                    try {
                        dataList = Piaotian.getSearch(keyword, classification, page);
                        //dataList.addAll(Piaotian.getMonthRank(page));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!dataList.isEmpty()) {
                            dataList.forEach(piaotianClassification -> {
                                Button button = new Button(getActivity());
                                View bar = new View(getActivity());
                                button.setText(piaotianClassification.getName() + "    作者: " + piaotianClassification.getAuthor());
                                button.setTextColor(Color.WHITE);
                                button.setTextSize(20);
                                button.setBackgroundColor(Color.TRANSPARENT);
                                button.setPadding(10,0,0,0);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                button.setGravity(Gravity.CENTER_VERTICAL);
                                button.setLayoutParams(params);


                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), PiaotianBookInfoActivity.class);
                                        intent.putExtra("URL",piaotianClassification.getHtml());
                                        startActivity(intent);
                                    }
                                });
                                listView.addView(button);

                                int color = ContextCompat.getColor(requireActivity(), R.color.light_blue_600);
                                bar.setBackgroundColor(color);
                                params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        6
                                );
                                bar.setLayoutParams(params);
                                listView.addView(bar);
                            });
                        }
                        else {
                            if(page == 1) {
                                nofound.setVisibility(View.VISIBLE);
                            }
                        }

                        page = page  + 1;
                        notLoading = true;
                        dataList = null;
                    }
                });
            }
        }).start();
    }

}