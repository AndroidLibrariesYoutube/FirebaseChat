package com.drapps.firebasechat.activity;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drapps.firebasechat.model.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import com.drapps.firebasechat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    Context context = ChatActivity.this;
    BottomSheetBehavior sheetBehavior;
    CardView mediaBottomSheet;
    FloatingActionButton openMedia;
    CoordinatorLayout coordinatorLayout;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    EditText etMessage;
    private Query query;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Chat, MessagesHolder> firebaseRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }


    private void init() {
        //Function call when activity create
        etMessage = findViewById(R.id.et_message_chat);
        coordinatorLayout = findViewById(R.id.coordinator);
        openMedia = findViewById(R.id.floating_chat);
        openMedia.setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler_chat);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mediaBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(mediaBottomSheet);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        getChatMessage();
        scrollToDown();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_chat:
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                     Check if the runtime version is at least Lollipop
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                         get the center for the clipping circle
//                        int cx = mediaBottomSheet.getWidth() / 2;
//                        int cy = mediaBottomSheet.getHeight();
//
//                         get the final radius for the clipping circle
//                        float finalRadius = (float) Math.hypot(cy, cx);
//
//                         create the animator for this view (the start radius is zero)
//                        Animator anim = ViewAnimationUtils.createCircularReveal(mediaBottomSheet, cx, cy, 0, finalRadius);
//                         make the view visible and start the animation
//                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//                        mediaBottomSheet.setVisibility(View.VISIBLE);
//                        anim.start();
//                    } else {
//                         set the view to visible without a circular reveal animation below Lollipop
//                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    }
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                         get the center for the clipping circle
//                        int cx = mediaBottomSheet.getWidth() /2;
//                        int cy = mediaBottomSheet.getHeight();
//                         get the final radius for the clipping circle
//                        float finalRadius = (float) Math.hypot(cx, cy);
//
//                         create the animator for this view (the start radius is zero)
//                        Animator anim = ViewAnimationUtils.createCircularReveal(mediaBottomSheet, cy, cx, finalRadius, 0);
//
//                         make the view visible and start the animation
//
//                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        sheetBehavior.setPeekHeight(0);
//                        anim.start();

//                    } else {
                // set the view to visible without a circular reveal animation below Lollipop
//sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);                    }
//                }
                createNewMessage();
                break;
        }
    }


    private void getChatMessage() {
//        progressBar.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.INVISIBLE);
        query = mDatabaseReference.child("message").child("1").orderByChild("timestamp").limitToLast(20);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressBar.setVisibility(View.GONE);

            }
        });


        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, MessagesHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessagesHolder holder, int position, @NonNull Chat model) {
                holder.BindView(position, model, context);


            }

            @NonNull
            @Override
            public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_bubble, parent, false);

                return new MessagesHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop Listener when activity stop
        firebaseRecyclerAdapter.stopListening();
    }

    private void createNewMessage() {

        if (!etMessage.getText().toString().equals("")) {

            Chat newMessage = new Chat(etMessage.getText().toString(),
                    "101",
                    "501",
                    "firebase-token",
                    1,
                    String.valueOf(ServerValue.TIMESTAMP));
//            System.out.print("Message" + newMessage);
            mDatabaseReference.child("message").child("1").push().setValue(newMessage)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);

                            Log.d("New Message", "New message uploaded");
                        }
                    });
            etMessage.setText("");
        } else {
            etMessage.setText("");
        }

    }

    private void scrollToDown() {
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });


    }
}


class MessagesHolder extends RecyclerView.ViewHolder {


    TextView tvRight, tvLeft;


    public MessagesHolder(@NonNull View view) {
        super(view);
        tvLeft = view.findViewById(R.id.tv_message_left);
        tvRight = view.findViewById(R.id.tv_message_right);

    }

    void BindView(int position, Chat model, Context context) {
//        Log.d("Id",model.getSender_id());
        if (model != null) {
            if (model.getSender_id() != null && !model.getSender_id().equals("")) {
                if (model.getSender_id().equals("101")) {
                    tvLeft.setVisibility(View.GONE);
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setText(model.getText());

                } else {
                    tvRight.setVisibility(View.GONE);
                    tvLeft.setVisibility(View.VISIBLE);
                    tvLeft.setText(model.getText());
                }
            } else {
                Log.d("Log", "Sender Id id empty or null");
            }
        } else {
            Log.d("Log", "Model is empty");
        }
    }


}
