package com.saharia.notesell.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saharia.notesell.R;
import com.saharia.notesell.databinding.ActivityUpdateprofileBinding;
import com.saharia.notesell.model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UpdateprofileActivity extends AppCompatActivity {

    ActivityUpdateprofileBinding binding;

    FirebaseAuth firebaseAuth;
    Uri imageUri;
    String name="";

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUpdateprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pd=new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();

        loaduserinfo();

        binding.updateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 85);
            }
        });

        binding.updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validatae();


            }
        });


    }

    private void validatae() {
        name=binding.updateProfileET.getText().toString().trim();
        if (TextUtils.isEmpty(name)){

            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        }else {
            if (imageUri==null){
                updateprofile("https://firebasestorage.googleapis.com/v0/b/notesell.appspot.com/o/Profiles%2Fimages.png?alt=media&token=3b8f31ab-e292-45d9-af75-e2b369dc4dd7");

            }else {
                uploadimage();

            }

        }
    }

    private void uploadimage() {
        pd.setMessage("Updating profile image");
        pd.show();

        String filepathname="Profiles/"+firebaseAuth.getUid();

        StorageReference reference= FirebaseStorage.getInstance().getReference(filepathname);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadImageUrl=""+uriTask.getResult();

                        updateprofile(uploadImageUrl);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UpdateprofileActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateprofile(String imageUri){

        pd.setMessage("Updating user profile...");
        pd.show();

        HashMap<String, Object>hashMap=new HashMap<>();
        hashMap.put("name",name);
        if (imageUri!=null){

            hashMap.put("profileImage" ,""+imageUri);
        }
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        pd.dismiss();
                        Toast.makeText(UpdateprofileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateprofileActivity.this, "Failed to update due to" +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loaduserinfo() {
        DatabaseReference reference;
        FirebaseDatabase database =FirebaseDatabase.getInstance();
        reference= database.getReference().child("users").child(FirebaseAuth.getInstance().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.profile)

                        .into(binding.updateImageView);

                binding.updateProfileET.setText(user.getName());




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==85 && data !=null){
            if (data.getData() !=null){
                binding.updateImageView.setImageURI(data.getData());

                imageUri=data.getData();
            }else {

                Toast.makeText(UpdateprofileActivity.this, "Cancalled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}