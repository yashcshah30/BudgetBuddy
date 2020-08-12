package com.example.sarika.budgetbuddy.ui.addCategory;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sarika.budgetbuddy.HomeScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.example.sarika.budgetbuddy.UserDocInfo;

public class AddViewModel extends ViewModel {
    public MutableLiveData<Boolean> bool;
    public String Uid;
    private FirebaseAuth mAuth;
    public FirebaseFirestore db;
    private static final String TAG = "DocSnippets";
    public boolean flag;
    public Map<String, Object> docMap;

    public AddViewModel() {
        bool=new MutableLiveData<Boolean>();
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
    }

    MutableLiveData<Boolean> getBool(){
        return bool;
    }

    public void addData(String name, int budget) {
        checkCategory(name, budget);
    }

    //ORIGINAL FUNC
    public void checkCategory(String name, int budget) {
        Log.d(TAG, "In check cat");
        final String catName = name;
        final int catBudget = budget;
        Log.d(TAG, "catname" + catName);
        DocumentReference docRef = db.document("/Users/" + Uid);
        Log.d(TAG, "docref" + docRef);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnap = task.getResult();
                    if (documentSnap.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnap.getData());
                        Map<String, Object> map;
                        map = documentSnap.getData();
                        Log.d(TAG, "map" + map);
                        checkMap(map, catName, catBudget);
                        /*if (map.containsKey(catName)) {
                            Log.d(TAG, "contains key");
                            flag = true;
                        } else {
                            flag = false;
                            Log.d(TAG, "doesn't contains key");
                        }
                         */
                        //document=documentSnap;
                        /*if(documentSnap.contains(catName))
                            flag=true;
                        else
                            flag=false;
                        Log.d(TAG, "Document data: " + document.getData());
                        */

                    } else {
                        Log.d(TAG, "No such document");
                        //db.collection("Users").document(Uid).set(new HashMap<String,Object>());
                        Map<String, Object> category = new HashMap<>();
                        category.put("Category name", catName);
                        category.put("Budget", catBudget);
                        category.put("Expense", 0);
                        UserDocInfo user = new UserDocInfo(catName, catBudget, 0);
                        Map<String, UserDocInfo> userDoc = new HashMap<>();
                        userDoc.put(catName, user);
                        db.collection("Users").document(Uid).collection("Categories").document(catName).set(category, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                        db.collection("Users").document(Uid).set(userDoc, SetOptions.merge())
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
                        bool.setValue(false);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        /*try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e){
            Log.d("Thread Sleep","error="+e);
        }*/
        //Log.d(TAG, "Flag" + flag);
       /*
        if (flag == true) {
            Log.d(TAG, "returning true");
            return true;
        } else {
            Log.d(TAG, "returning false");
            return false;
        }
        */
        // return  false;

    }

    public void checkMap(Map<String, Object> map, String name, int budget) {
        Log.d(TAG, "In check map");
        if (!map.containsKey(name)) {
            flag = false;
            bool.setValue(false);
            Log.d(TAG, "Uid" + Uid);
            Map<String, Object> category = new HashMap<>();
            category.put("Category name", name);
            category.put("Budget", budget);
            category.put("Expense", 0);
            UserDocInfo user = new UserDocInfo(name, budget, 0);
            Map<String, UserDocInfo> userDoc = new HashMap<>();
            userDoc.put(name, user);
            db.collection("Users").document(Uid).collection("Categories").document(name).set(category, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });

            db.collection("Users").document(Uid).set(userDoc, SetOptions.merge())
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
        } else {
            flag = true;
            bool.setValue(true);
            Log.d(TAG, "flag=" + flag);
        }
    }
}
/*
    //TRYING WITH A FUNCTION
    public boolean checkCategory(String name) {
        final String catName = name;
        Log.d(TAG, "catname" + catName);
        DocumentReference docRef = db.document("/Users/" + Uid);
        Log.d(TAG, "docref" + docRef);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnap = task.getResult();
                    if (documentSnap.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnap.getData());
                        Map<String, Object> map;
                        map = documentSnap.getData();
                        Log.d(TAG, "map" + map);
                        flag=test_function(map,catName);
                        Log.d(TAG,"Value of flaf set to"+flag);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        Log.d(TAG, "Flag" + flag);
        return flag;
    }

    public boolean test_function(Map<String,Object> map,String name){
        return map.containsKey(name);
    }
}
*/




/* USING COLLECTIONS
        CollectionReference cRef=db.collection("/Users/"+Uid+"/Categories");
        cRef.whereEqualTo("Category name",name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //flag=true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.get("Category name").equals(catName)) {
                                    flag = true;
                                    Log.d(TAG,"Value of flag"+flag);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            flag=false;
                            Log.d(TAG,"Value of flag"+flag);
                        }
                    }
                });
        Log.d(TAG,"returning"+flag);
        return flag;
 */
/*
 public int addData(String name, int budget) {
        //flag=false;
        /*if (checkCategory(name)) {
            return 0;
        }
        checkCategory(name);
        /*try{
            Log.d(TAG,"puttinh thread to sleep");
            Thread.sleep(5000);
        }
        catch (InterruptedException e){
            Log.d("Exception","InterruptedEx"+e);
        }*/
        /*if(flag)
            return 0;

        if(docMap.containsKey(name))
                return 0;

                Log.d(TAG, "Uid" + Uid);
                Map<String, Object> category = new HashMap<>();
        category.put("Category name", name);
        category.put("Budget", budget);
        category.put("Expense", 0);
        UserDocInfo user = new UserDocInfo(name, budget, 0);
        Map<String, UserDocInfo> userDoc = new HashMap<>();
        userDoc.put(name, user);
        db.collection("Users").document(Uid).collection("Categories").document(name).set(category, SetOptions.merge())
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

        db.collection("Users").document(Uid).set(userDoc, SetOptions.merge())
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
        return 1;
        }
 */