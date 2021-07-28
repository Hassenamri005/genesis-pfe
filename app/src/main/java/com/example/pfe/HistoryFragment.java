package com.example.pfe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pfe.ui.notifications.NotificationsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import static com.example.pfe.MainActivity.isAdmin;

public class HistoryFragment extends Fragment implements MyRecyclerViewAdapter2.ItemClickListener{
    MyRecyclerViewAdapter3 adapter;
    private NotificationsViewModel notificationsViewModel;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> historyNames,historyActions,historyDate,hisoryTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);





        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/history");

        if (isAdmin==true) {

            // data to populate the RecyclerView with
            historyNames = new ArrayList<>();
            historyActions = new ArrayList<>();
            historyDate = new ArrayList<>();
            hisoryTime = new ArrayList<>();
            //historyNames.add("Horse");

            // set up the RecyclerView
            RecyclerView recyclerView = root.findViewById(R.id.rvAnimals);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new MyRecyclerViewAdapter3(getContext(), historyNames,historyActions,historyDate,hisoryTime);
            adapter.setClickListener(this::onItemClick);
            recyclerView.setAdapter(adapter);

            getUserInfo();
        }else {
            Toast.makeText(getContext(),"You are not admin to get access on this part !",Toast.LENGTH_LONG).show();
        }




        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void getUserInfo(){
        // Read from the database
        Query query = myRef.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String value = String.valueOf(next.child("who").getValue());
                            String value2 = String.valueOf(next.child("action").getValue());
                            String value3 = String.valueOf(next.child("date").getValue());
                            String value4 = String.valueOf(next.child("time").getValue());
                            int insertIndex = 0;
                            historyNames.add(insertIndex, value);
                            historyActions.add(insertIndex, value2);
                            historyDate.add(insertIndex, value3);
                            hisoryTime.add(insertIndex, value4);
                            adapter.notifyItemInserted(insertIndex);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }

}














/*
    Button btn = root.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();
        myRef.child("history").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date= formatter.format(date);
        myRef.child("history").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("history").child(key).child("time").setValue(currentTime.getHours()+":"+currentTime.getMinutes());
        String action = email+" Signed up.";
        myRef.child("history").child(key).child("action").setValue(action);
        }
        });
        */