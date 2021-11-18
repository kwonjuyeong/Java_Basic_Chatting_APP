package com.example.java_chat_practice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends AppCompatActivity {


    public static final int REQUEST_CODE_FILE_BROWSER = 0;
    private ArrayList<ChattingInfo> chattingInfoArrayList = new ArrayList<ChattingInfo>();
    private ConstraintLayout layout;
    private String senderUID;
    private String receiverUID;
    private RecyclerView.Adapter adapter;
    private String roomName;
    private ChatViewHolder selectedViewHolder;
    private ProgressBar progressBar;

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
        ImageButton imageButtonAdd = findViewById(R.id.imageButtonadd);
        //layout = findViewById(R.id.constraintlayoutChatViewHolder);
        progressBar = findViewById(R.id.progressBar);

        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                intent1.setType("*/*");

                startActivityForResult(intent1, REQUEST_CODE_FILE_BROWSER);
            }
        });

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
        ArrayList<String> userlist = new ArrayList<>();
        userlist.add(senderUID);
        userlist.add(receiverUID);

        ref.orderByChild("userlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp_chatting_room_name = "";
                for(DataSnapshot chattingroom_snap : snapshot.getChildren()){
                    for(DataSnapshot child : chattingroom_snap.getChildren()){
                        if(child.getKey().compareTo("userlist")==0){
                            GenericTypeIndicator<List<String>> indicator = new GenericTypeIndicator<List<String>>() {};
                            ArrayList<String> tmplist = (ArrayList<String>) child.getValue(indicator);

                            if(tmplist.equals(userlist)){
                              temp_chatting_room_name = chattingroom_snap.getKey();
                              break;
                            }
                        }
                    }
                    if(temp_chatting_room_name.length()>0) break;
                }

                if(temp_chatting_room_name.length() <=0){
                    DatabaseReference child_ref = ref.push();
                    roomName = child_ref.getKey();
                }
                else {
                    roomName = temp_chatting_room_name;
                }
                ref.child(roomName).child("userlist").setValue(userlist);

                ref.child(roomName).child("chat").addValueEventListener(new ValueEventListener() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_FILE_BROWSER && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            File file = new File(uri.getPath());



            StorageReference storageReference = FirebaseStorage.getInstance().getReference(roomName).child(file.getName());
            UploadTask uploadTask = storageReference.putFile(uri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    progressBar.setVisibility(View.VISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressBar.setVisibility(View.GONE);

                        addText2DB(storageReference.getName() );
                }
            });

        }
    }

    private void addChatting2List(EditText editTextChat) {
        String text = editTextChat.getText().toString();
        editTextChat.setText("");

        addText2DB(text);
    }

    private void addText2DB(String text) {
        chattingInfoArrayList.add(new ChattingInfo(text, senderUID));
        adapter.notifyDataSetChanged();

        DatabaseReference ref_chatlist = FirebaseDatabase.getInstance().getReference("chattinglist");
        ref_chatlist.child(roomName).child("chat").setValue(chattingInfoArrayList);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        boolean result = super.onContextItemSelected(item);
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.context_menu_download_image:
                String chattext = selectedViewHolder.getTextView().getText().toString();

                StorageReference file_ref = FirebaseStorage.getInstance().getReference(roomName).child(chattext);

                File tempFile = null;
                try {
                    tempFile = File.createTempFile("image", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final File finalTempFile = tempFile;
                file_ref.getFile(tempFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {  Toast.makeText(getApplicationContext(), "file download complete", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(finalTempFile.getAbsolutePath());
                    ImageView imageViewChat = selectedViewHolder.getImageViewChat();
                    imageViewChat.setImageBitmap(bitmap);
                    imageViewChat.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
        return result;
    }

    private  class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final TextView textView;
        private final ImageView imageViewChat;

        public ChatViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewChatViewHolder);
            imageViewChat = itemView.findViewById(R.id.imageViewChat);

            textView.setOnCreateContextMenuListener(this);
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


        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageViewChat() {
            return imageViewChat;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuInflater menuInflater = new MenuInflater(getApplicationContext());
            menuInflater.inflate(R.menu.context_menu_chat, menu);

            selectedViewHolder = this;

        }
    }

}