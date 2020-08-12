package com.example.sarika.budgetbuddy.ui.editCategory;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sarika.budgetbuddy.UserDocInfo;
import com.example.sarika.budgetbuddy.ui.DataRetrieval;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EditViewModel extends ViewModel implements DataRetrieval {
    // TODO: Implement the ViewModel
    MutableLiveData<String[]> cat;
    public String Uid;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    int expense;
    private static final String TAG = "DocSnippets";
    UserDocInfo user;

    public EditViewModel(){
        cat=new MutableLiveData<>();
        mAuth=FirebaseAuth.getInstance();
        Uid=mAuth.getUid();
        db=FirebaseFirestore.getInstance();
    }

    /*public String[] getData(){
        db.collection("Users").document(Uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    int i=0;
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Map<String, Object> getcat = documentSnapshot.getData();
                            for(Map.Entry<String, Object> m : getcat.entrySet())
                            {
                                cat[i] = m.getKey();
                                i++;
                            }
                        }
                        else{
                            Log.d(TAG, "DocumentSnapshot not successful!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error reading document", e);
                    }
                });
        return cat;
    }*/

    /*public int getexpense(final String name){
        DocumentReference ref = db.document("/Users/" +Uid);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Map<String, Object> map;
                                map = documentSnapshot.getData();
                                expense = findexpense(map, name);
                            }
                            else{
                                Log.d(TAG, "No such document");
                            }
                        }
                        else{
                            Log.d(TAG, "get failed with", task.getException());
                        }
                    }
                });
        return expense;
    }*/

    /*public int findexpense(Map<String, Object> map, String name){
        Log.d("Doc","map="+map);
        for(Map.Entry<String, Object> m : map.entrySet()) {
            if (name.equals(m.getKey())) {
                user = (UserDocInfo) m.getValue();
            }
        }
        Log.d(TAG, "expense: "+user.getExpense());
        return user.getExpense();
    }*/


    public void getData(){
        DocumentReference ref = db.document("/Users/" +Uid);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Map<String, Object> map;
                                map = documentSnapshot.getData();
                                String cat1[] = getcatname(map);
                                for(int i=0;i<cat1.length;i++)
                                    Log.d("Doc","cat1="+cat1[i]);
                                cat.setValue(cat1);
                            }
                            else{
                                Log.d(TAG, "No such document");
                            }
                        }
                        else{
                            Log.d(TAG, "get failed with", task.getException());
                        }
                    }
                });
       // Log.d("Doc","returning"+cat);
     //return cat;
    }

    public String[] getcatname(Map<String, Object> map){
        int i=0;
        Log.d("Doc","map="+map);
        String[] catname = new String[map.size()];
        for(Map.Entry<String, Object> m : map.entrySet())
        {
            catname[i] = m.getKey();
            //user = (UserDocInfo) m.getValue();
            //Log.d("Doc","user"+user.getExpense());
            i++;
        }
        //cat.setValue(catname);
        return catname;
    }

    public MutableLiveData<String[]> getCat() {
        return cat;
    }

    public void updateData(String name, int budget){
        Map<String, Object> map = new HashMap<>();
        map.put("Category name", name);
        map.put("Budget", budget);
        db.collection("Users").document(Uid).collection("Categories").document(name).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        db.collection("Users").document(Uid).update(
            name+".budget", budget
        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}

