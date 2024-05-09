package org.dop.ex3;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.Manifest;

import org.dop.ex3.Model.User;


public class MainActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText emailField;
    private EditText phoneField;
    private RadioGroup genderGroup;
    private Button saveButton, cancelButton, changeButton;
    private ImageView imageView;

    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri imageUri;
    private static final String FILENAME = "user.txt";
    private static final int CAMERA_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        genderGroup = findViewById(R.id.genderGroup);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        changeButton = findViewById(R.id.changeButton);
        imageView = findViewById(R.id.userImage);
        imageUri = createUri();

        registerPictureLauncher();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String phone = phoneField.getText().toString();
                int genderId = genderGroup.getCheckedRadioButtonId();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || genderId == -1) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton genderButton = findViewById(genderId);
                    String gender = genderButton.getText().toString();

                    User user = new User(name, email, phone, gender);
                    writeUserToFile(MainActivity.this, FILENAME, user);

                    Toast.makeText(MainActivity.this, "User saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(MainActivity.this, "Exit!", Toast.LENGTH_SHORT).show();
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndOpenCamera();
            }
        });

        User user = readUserFromFile(this, FILENAME);
        if (user != null) {
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            if (user.getGender().equals("Male")) {
                genderGroup.check(R.id.maleRadio);
            } else {
                genderGroup.check(R.id.femaleRadio);
            }
        }

        // Load the saved image, if it exists
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        if (imageFile.exists()) {
            Uri imageUri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    "com.taturo.exercise3.fileProvider",
                    imageFile
            );
            imageView.setImageURI(imageUri);
        }
    }

    public static User readUserFromFile(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line = reader.readLine();
            if (line != null) {
                String[] values = line.split(",");
                return new User(values[0], values[1], values[2], values[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void writeUserToFile(Context context, String filename, User user) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            String line = user.getName() + "," + user.getEmail() + "," + user.getPhone() + "," + user.getGender();
            writer.write(line);

            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//

    private Uri createUri() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.taturo.exercise3.fileProvider",
                imageFile
        );
    }

    private void registerPictureLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        try {
                            if(result) {
                                imageView.setImageURI(null);
                                imageView.setImageURI(imageUri);
                            }
                        } catch (Exception exception) {
                            exception.getStackTrace();
                        }
                    }
                }
        );
    }

    private void checkCameraPermissionAndOpenCamera() {
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            takePictureLauncher.launch(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(imageUri);
            } else {
                Toast.makeText(this, "Camera permission denied, please allow permission to take picture", Toast.LENGTH_SHORT).show();
            }
        }
    }
}