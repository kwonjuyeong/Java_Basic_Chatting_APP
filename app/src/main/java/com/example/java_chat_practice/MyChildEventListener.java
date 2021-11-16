package com.example.java_chat_practice;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class MyChildEventListener implements ChildEventListener {
    private final FriendListFragment friendListFragment;

    public MyChildEventListener(FriendListFragment friendListFragment) {
        this.friendListFragment = friendListFragment;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key_uid = snapshot.getKey();
        String e = (String) snapshot.child("email").getValue();
        String n = (String) snapshot.child("name").getValue();
        String u = (String) snapshot.child("uid").getValue();

        Log.d("Datasnapshot", e + ", "+ n +", " + u);

        friendListFragment.getFriendInfoArrayList().add(new FriendInfo(n, e));
        friendListFragment.getAdapter().notifyDataSetChanged();

        String current_user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendlist = FirebaseDatabase.getInstance().getReference("friendlist");
        friendlist.child(current_user_uid).setValue(friendListFragment.getFriendInfoArrayList());
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
