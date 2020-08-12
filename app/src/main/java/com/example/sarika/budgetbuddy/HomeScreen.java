package com.example.sarika.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends AppCompatActivity {

    private String Uid;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseFirestore db;
    private String TAG = "doc";
    private Map<String,Object> updates;
    private FirebaseAuth mAuth;
    //private String Uid = mAuth.getUid();
    //TextView Uid_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                   //     .setAction("Action", null).show();
                Intent goToExpense = new Intent();
                goToExpense.setClass(getApplicationContext(),AddExpense.class);
                startActivity(goToExpense);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Uid=mAuth.getUid();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_add, R.id.nav_edit,
                R.id.nav_delete, R.id.nav_sign_out)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //display contents
        /*refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
                return false;
            }
        });

         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.action_refresh) {
            Toast.makeText(getApplicationContext(), "Refreshed your expense log.", Toast.LENGTH_LONG).show();
            DocumentReference doc = db.document("/Users/" + Uid);
            doc.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    updates = documentSnapshot.getData();
                                    Log.d(TAG, "map" + updates);
                                    refreshdata();
                                    //deletedata();
                                } else {
                                    Log.d(TAG, "onComplete: No such document");
                                }
                            } else {
                                Log.d(TAG, "onComplete:get failed with ", task.getException());
                            }
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
            //this.recreate();
    }

    public void refreshdata()
    {
        for(String key:updates.keySet())
        {
            db.collection("Users").document(Uid).update(
                    key+".expense", 0
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
        //this.recreate();
  //      HomeViewModel obj=new HomeViewModel();
//        obj.getData();

        db.collection("/Users/"+Uid+"/Categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Map<String, Object> updates=new HashMap<>();
                                Log.d("in refresh","data map="+data);
                                for (String key : data.keySet()) {
                                    if (key.equals("Expense"))
                                        updates.put("Expense", 0);
                                    if (key.equals("Category name"))
                                        updates.put("Category name",data.get(key).toString());
                                    if (key.equals("Budget"))
                                        updates.put("Budget",Integer.parseInt(data.get(key).toString()));
                                }
                                Log.d("in refresh","updates map="+updates);
                                document.getReference().set(updates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Log.d("In refreshg", "update success");
                                                else
                                                    Log.d("In refreshg", "update not success" + task.getException());
                                            }
                                        });
                            }
                        }
                        else
                            Log.d("in refresh","task failed"+task.getException());
                    }
                });
        finish();
        startActivity(getIntent());

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}