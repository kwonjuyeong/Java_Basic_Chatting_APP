package com.example.java_chat_practice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChattingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChattingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ChattingRoomInfo> chattingRoomInfoArrayList = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    public ChattingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChattingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChattingListFragment newInstance(String param1, String param2) {
        ChattingListFragment fragment = new ChattingListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chatting_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewChattingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getContext()).
                        inflate(R.layout.chattinglistlayout, parent, false);
                RecyclerView.ViewHolder viewHolder = new ChattingListViewHolder(itemView);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ChattingListViewHolder viewHolder = (ChattingListViewHolder) holder;
                ChattingRoomInfo chattingRoomInfo = chattingRoomInfoArrayList.get(position);

                TextView textViewUserList = viewHolder.getTextViewUserList();
                ArrayList<String> userlist = chattingRoomInfo.getUserlist();

                String userlisttext = "";
                for (String displayName : userlist) {
                   userlisttext =  userlisttext.concat(displayName + ",");
                }
                textViewUserList.setText(userlisttext);
            }

            @Override
            public int getItemCount() {
                return chattingRoomInfoArrayList.size();
            }
        };
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference("chattinglist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot roomName : snapshot.getChildren()){
                    ArrayList<String> userlist = new ArrayList<>();
                    chattingRoomInfoArrayList.add(new ChattingRoomInfo(userlist));

                    for(DataSnapshot sublist : roomName.getChildren()){
                        if(sublist.getKey().equals("userlist")){
                            for(DataSnapshot user: sublist.getChildren()){
                                String uid = (String) user.getValue();
                                FirebaseDatabase.getInstance()
                                        .getReference("userlist").child(uid)
                                        .child("name").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        String displayName = (String) dataSnapshot.getValue();
                                        userlist.add(displayName);
                                        adapter.notifyDataSetChanged();
                                    }
                                });


                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    private static class ChattingListViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewUserList;
        private final TextView textViewLastChatting;

        public ChattingListViewHolder(View itemView) {
            super(itemView);

            textViewUserList = itemView.findViewById(R.id.textViewChattingRoomUserList);
            textViewLastChatting = itemView.findViewById(R.id.textViewLastChatting);
        }

        public TextView getTextViewUserList() {
            return textViewUserList;
        }

        public TextView getTextViewLastChatting() {
            return textViewLastChatting;
        }
    }
}