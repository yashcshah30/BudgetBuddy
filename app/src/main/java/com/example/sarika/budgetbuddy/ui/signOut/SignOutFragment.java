package com.example.sarika.budgetbuddy.ui.signOut;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarika.budgetbuddy.R;
import com.example.sarika.budgetbuddy.Welcome;
import com.google.firebase.auth.FirebaseAuth;

public class SignOutFragment extends Fragment {

    private SignOutViewModel mViewModel;

    public static SignOutFragment newInstance() {
        return new SignOutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_out, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SignOutViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signOut();
        Intent goToWelcome= new Intent(getActivity(), Welcome.class);
        goToWelcome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToWelcome);
    }
}
