package org.dop.ex1;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private Button btnReadData, btnWrite;
    private EditText etData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReadData = findViewById(R.id.btnReadData);
        etData = findViewById(R.id.etData);
        btnWrite = findViewById(R.id.btnWrite);


        btnReadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readDataFromFile();
            }
        });
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeData();
            }
        });
    }

    private void readDataFromFile() {
        try {
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.myfile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            inputStream.close();
            etData.setText(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeData() {
        try {
            String data = etData.getText().toString();
            FileOutputStream outputStream = openFileOutput("myfile.txt", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();

            Toast.makeText(this, "Data written to file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
