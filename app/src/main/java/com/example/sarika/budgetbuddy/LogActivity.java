package com.example.sarika.budgetbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class LogActivity extends AppCompatActivity {

    private String categoryName;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String Uid;
    private List<String> mDesc, mAmount;
    private RecyclerView mRecyclerView;
    private LogRecylcerAdapter mAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        categoryName=getIntent().getStringExtra("categoryName");
        progressBar=findViewById(R.id.progressBar_log);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        Uid=mAuth.getUid();
        mRecyclerView=findViewById(R.id.log_recycle);
    }

    public void onStart(){
        super.onStart();
        mDesc=new ArrayList<>();
        mAmount=new ArrayList<>();
        db.collection("Users").document(Uid).collection("Categories").document(categoryName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    Map<String, Object> map = doc.getData();
                    for (String key : map.keySet()) {
                        if (key.equals("Budget") || key.equals("Category name") || key.equals("Expense"))
                            continue;
                        else {
                            mDesc.add(key);
                            long amt=(Long)map.get(key);
                            mAmount.add(amt+"");
                        }
                    }
                    updateUI();
                } else {
                    Log.d("LogActivity", "get failed with ", task.getException());
                }
            }
        });
    }

    public void updateUI(){
        mAdapter=new LogRecylcerAdapter(mDesc,mAmount,getApplicationContext(),categoryName);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressBar.setVisibility(View.INVISIBLE);
    }
}


class LogRecylcerAdapter extends RecyclerView.Adapter<LogRecylcerAdapter.ViewHolder>{
    private final String TAG="Recycle";
    List<String> desc;
    List<String> spent;
    Context mContext;
    String categoryName;
    public LogRecylcerAdapter(List<String> list, List<String> amount, Context context, String name){
        desc=list;
        spent=amount;
        mContext=context;
        categoryName=name;
    }

    @NonNull
    @Override
    public LogRecylcerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogRecylcerAdapter.ViewHolder holder, final int position) {
        holder.description.setText(desc.get(position));
        holder.amount.setText(spent.get(position));


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d("LOg","in parent on click");
                AlertDialog.Builder builder= new AlertDialog.Builder((view.getContext()));
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete this entry");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Log activity","in yes");
                        String name=desc.get(position);
                        int amt=Integer.parseInt(spent.get(position));
                        FirebaseAuth auth=FirebaseAuth.getInstance();
                        String Uid=auth.getUid();
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        Toast.makeText(view.getContext(), "Deleted "+desc.get(position), Toast.LENGTH_SHORT).show();
                        desc.remove(position);
                        spent.remove(position);
                        db.collection("Users").document(Uid).update(categoryName+".expense", FieldValue.increment(-amt));
                        db.collection("Users").document(Uid).collection("Categories").document(categoryName).update(name,FieldValue.delete());
                        db.collection("Users").document(Uid).collection("Categories").document(categoryName).update("Expense",FieldValue.increment(-amt));
                        notifyDataSetChanged();
                        //new LogActivity().updateUI();
                        //update list
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Log.d("Log activity","in yes");
                                //Toast.makeText(view.getContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                //AlertDialog dia=builder.create();
                //dia.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "list size="+desc.size());
        return desc.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //TextView categoryName, budget, expense,percent,warning;
        TextView description, amount;
        RelativeLayout parentLayout;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description=itemView.findViewById(R.id.description_text);
            amount=itemView.findViewById(R.id.amount_text);
            parentLayout=itemView.findViewById(R.id.parent);
            //progressBar=itemView.findViewById(R.id.progress_bar_log);
        }
    }
}
