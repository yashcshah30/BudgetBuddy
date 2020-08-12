package com.example.sarika.budgetbuddy.ui.deleteCategory;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sarika.budgetbuddy.UserDocInfo;
import com.example.sarika.budgetbuddy.ui.DataRetrieval;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DeleteViewModel extends ViewModel implements DataRetrieval {

    MutableLiveData<String[]> cat;
    public String Uid;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "DocSnippets";
    UserDocInfo user;

    public DeleteViewModel(){
        cat=new MutableLiveData<>();
        mAuth= FirebaseAuth.getInstance();
        Uid=mAuth.getUid();
        db= FirebaseFirestore.getInstance();
    }

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

    public MutableLiveData<String[]> getCat(){
        return cat;
    }
    // TODO: Implement the ViewModel
}