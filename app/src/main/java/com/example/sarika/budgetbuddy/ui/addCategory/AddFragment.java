package com.example.sarika.budgetbuddy.ui.addCategory;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarika.budgetbuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class AddFragment extends Fragment {

    private AddViewModel mViewModel;
    private TextView nameCategory;
    private TextView budgetAmount;
    private Button addButton;
    private TextView AddText;
    //private ProgressBar progressBar;
    FirebaseFirestore db;
    DocumentSnapshot document;
    private static final String TAG = "DocSnippets";
    public boolean flag;
    public Context context;

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //context=container.getContext();
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddViewModel.class);
        mViewModel.getBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //progressBar.setVisibility(View.INVISIBLE);
                if(aBoolean==true){
                    Toast.makeText(getActivity(), "Category already exists", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "In cat exists");
                }
                else
                {
                    Toast.makeText(getActivity(), "Category Added!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "In cat not exists");
                }
            }
        });
    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        nameCategory=view.findViewById(R.id.category_name);
        budgetAmount=view.findViewById(R.id.budget_amount);
        addButton=view.findViewById(R.id.add_button);
        AddText=view.findViewById(R.id.AddCategory);
        //progressBar=view.findViewById(R.id.progressBar);
    }
    @Override
    public void onStart() {
        super.onStart();
        //AddText.setText("In on start");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat_name = nameCategory.getText().toString().trim();
                //if(cat_name.equals(""))
                   // Toast.makeText(getActivity(),"Please enter a valid category name",Toast.LENGTH_LONG).show();
                int budget=0;
                if(budgetAmount.getText().toString().trim().equals("") || cat_name.equals("")) {
                    Toast.makeText(getActivity(), "Please enter valid data", Toast.LENGTH_LONG).show();
                }
                else {
                    budget = Integer.parseInt(budgetAmount.getText().toString().trim());
                    mViewModel.addData(cat_name, budget);
                    nameCategory.setText("");
                    budgetAmount.setText("");
                }
                Log.d(TAG,"flag val"+mViewModel.flag);
            }
            });
    }
}