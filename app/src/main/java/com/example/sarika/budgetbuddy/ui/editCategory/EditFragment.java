package com.example.sarika.budgetbuddy.ui.editCategory;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarika.budgetbuddy.R;
import com.example.sarika.budgetbuddy.ui.addCategory.AddFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditFragment extends Fragment {

    private EditViewModel mViewModel;
    private TextView updatebudget;
    private TextView edittext;
    private Button updatebutton;
    private Spinner spinner;
    private ProgressBar progressBar;
    String spinnertext;

    public static EditFragment newInstance() {
        return new EditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EditViewModel.class);
        Log.d("Doc", "mViewModel-" + mViewModel);
        mViewModel.getCat().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {
                progressBar.setVisibility(View.INVISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, mViewModel.cat.getValue());
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnertext = adapterView.getItemAtPosition(i).toString();
                        //int expense = mViewModel.getexpense(spinnertext);
                       // Toast.makeText(adapterView.getContext(), spinnertext, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
        // TODO: Use the ViewModel
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        updatebudget = view.findViewById(R.id.budget_update);
        edittext = view.findViewById(R.id.EditCategory);
        updatebutton = view.findViewById(R.id.update_button);
        spinner = view.findViewById(R.id.spinner1);
        progressBar=view.findViewById(R.id.progressBar2);
    }

    public void onStart() {
        super.onStart();
        mViewModel.getData();
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int budgettoupdate =0;
                if(updatebudget.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(), "Please enter a valid amount",Toast.LENGTH_LONG).show();
                else {
                    budgettoupdate=Integer.parseInt(updatebudget.getText().toString().trim());
                    mViewModel.updateData(spinnertext, budgettoupdate);
                    updatebudget.setText("");
                    Toast.makeText(getActivity(), "Category Updated!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


