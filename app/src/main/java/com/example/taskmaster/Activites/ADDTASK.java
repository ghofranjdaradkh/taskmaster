package com.example.taskmaster.Activites;

import static android.widget.Toast.LENGTH_SHORT;

import static java.util.Arrays.stream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
//import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.datastore.generated.model.TaskState;
import com.amplifyframework.datastore.generated.model.Team;

import com.example.taskmaster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.snackbar.Snackbar;
//import com.example.taskmaster.dataBase.TaskdataBase;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ADDTASK extends AppCompatActivity {
    //TaskdataBase taskdataBase;
    List<Task> TASKS = null;
    Spinner teamsSpinner = null;
    Spinner Statespinner = null;
    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    public static final String TAG = "AddTaskActivity";
    public static final String DATABASE_NAME = "NAME";
    FusedLocationProviderClient LocationProviderClient;
    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;
    Geocoder geocoder=null;
    private MediaPlayer mp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        teamFuture = new CompletableFuture<>();
        mp =new MediaPlayer();
        setUpSpinner();
        saveButton();
        setUpSpeakButton();

//Import Mainfest from the Android it self
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        LocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //to get last location
        LocationProviderClient.getLastLocation().addOnSuccessListener(location ->
        {
            if (location == null) {
                Log.e(TAG, "Location CallBack was null");
            }
            String currentLatitude = Double.toString(location.getLatitude());
            String currentLongitude = Double.toString(location.getLongitude());
            Log.i(TAG, "Our userLatitude: " + location.getLatitude());
            Log.i(TAG, "Our userLongitude: " + location.getLongitude());
        });
        LocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        });
// to translate the longtude and latitude to spasific location (adress)
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL); //get the locatoin every 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    //get the location
                    String address = geocoder.getFromLocation(
                                    locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(),
                                    1)
                            .get(0)
                            .getAddressLine(0);
                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };

        LocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());


        Toast toast = Toast.makeText(this, "submitted", LENGTH_SHORT);
        TextView addTask = findViewById(R.id.textBar);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ADDTASK.this, MainActivity.class);
                startActivity(intent1);

                toast.show();


            }

        });

        ImageView imageView = findViewById(R.id.arrowImage2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(ADDTASK.this, MainActivity.class);
                startActivity(intentArrow);
            }
        });

    }

    @Override
    protected void onResume() {


        super.onResume();

        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getType() != null && callingIntent.getType().equals("text/plain")) {
            String callingText = callingIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (callingText != null) {
                String cleanedText = cleanText(callingText);
                // Set the cleaned text in the UI
                ((EditText) findViewById(R.id.editTextdescription)).setText(cleanedText);
            }
        }

        if(callingIntent != null && callingIntent.getType() != null && callingIntent.getType().startsWith("image") ){
            Uri incomingImageFileUri= callingIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            if (incomingImageFileUri != null){
                InputStream incomingImageFileInputStream = null;

                try {
                    incomingImageFileInputStream = getContentResolver().openInputStream(incomingImageFileUri);

                    ImageView taskImageView = findViewById(R.id.newimageView);

                    if (taskImageView != null) {

                        taskImageView.setImageBitmap(BitmapFactory.decodeStream(incomingImageFileInputStream));
                    }else {
                        Log.e(TAG, "ImageView is null for some reasons");
                    }
                }catch (FileNotFoundException fnfe){
                    Log.e(TAG," Could not get file stream from the URI "+fnfe.getMessage(),fnfe);
                }
            }
        }

    }


    private String cleanText(String text) {
        // Remove links
        text = text.replaceAll("\\b(?:https?|ftp):\\/\\/\\S+\\b", "");

        // Remove double quotes
        text = text.replaceAll("\"", "");

        return text;

    }


    public void setUpSpinner() {

        teamsSpinner = findViewById(R.id.addteam);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Team Successfully");
                    ArrayList<String> teamName = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    if (success.getData() != null) {
                        for (Team team : success.getData()) {
                            teams.add(team);
                            teamName.add(team.getName());
                            Log.d(TAG, "setupSpinners() returned: " + team.getName());
                        }
                        teamFuture.complete(teams);
                        runOnUiThread(() -> {
                            teamsSpinner.setAdapter(new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    teamName
                            ));
                        });
                    } else {
                        Log.e(TAG, "Success response data is null");
                    }
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.e(TAG, "Failed to read teams successfully: " + failure.toString());
                }
        );


        Statespinner = (Spinner) findViewById(R.id.spinnerlsitforState);
        Statespinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskState.values()
        ));
    }


    public void saveButton() {

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            String name = ((EditText) findViewById(R.id.taskTitle)).getText().toString();
            String description = ((EditText) findViewById(R.id.editTextdescription)).getText().toString();
            String selectedTeamString = teamsSpinner.getSelectedItem().toString();
            List<Team> teams = null;
            try {
                teams = teamFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, " InterruptedException while getting teams");
            } catch (ExecutionException ee) {
                Log.e(TAG, " ExecutionException while getting teams");
            }

            assert teams != null;
            Team selectedTeam = teams.stream().filter(c -> c.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationProviderClient.getLastLocation().addOnSuccessListener(location ->
                            {
                                if (location == null) {
                                    Log.e(TAG, "Location CallBack was null");
                                }
                                String currentLatitude = Double.toString(location.getLatitude());
                                String currentLongitude = Double.toString(location.getLongitude());
                                Log.i(TAG, "Our userLatitude: " + location.getLatitude());
                                Log.i(TAG, "Our userLongitude: " + location.getLongitude());
                                saveProduct(name, description, currentLatitude, currentLongitude, selectedTeam);

                            }

                    ).addOnCanceledListener(() ->
                    {
                        Log.e(TAG, "Location request was Canceled");
                    })
                    .addOnFailureListener(failure ->
                    {
                        Log.e(TAG, "Location request failed, Error was: " + failure.getMessage(), failure.getCause());
                    })
                    .addOnCompleteListener(complete ->
                    {
                        Log.e(TAG, "Location request Completed");
                    });
        });

    }

    private void saveProduct(String name ,String description ,String latitude, String longitude, Team selectedTeam) {


        Task newTask = Task.builder()
                .name(name)
                .description(description)
                .state((TaskState) Statespinner.getSelectedItem())
                .teamPerson(selectedTeam)
                .taskLatitude(latitude)
                .taskLongitude(longitude)
                .build();

        Amplify.API.mutate(
                ModelMutation.create(newTask),
                successResponse -> {
                    Log.i(TAG, "AddTaskActivity.onCreate(): made a task successfully");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ADDTASK.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                },
                failureResponse -> {
                    Log.e(TAG, "AddTaskActivity.onCreate(): failed with this response" + failureResponse);
                    Toast.makeText(this, "Failed to add task. Please try again.", Toast.LENGTH_SHORT).show();
                }
        );
        Snackbar.make(findViewById(R.id.addactivity), "Task saved!", Snackbar.LENGTH_SHORT).show();

    }

private void setUpSpeakButton(){
    Button speakButton = (Button) findViewById(R.id.convertTxt);
    speakButton.setOnClickListener(b ->
    {
        String taskTitle= ((EditText) findViewById(R.id.editTextdescription)).getText().toString();

        Amplify.Predictions.convertTextToSpeech(
                taskTitle,
                result -> playAudio(result.getAudioData()),
                error -> Log.e(TAG,"conversion failed ", error)
        );
    });
}


    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e("MyAmplifyApp", "Error writing audio file", error);
        }
    }}