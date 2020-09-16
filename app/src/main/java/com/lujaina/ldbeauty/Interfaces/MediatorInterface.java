package com.lujaina.ldbeauty.Interfaces;


import androidx.fragment.app.Fragment;

public interface MediatorInterface {
    void changeFragmentTo(Fragment fragmentToDisplay, String fragmentTag);

    void onBackPressed();
}