package com.drapps.firebasechat.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.drapps.firebasechat.fragment.ImageFragment;
import com.drapps.firebasechat.adapter.ChatAdapter;
import com.drapps.firebasechat.common.Common;
import com.drapps.firebasechat.model.Chat;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import com.drapps.firebasechat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";
    Context context = ChatActivity.this;
    BottomSheetBehavior sheetBehavior;
    CardView mediaBottomSheet;
    FloatingActionButton openMedia;
    CoordinatorLayout coordinatorLayout;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    EditText etMessage;
    private Query query;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private ImageView imgSelectImage;
    private View parentView;


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
        chatAdapter = new ChatAdapter(context, chatList);
        recyclerView.setAdapter(chatAdapter);


        imgSelectImage = findViewById(R.id.img_select_image);
        imgSelectImage.setOnClickListener(this);

        mediaBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(mediaBottomSheet);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference("Image");

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
        openBottomSheet();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.floating_chat:
                if (!etMessage.getText().toString().equals("")) {
                    createNewMessage(Common.TYPE_TEXT, etMessage.getText().toString());
                } else {
                    etMessage.setText("");
                }
                break;
            case R.id.img_select_image:
                pickImage();
                break;

        }
    }

    private void getChatMessage() {
        //Get all chat messages from database

        query = mDatabaseReference.child("message").child("1").orderByChild("timestamp");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Add data to list when data added to database.
                Log.d(TAG, "onChildAdded" + dataSnapshot.getKey() + s);
                Chat chat = dataSnapshot.getValue(Chat.class);
                chatList.add(chat);
//                chatAdapter = new ChatAdapter(context, chatList);
//                recyclerView.setAdapter(chatAdapter);
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                System.out.print(TAG + "onChildRemoved" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void createNewMessage(int type, String message) {
        //Create a new message on send click


        Chat newMessage = new Chat
                (message,
                        "101",
                        "501",
                        "firebase-token",
                        type,
                        String.valueOf(ServerValue.TIMESTAMP));
        System.out.print("Message" + message);

        mDatabaseReference.child("message").child("1").push().setValue(newMessage)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                        Log.d("New Message", "New message uploaded");
                    }
                });

        switch (type) {
            case 1:
                etMessage.setText("");
                break;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void openBottomSheet() {
        //Set touch listener on Drawable end of Edittext Message and open bottom sheet.
        etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                     Check if the runtime version is at least Lollipop
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                         get the center for the clipping circle
                                int cx = mediaBottomSheet.getWidth() / 2;
                                int cy = mediaBottomSheet.getHeight();

//                         get the final radius for the clipping circle
                                float finalRadius = (float) Math.hypot(cy, cx);

//                         create the animator for this view (the start radius is zero)
                                Animator anim = ViewAnimationUtils.createCircularReveal(mediaBottomSheet, cx, cy, 0, finalRadius);
//                         make the view visible and start the animation
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                mediaBottomSheet.setVisibility(View.VISIBLE);
                                anim.start();
                            } else {
//                         set the view to visible without a circular reveal animation below Lollipop
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                         get the center for the clipping circle
                                int cx = mediaBottomSheet.getWidth() / 2;
                                int cy = mediaBottomSheet.getHeight();
//                         get the final radius for the clipping circle
                                float finalRadius = (float) Math.hypot(cx, cy);

//                         create the animator for this view (the start radius is zero)
                                Animator anim = ViewAnimationUtils.createCircularReveal(mediaBottomSheet, cy, cx, finalRadius, 0);

//                         make the view visible and start the animation

                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                sheetBehavior.setPeekHeight(0);
                                anim.start();

                            } else {
//                         set the view to visible without a circular reveal animation below Lollipop
                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void pickImage() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, Common.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                // this is the image selected by the user
                Uri imageUri = data.getData();
                Bundle bundle = new Bundle();
                bundle.putString("image", String.valueOf(imageUri));
                ImageFragment fr = new ImageFragment();
                fr.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.coordinator_chat, fr);
                fragmentTransaction.commit();

            }
        }
    }

    public void getImageFromFragment(String uri) {
        if (uri != null && !uri.equals("")) {
            uploadImageToStorage(uri);
        }
    }

    private void uploadImageToStorage(String image) {

        final StorageReference storageReference = mStorageReference.child("1").child("image");

//        storageReference.putFile(Uri.parse(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//            }
//        });
        final UploadTask uploadTask = storageReference.putFile(Uri.parse(image));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    createNewMessage(Common.TYPE_IMAGE, String.valueOf(downloadUri));
                } else {
                    // Handle failures
                    // ...
                }
            }
        })
//        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.d(TAG, "Image Uploaded");
//
//                if (taskSnapshot != null) {
//                    createNewMessage(Common.TYPE_IMAGE, String.valueOf(storageReference.getDownloadUrl()));
////                    Log.d(TAG, "URL" + taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult(););
//                }
//            }
//        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Image Upload Failed");

                    }
                });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Image Upload in progress ");

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                int i = (int) progress;
                Log.d(TAG, String.valueOf(i));
            }
        });

    }


}
