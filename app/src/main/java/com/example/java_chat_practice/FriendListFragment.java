package com.example.java_chat_practice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArrayList<FriendInfo> getFriendInfoArrayList() {
        return friendInfoArrayList;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    private ArrayList<FriendInfo> friendInfoArrayList = new ArrayList<FriendInfo>();
    private RecyclerView.Adapter adapter;

    public FriendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

         setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.friendlist_menu_layout,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_friend_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = View.inflate(getContext(), R.layout.friend_add_dialog_layout, null);

                EditText editTextName = view.findViewById(R.id.editTextTextPersonName);
                EditText editTextEmail = view.findViewById(R.id.editTextTextEmailAddress);

                builder.setView(view);
                builder.setTitle("친구 추가");
                builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editTextName.getText().toString();
                        String email = editTextEmail.getText().toString();

                        DatabaseReference userlist = FirebaseDatabase.getInstance().getReference("userlist");
                        Query query = userlist.orderByChild("email").equalTo(email);
                        query.addChildEventListener(new MyChildEventListener(FriendListFragment.this));


                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.recyclerview_itemview_layout, parent, false);
                RecyclerView.ViewHolder viewHolder = new MyViewHolder(itemView);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                MyViewHolder viewHolder = (MyViewHolder) holder;

                viewHolder.setName(friendInfoArrayList.get(position).getName());
                viewHolder.setEmail(friendInfoArrayList.get(position).getEmail());
            }

            @Override
            public int getItemCount() {
                return friendInfoArrayList.size();
            }
        };
        recyclerView.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendlist = FirebaseDatabase.getInstance().getReference("friendlist");
        friendlist.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<FriendInfo>> t = new GenericTypeIndicator<List<FriendInfo>>() {};
                ArrayList<FriendInfo> arrayList = (ArrayList<FriendInfo>) snapshot.getValue(t);
                friendInfoArrayList = arrayList;
                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;
        private final TextView textViewEmail;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
        }

        public void setName(String name) {
            textViewName.setText(name);
        }

        public void setEmail(String email) {
            textViewEmail.setText(email);
        }
    }

}