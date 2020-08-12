package com.example.sarika.budgetbuddy.ui.deleteCategory;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarika.budgetbuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeleteFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinner;
    static String spinnertext;
    Button deletebutton;
    private TextView deletetext;
    private ProgressBar progressBar;

    private DeleteViewModel mViewModel;
    String tag=" ";
    String msg=" ";
    public String Uid;
    private FirebaseAuth mAuth;
    ArrayAdapter<String> adapter;
    public DeleteFragment()
    {
        mAuth=FirebaseAuth.getInstance();
        Uid=mAuth.getUid();
    }

    public static DeleteFragment newInstance() {
        return new DeleteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DeleteViewModel.class);
        // TODO: Use the ViewModel
        Log.d("Doc", "mViewModel-" + mViewModel);
        mViewModel.getCat().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, mViewModel.cat.getValue());
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

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnertext = adapterView.getItemAtPosition(i).toString();
                //int expense = mViewModel.getexpense(spinnertext);
                // Toast.makeText(adapterView.getContext(), spinnertext, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        deletebutton = view.findViewById(R.id.delete_button);
        deletetext = view.findViewById(R.id.DeleteCategory);
        spinner = view.findViewById(R.id.spinner2);
        progressBar=view.findViewById(R.id.progressBar3);
        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String str = spinnertext;
                DocumentReference doc = FirebaseFirestore.getInstance().document("/Users/"+Uid);
                doc.collection("Categories").document(str).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), str+" Deleted", Toast.LENGTH_SHORT).show();
                        //mViewModel.getData();

                    }
                });
                Map<String,Object> updates= new HashMap<>();
                updates.put(str, FieldValue.delete());
                db.collection("Users").document(Uid).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            //Toast.makeText(getContext(), str + " Deleted from uid", Toast.LENGTH_SHORT).show();
                            mViewModel.getData();
                            //adapter.remove(str);
                            spinner.setAdapter(adapter);
                        }
                        else
                            //Toast.makeText(getContext(), str+" nahi hua"+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.d("doc","task fail" +task.getException());
                    }
                });


            }
        });


    }
    public void onStart() {
        super.onStart();
        mViewModel.getData();
        /*deletebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                db.collection("Category").document().delete();
            }
        });*/
    }
}
