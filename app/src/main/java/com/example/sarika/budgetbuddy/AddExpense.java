package com.example.sarika.budgetbuddy;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddExpense extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String Uid;
    private TextView description;
    private TextView spent;
    private Spinner spinner;
    private Button add;
    private ProgressBar progressBar;
    private static final String TAG = "DocSnippets";
    private String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        description = findViewById(R.id.description);
        spent = findViewById(R.id.spentAmount);
        spinner = findViewById(R.id.spinner);
        add = findViewById(R.id.addExpButton);
        progressBar=findViewById(R.id.progressBar);

        db.collection("Users").document(Uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnap = task.getResult();
                            if (documentSnap.exists()) {
                                Map<String, Object> map = documentSnap.getData();
                                categories = new String[map.size()];
                                int i = 0;
                                for (String key : map.keySet())
                                    categories[i++] = key;
                                makeSpinner();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = description.getText().toString().trim();
                String category = spinner.getSelectedItem().toString();

                //Testing non null non zero values
                int amount = 0;
                try {
                    amount = Integer.parseInt(spent.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please enter a non zero amount.", Toast.LENGTH_LONG).show();
                }
                if (desc.equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter a description.", Toast.LENGTH_LONG).show();
                else if (amount == 0)
                    Toast.makeText(getApplicationContext(), "Please enter a non zero amount.", Toast.LENGTH_LONG).show();
                else
                    addExpense(desc, amount, category);
            }
        });
    }

    public void makeSpinner() {
        //making spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void addExpense(String desc, final int amount, final String category) {

        //creating map of new exp
        Map<String, Integer> map = new HashMap<>();
        map.put(desc, amount);

        //Writing the exp in categories collection document
        db.collection("Users").document(Uid).collection("Categories").document(category)
                .set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Added expense record!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Document written");
                description.setText("");
                spent.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error writing record.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error-" + e);
            }
        });

        //Computing total exp in a categry
        db.document("/Users/" + Uid + "/Categories/" + category).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnap = task.getResult();
                            if (documentSnap.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + documentSnap.getData());
                                Map<String, Object> map;
                                map = documentSnap.getData();
                                Log.d(TAG, "map" + map);
                                int exp=0;
                                for(String key : map.keySet()){
                                    if(key.equals("Budget") || key.equals("Category name") || key.equals("Expense"))
                                        ;
                                    else
                                        exp+= (Long)map.get(key);
                                }
                                Log.d(TAG,"expense tot"+exp);
                                //Updating the final expense total in both record places
                                db.document("/Users/" + Uid + "/Categories/" + category).update("Expense",exp);
                                db.document("/Users/" + Uid).update(category+".expense",exp);
                            }
                        }
                    }
                });
        }
}