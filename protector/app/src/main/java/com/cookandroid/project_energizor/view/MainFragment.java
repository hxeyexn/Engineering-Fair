package com.cookandroid.project_energizor.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cookandroid.project_energizor.R;

//MainFragment(메인페이지)
public class MainFragment extends Fragment {

    ViewGroup rootView;
    GridView gridView;

    int[] menuImage = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_main, container, false);
        gridView = rootView.findViewById(R.id.main_menu);

        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(getActivity(), menuImage);
        gridView.setAdapter(mainMenuAdapter);

        return rootView;
    }
}

