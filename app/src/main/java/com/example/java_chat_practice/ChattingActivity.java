package com.example.java_chat_practice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends AppCompatActivity {


    private ArrayList<ChattingInfo> chattingInfoArrayList = new ArrayList<ChattingInfo>();
    private ConstraintLayout layout;
    private String senderUID;
    private String receiverUID;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_activiry);

        Intent intent = getIntent();
        senderUID = intent.getStringExtra("sender");
        receiverUID = intent.getStringExtra("receiver");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        EditText editTextChat = findViewById(R.id.editTextChat);
        ImageButton imageButtonSend = findViewById(R.id.imageButtonSend);
        layout = findViewById(R.id.constraintlayoutChatViewHolder);

        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.chatviewholder_layout, parent, false);
                RecyclerView.ViewHolder viewHolder = new ChatViewHolder(itemView);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ChattingInfo info = chattingInfoArrayList.get(position);
                ChatViewHolder viewHolder = (ChatViewHolder) holder;

                viewHolder.setText(info.getText());

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                viewHolder.isSender(uid.equals(info.getSenderUID()));
            }

            @Override
            public int getItemCount() {
                return chattingInfoArrayList.size();
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

            editTextChat.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                        addChatting2List(editTextChat);
                    }
                    return false;
                }
            });

            imageButtonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChatting2List(editTextChat);
                }
            });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chattinglist");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<ChattingInfo>> indicator = new GenericTypeIndicator<List<ChattingInfo>>() {
                };
                ArrayList<ChattingInfo> list = (ArrayList<ChattingInfo>) snapshot.getValue(indicator);
                if (list != null) {
                    chattingInfoArrayList = list;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addChatting2List(EditText editTextChat) {
        String text = editTextChat.getText().toString();

        chattingInfoArrayList.add(new ChattingInfo(text, senderUID, receiverUID));
        adapter.notifyDataSetChanged();

        DatabaseReference ref_chatlist = FirebaseDatabase.getInstance().getReference("chattinglist");
        ref_chatlist.setValue(chattingInfoArrayList);
    }

    private  class ChatViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewChatViewHolder);
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void isSender(boolean b) {
            //ConstraintSet set = new ConstraintSet();
            //set.clone(layout);
            //set.clear(R.id.textViewChatViewHolder, ConstraintSet.LEFT);
            //set.clear(R.id.textViewChatViewHolder, ConstraintSet.RIGHT);

            if(b){
                textView.setBackgroundColor(Color.YELLOW);
                //set.connect(R.id.textViewChatViewHolder, ConstraintSet.RIGHT, R.id.constraintlayoutChatViewHolder, ConstraintSet
                //.RIGHT);
            }
            else{
                textView.setBackgroundColor(Color.GREEN);
                //et.connect(R.id.textViewChatViewHolder, ConstraintSet.LEFT, R.id.constraintlayoutChatViewHolder, ConstraintSet
                //       .LEFT);
            }
        }
    }

}