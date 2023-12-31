package com.example.taskmaster.Activites;

import static com.example.taskmaster.Activites.MainActivity.Main_ID_TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskDetailPage extends AppCompatActivity {
    public static final String TAG= "TaskDetailsActivity";
    private String s3ImageKey = "";
    private MediaPlayer mp = null;

//lunch activity and take the result
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_page);
        activityResultLauncher = getImagePickingActivityResultLauncher();
        mp =new MediaPlayer();
        setUpAddImageButton();
        setUpDeleteImageButton();
        setUpSaveButton();
setUpSpeakButton();

    }





    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        ImageView imageView = findViewById(R.id.arrowImage);
        Intent intent = getIntent();
        if (intent != null) {
            String taskTitle = intent.getStringExtra("taskTitle");
            String taskBody=intent.getStringExtra("taskBody");
            String taskState = intent.getStringExtra("taskState");

            if (taskTitle != null && taskState != null) {
                titleTextView.setText(taskBody+"\n"+taskTitle +"\n");

                descriptionTextView.setText(taskState);
            } else {
                Log.e("TaskDetailPage", "Title or state is null");
            }
        } else {
            Log.e("TaskDetailPage", "Intent is null");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(TaskDetailPage.this, MainActivity.class);
                startActivity(intentArrow);
            }
        });
    }






    private void setUpAddImageButton()
    {
        Button addImageButton = (Button) findViewById(R.id.addImageButton2);
        addImageButton.setOnClickListener(b ->
        {
            launchImageSelectionIntent();
        });

    }

    private void launchImageSelectionIntent()
    {
        // Part 1: Launch activity to pick file

        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");  // only allow one kind or category of file; if you don't have this, you get a very cryptic error about "No activity found to handle Intent"
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
        // Below is simple version for testing
        //startActivity(imageFilePickingIntent);

        // Part 2: Create an image picking activity result launcher
        activityResultLauncher.launch(imageFilePickingIntent);

    }



    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        // Part 2: Create an image picking activity result launcher
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                //to take the result use >>  registerForActivityResult
                //deal with contract >>to strat the activity
                registerForActivityResult(
                        //setup contract to setup activity
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>()
                        {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {
                                Button addImageButton = findViewById(R.id.addImageButton);
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    if (result.getData() != null)
                                    {
                                        //uniform resource
                                        /* i need th Uri because i cant upload the image in direct way , i need to convert the image to input stream
                                        and then upload the image to s3 package */
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try
                                        {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            //to extract the content from uri use this method getFileNameFromUri
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            // Part 3: Use our InputStream to upload file to S3
//                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);

                                        } catch (FileNotFoundException fnfe)
                                        {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }


    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename,Uri pickedImageFileUri)
    {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    // Part 4: Update/save our Product object to have an image key
                    saveTask(success.getKey());
//                    updateImageButtons();
                    ImageView productImageView = findViewById(R.id.addtaskimageView2);
                    InputStream pickedImageInputStreamCopy = null;  // need to make a copy because InputStreams cannot be reused!
                    try
                    {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }
                    productImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));

                },
                failure ->
                {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }



    private void setUpSaveButton()
    {
        Button saveButton = (Button)findViewById(R.id.Savebutton2);
        saveButton.setOnClickListener(v ->
        {
            saveTask(s3ImageKey);
        });
    }

    private void saveTask(String imageS3Key) {
        Amplify.API.query(
                ModelQuery.get(Task.class, Main_ID_TAG),
                response -> {
                    Task existingTask = response.getData();

                    if (existingTask != null) {

                        String existingTaskId = existingTask.getId();
                       String nameTask=existingTask.getName();
                        // Create a new task with the updated information
                        Task taskToSave = Task.builder()
                                .name(nameTask)
                                .id(existingTaskId)
                                .taskImageS3Key(imageS3Key)
                                .build();

                        // Perform the update mutation
                        Amplify.API.mutate(
                                ModelMutation.update(taskToSave),
                                successResponse -> {
                                    Log.i(TAG, "EditTaskActivity.onCreate(): edited a Task successfully");
                                    Snackbar.make(findViewById(R.id.editTaskAcivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
                                },
                                failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)
                        );
                    } else {
                        // Handle the case where the task was not found
                        Log.e(TAG, "Task not found");
                    }
                },
                error -> Log.e(TAG, "Error fetching Task: " + error)
        );

    }


    private void setUpDeleteImageButton()
    {
        Button deleteImageButton = (Button)findViewById(R.id.deleteImage2);
        String s3ImageKey = this.s3ImageKey;
        deleteImageButton.setOnClickListener(v ->
        {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success ->
                    {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());

                    },
                    failure ->
                    {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );
            ImageView productImageView = findViewById(R.id.addtaskimageView2);
            productImageView.setImageResource(android.R.color.transparent);

            saveTask("");
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }

    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.addImageButton2);
        Button deleteImageButton = findViewById(R.id.deleteImage2);
        runOnUiThread(() -> {
            if (s3ImageKey == null || s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void switchFromDeleteButtonToAddButton(Button deleteImageButton) {
        Button addImageButton = findViewById(R.id.addImageButton2);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }

    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.deleteImage2);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }

    // Taken from https://stackoverflow.com/a/25005243/16889809
    //this code from the stackoverflow to extract the content itself from input string
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void setUpSpeakButton(){
        Button speakButton = (Button) findViewById(R.id.button2convert);
        speakButton.setOnClickListener(b ->
        {
            String taskTitle = ((TextView) findViewById(R.id.titleTextView)).getText().toString();


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
    }
}









