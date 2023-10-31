package com.example.travel_assistant.activity;

import static org.apache.commons.lang3.ClassUtils.getPackageName;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.example.travel_assistant.fragments.LoginFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthFragmentContainer extends AppCompatActivity {

    Button loginBtn, registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_fragment_container);

        //getData();
        //getString("NYC");

    }

    public void getString(String iata){

        String resourceName = iata; // The resource name you want to access
        int resourceId = getResources().getIdentifier(resourceName, "string", getPackageName());

        if (resourceId != 0) {
            String resourceValue = getResources().getString(resourceId);
            // Use the resourceValue as needed
            Log.d("getString", "resourceValue: " + resourceValue);
        } else {
            // Handle the case where the resource is not found
            Log.d("getString", "can't get string ");
        }

    }

    public void getData(){

        //code to get the iata data from csv
        try {
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.iata_airport);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder data = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // Split the CSV line by the semicolon
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    // Get data from the third column (index 2)
                    String thirdColumnData = parts[2].replaceAll("\"", "");
                    // Get data from the fifth column (index 4)
                    String fifthColumnData = parts[4];

                    // Format the data to be input into the strings.xml
                    String formattedData = "<string name=\"" + thirdColumnData + "\">" + fifthColumnData + "</string>\n";
                    data.append(formattedData);
                }
            }


            Log.d("getData", "data: " + data);

            String dataToExport = data.toString();


            // add-write text into file
            try {
                FileOutputStream fileout=openFileOutput("mytextfile.txt", MODE_PRIVATE);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                outputWriter.write(data.toString());
                outputWriter.close();
                //display file saved message
                Toast.makeText(getBaseContext(), "File saved successfully!",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }


            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}