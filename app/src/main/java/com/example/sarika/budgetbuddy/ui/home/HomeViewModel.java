package com.example.sarika.budgetbuddy.ui.home;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sarika.budgetbuddy.UserDocInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeViewModel extends ViewModel {

   // private MutableLiveData<String> mText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String Uid;
    private MutableLiveData<List<UserDocInfo>> list;

    public HomeViewModel() {
        //mText = new MutableLiveData<>();
        //mText.setValue("This is home fragment");
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        Uid=mAuth.getUid();
        list=new MutableLiveData<>();
    }

    public  void getData(){
        final List<UserDocInfo> localList= new ArrayList<UserDocInfo>();
        db.collection("Users").document(Uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnap = task.getResult();
                            if (documentSnap.exists()) {
                                Map<String, Object> map = documentSnap.getData();
                                Log.d("doc in get data","map="+map);
                                for(String key :map.keySet()){
                                    Log.d("Doc","map class="+map.get(key));
                                    //localList.add((UserDocInfo)map.get(key));
                                    HashMap<String,Object> hash= (HashMap<String, Object>)map.get(key);
                                    UserDocInfo obj=new UserDocInfo((String)hash.get("categoryName"),(Long)hash.get("budget"),(Long)hash.get("expense"));
                                    Log.d("doc","data="+obj.categoryName+obj.expense+obj.budget);
                                    localList.add(obj);
                                }
                                list.setValue(localList);
                            } else {
                                Log.d(TAG, "No such document");
                                db.collection("Users").document(Uid).set(new HashMap<Object,String>());
                                getData();
                                //list.setValue(null);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public MutableLiveData<List<UserDocInfo>> getList(){
        //Log.d("home view model",list+"");
        return list;
    }
   // public LiveData<String> getText() {
        //return mText;
    //}
}