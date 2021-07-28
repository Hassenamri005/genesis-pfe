package com.example.pfe;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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


public class NotificationFragment extends Fragment implements MyRecyclerViewAdapter2.ItemClickListener{
    private Button button;
    MyRecyclerViewAdapter01 adapter;
    private NotificationsViewModel notificationsViewModel;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> notificationTitle,notificationContent,notificationDate,notificationTime;
    TextView clearAll;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);


        // data to populate the RecyclerView with
        notificationTitle = new ArrayList<>();
        notificationContent = new ArrayList<>();
        notificationDate = new ArrayList<>();
        notificationTime = new ArrayList<>();
        //historyNames.add("Horse");
        // set up the RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyRecyclerViewAdapter01(getContext(), notificationTitle,notificationContent,notificationDate,notificationTime);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/notifications");
        getUserInfo();

        clearAll = root.findViewById(R.id.textView4);
        if (isAdmin==false){
            clearAll.setVisibility(View.INVISIBLE);
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
                            String value = String.valueOf(next.child("title").getValue());
                            String value2 = String.valueOf(next.child("content").getValue());
                            String value3 = String.valueOf(next.child("date").getValue());
                            String value4 = String.valueOf(next.child("time").getValue());
                            Log.i("hhhhhhhhhhhhhh ",value4);
                            int insertIndex = 0;
                            notificationTitle.add(insertIndex, value);
                            notificationContent.add(insertIndex, value2);
                            notificationDate.add(insertIndex, value3);
                            notificationTime.add(insertIndex, value4);
                            adapter.notifyItemInserted(insertIndex);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
       // Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }




}