package com.example.louisa.savemore;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;


public class AddSavingGoals extends BaseActivity
{
    @Bind(R.id.name) EditText name;
    @Bind(R.id.email) EditText email;
    @Bind(R.id.price) EditText price;

    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.btn_save) Button btn_save;
    @Bind(R.id.btn_capture) ImageButton btn_capture;

    DatabaseReference databaseRef;
    String key;
    String senderEmail;
    String productName;
    String receiverEmail;
    float amountToShare;
    float totalAmount;

    private static final int PICS_FROM_GALLERY = 1;
    private static final int PICS_FROM_CAMERA = 0;
    protected static Uri mImageCaptureUri;
    Uri selectedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_cost);

        ButterKnife.bind(this);

        databaseRef = mDatabase.getReference("sharedCost");
        key = databaseRef.push().getKey();
        setClickEvents();


    }


    private void setClickEvents(){
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSave();
            }
        });

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraProcess();

                //startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            }
        });

    }

    private void startCameraProcess()
    {
        /*ActivityCompat.requestPermissions(AddSharedCost.this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                1);*/
        capturePicture();
       /* Log.d("camera","btn_capture was clicked");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //btn_capture.setEnabled(false);
            Log.d("camera","No Permission");

            ActivityCompat.requestPermissions(AddSharedCost.this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else
        {
            Log.d("camera","capturePicture called");
            capturePicture();
        }*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        Log.d("camera","requestCode");

        if (requestCode == 0) {
            Log.d("camera","requestCode = 0");

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d("camera","permission granted");

                capturePicture();
            }else{
                Log.d("camera","Permissions not granted");
            }
        }else{
            Log.d("camera","Request code is not 0");
        }
    }

    private void capturePicture ()
    {
        Log.d("camera","Calling capturePicture method");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        startActivityForResult(intent, PICS_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICS_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageURI(mImageCaptureUri);

            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }


    private void processSave()
    {

        if(validateFields())
        {
            //String loginUserId = mAuth.getCurrentUser();
            senderEmail = mAuth.getCurrentUser().getEmail();

            productName = name.getText().toString();
            receiverEmail = email.getText().toString();
            totalAmount = Float.parseFloat(price.getText().toString());
            //mDatabase.getReference("sharedCost").child (productName).child(receiverEmail).child(amountToShare)

            saveCosts();
            //saveCosts(receiverEmail,productName,senderEmail,amountToShare);
        }
    }


    private void saveCosts(){
        amountToShare = totalAmount/2;

        SharedCost sharedCost = new SharedCost();
        sharedCost.setEmail(receiverEmail);
        sharedCost.setName(productName);
        sharedCost.setPrice(amountToShare);
        sharedCost.setSender_email(senderEmail);
        sharedCost.setTotal_amount(totalAmount);

        senderEmail = cleanEmail(senderEmail);

        Map<String, Object> shareValues = sharedCost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, shareValues);

        databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Cost Shared",Toast.LENGTH_LONG).show();
                    databaseRef.child(key).child(senderEmail).setValue(true);
                    receiverEmail = cleanEmail(receiverEmail);
                    databaseRef.child(key).child(receiverEmail).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                    //finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Unable to save at the moment",Toast.LENGTH_LONG).show();
                }
            }
        });
        databaseRef.setPriority(ServerValue.TIMESTAMP);
    }


    private boolean validateFields()
    {
        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("Please enter a name");
            return false;
        }else if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Please enter a valid email address");
            return false;
        }else if(TextUtils.isEmpty(price.getText().toString())){
            price.setError("Please enter a valid price");
            return false;
        }else{
            return true;
        }
    }
}