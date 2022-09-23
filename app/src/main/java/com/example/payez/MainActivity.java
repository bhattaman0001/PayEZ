package com.example.payez;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button capture;
    private TextInputEditText cardEdit, expiryMonth, expiryYear;
    private ExtendedFloatingActionButton fab;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capture = findViewById(R.id.captureBtn);
        cardEdit = findViewById(R.id.cardNumber);
        expiryMonth = findViewById(R.id.expiryMonth);
        expiryYear = findViewById(R.id.expiryYear);
        fab = findViewById(R.id.fabScan);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            int PERMISSION_CODE = 100;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, PERMISSION_CODE);
        }

        capture.setOnClickListener(view -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this));

        fab.setOnClickListener(view -> {
            Toast.makeText(this, "Coming Soon!!", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    getText(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getText(Bitmap bitmap){
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if(!recognizer.isOperational()){
            Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0;i<textBlockSparseArray.size();i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            int n = stringBuilder.length();
            String number="";
            for(int i=0;i<n;i++){
                if(stringBuilder.charAt(i) >= '0' && stringBuilder.charAt(i) <= '9'){
                    for(int j=i;j<20;j++){
                        if(stringBuilder.charAt(j) >= '0' && stringBuilder.charAt(i) <= '9')
                            number += stringBuilder.charAt(j);
                    }
                    cardEdit.setText(number);
                }else if(stringBuilder.charAt(i)=='/'){
                    int f = 1;
                    for(int j=i+1;j<n;j++){
                        if(stringBuilder.charAt(j) == '/'){
                            f = 0;
                            String month = stringBuilder.charAt(j-2) + stringBuilder.charAt(j-1) + "";
                            String year = stringBuilder.charAt(j+1) + stringBuilder.charAt(j+2) + "";
                            expiryMonth.setText(month);
                            expiryYear.setText(year);
                            break;
                        }
                    }
                    if(f == 1){
                        String month = stringBuilder.charAt(i-2) + stringBuilder.charAt(i-1) + "";
                        String year = stringBuilder.charAt(i+1) + stringBuilder.charAt(i+2) + "";
                        expiryMonth.setText(month);
                        expiryYear.setText(year);
                    }
                    break;
                }
            }
            capture.setText("Retake");
        }
    }
}