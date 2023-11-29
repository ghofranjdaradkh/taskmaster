package com.example.taskmaster.Activites;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskState;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.Activites.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.example.taskmaster.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTask  extends AppCompatActivity {

    public static final String TAG= "editTaskActivity";
    private CompletableFuture<Task> taskCompletableFuture = null;
    private CompletableFuture<List<Team>> teamFuture = null;
    private Task taskToEdit= null;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner taskCategorySpinner = null;
    private Spinner teamNameSpinner = null;

//    ActivityResultLauncher<Intent> activityResultLauncher;

    private String s3ImageKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskCompletableFuture = new CompletableFuture<>();
        teamFuture = new CompletableFuture<>();

//        activityResultLauncher = getImagePickingActivityResultLauncher();  // You MUST set this up in onCreate() in the lifecycle

        setUpEditableUIElement();
        setUpSaveButton();
        setUpDeleteButton();
//        setUpAddImageButton();
        setUpDeleteImageButton();
        updateImageButtons();
    }

    private void setUpEditableUIElement() {
        Intent callingIntent = getIntent();
        String taskId = null;

        if(callingIntent != null){
            taskId = callingIntent.getStringExtra(MainActivity.Main_ID_TAG);
        }

        String taskId2 = taskId;

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG,"Read Tasks Successfully");

                    for (Task databaseTask: success.getData()){
                        if(databaseTask.getId().equals(taskId2)){
                            taskCompletableFuture.complete(databaseTask);
                        }
                    }

                    runOnUiThread(() ->
                    {
                        //Update UI element
                    });
                },
                failure -> Log.i(TAG, "Did not read Tasks successfully")
        );

        try {
            taskToEdit = taskCompletableFuture.get();
        }catch (InterruptedException ie){
            Log.e(TAG, "InterruptedException while getting task");
            Thread.currentThread().interrupt();
        }catch (ExecutionException ee){
            Log.e(TAG, "ExecutionException while getting task");
        }

        titleEditText = ((EditText) findViewById(R.id.editTaskNameEditText));
        titleEditText.setText(taskToEdit.getName());
        descriptionEditText = ((EditText) findViewById(R.id.editTextdescription));
        descriptionEditText.setText(taskToEdit.getDescription());

        s3ImageKey = taskToEdit.getTaskImageS3Key();
        if (s3ImageKey != null && !s3ImageKey.isEmpty())
        {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success ->
                    {
                        ImageView taskImageView = findViewById(R.id.addtaskimageView);
                        taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                    },
                    failure ->
                    {
                        Log.e(TAG, "Unable to get image from S3 for the Task for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
                    }
            );
        }
        setUpSpinners();
    }

    private void setUpSpinners()
    {
        teamNameSpinner = (Spinner) findViewById(R.id.editTeamSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i(TAG, "Read Team Name successfully!");
                    ArrayList<String> TeamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData())
                    {
                        teams.add(team);
                        TeamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() ->
                    {
                        teamNameSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                TeamNames));
                        teamNameSpinner.setSelection(getSpinnerIndex(teamNameSpinner, taskToEdit.getTeamPerson().getName()));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read Team Name successfully!");
                }
        );

        taskCategorySpinner = (Spinner) findViewById(R.id.editstateSpinner);
        taskCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskState.values()));
        taskCategorySpinner.setSelection(getSpinnerIndex(taskCategorySpinner, taskToEdit.getState().toString()));
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck){
        for (int i = 0;i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)){
                return i;
            }
        }
        return 0;
    }

    private void setUpSaveButton()
    {
        Button saveButton = (Button)findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(v ->
        {
            saveTask(s3ImageKey);
        });
    }

    private void saveTask(String imageS3Key)
    {
        List<Team> teams = null;
        String teamToSaveString = teamNameSpinner.getSelectedItem().toString();
        try
        {
            teams = teamFuture.get();
        }
        catch (InterruptedException ie)
        {
            Log.e(TAG, "InterruptedException while getting Task");
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ee)
        {
            Log.e(TAG, "ExecutionException while getting Task");
        }
        Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);

        Task taskToSave = Task.builder()
                .name(titleEditText.getText().toString())
                .id(taskToEdit.getId())
                .description(descriptionEditText.getText().toString())
                .teamPerson(teamToSave)
                .state(TaskCategoryFromString(taskCategorySpinner.getSelectedItem().toString()))

                .taskImageS3Key(imageS3Key)

                .build();

        Amplify.API.mutate(
                ModelMutation.update(taskToSave),  // making a GraphQL request to the cloud
                successResponse ->
                {
                    Log.i(TAG, "EditTaskActivity.onCreate(): edited a Task successfully");
                    Snackbar.make(findViewById(R.id.editTaskAcivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
                },  // success callback
                failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
        );
    }
//    private void setUpSaveButton()
//    {
//        Button saveButton = (Button)findViewById(R.id.editTaskButton);
//        saveButton.setOnClickListener(v ->
//        {
//            List<Team> teams = null;
//            String teamToSaveString = teamNameSpinner.getSelectedItem().toString();
//            try
//            {
//                teams = teamFuture.get();
//            }
//            catch (InterruptedException ie)
//            {
//                Log.e(TAG, "InterruptedException while getting product");
//                Thread.currentThread().interrupt();
//            }
//            catch (ExecutionException ee)
//            {
//                Log.e(TAG, "ExecutionException while getting product");
//            }
//            Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);
//            Task taskToSave = Task.builder()
//                    .title(titleEditText.getText().toString())
//                    .id(taskToEdit.getId())
//                    .description(descriptionEditText.getText().toString())
//                    .teamName(teamToSave)
//                    .taskStatusEnum(TaskCategoryFromString(taskCategorySpinner.getSelectedItem().toString()))
//                    .build();
//
//            Amplify.API.mutate(
//                    ModelMutation.update(taskToSave),  // making a GraphQL request to the cloud
//                    successResponse ->
//                    {
//                        Log.i(TAG, "EditTaskActivity.onCreate(): edited a Task successfully");
//                        // TODO: Display a Snack bar
//                        Snackbar.make(findViewById(R.id.editTaskAcivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
//                    },  // success callback
//                    failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
//            );
//        });
//    }

    public static TaskState TaskCategoryFromString(String inputTaskStateEnumText){
        for (TaskState taskStatusEnum : TaskState.values()){
            if(taskStatusEnum.toString().equals(inputTaskStateEnumText)){
                return taskStatusEnum;
            }
        }
        return null;
    }

    private void setUpDeleteButton(){
        Button deleteButton = (Button) findViewById(R.id.DeleteButton);
        deleteButton.setOnClickListener(v ->{
            Amplify.API.mutate(
                    ModelMutation.delete(taskToEdit),
                    successResponse ->
                    {
                        Log.i(TAG, "EditTaskActivity.onCreate(): deleted a Task successfully");
                        Intent goToMainActivity = new Intent(EditTask.this, MainActivity.class);
                        startActivity(goToMainActivity);
                    },
                    failureResponse -> Log.i(TAG,"EditTaskActivity.onCreate(): failed with this response: "+ failureResponse)
            );
        });
    }


//    private void setUpAddImageButton()
//    {
//        Button addImageButton = (Button) findViewById(R.id.addImageButton);
//        addImageButton.setOnClickListener(b ->
//        {
//            launchImageSelectionIntent();
//        });
//
//    }
//
//    private void launchImageSelectionIntent()
//    {
//        // Part 1: Launch activity to pick file
//
//        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        imageFilePickingIntent.setType("*/*");  // only allow one kind or category of file; if you don't have this, you get a very cryptic error about "No activity found to handle Intent"
//        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
//        // Below is simple version for testing
//        //startActivity(imageFilePickingIntent);
//
//        // Part 2: Create an image picking activity result launcher
//        activityResultLauncher.launch(imageFilePickingIntent);
//
//    }

//    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
//    {
//        // Part 2: Create an image picking activity result launcher
//        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
//                registerForActivityResult(
//                        new ActivityResultContracts.StartActivityForResult(),
//                        new ActivityResultCallback<ActivityResult>()
//                        {
//                            @Override
//                            public void onActivityResult(ActivityResult result)
//                            {
//                                Button addImageButton = findViewById(R.id.addImageButton);
//                                if (result.getResultCode() == Activity.RESULT_OK)
//                                {
//                                    if (result.getData() != null)
//                                    {
//                                        Uri pickedImageFileUri = result.getData().getData();
//                                        try
//                                        {
//                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
//                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
//                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
//                                            // Part 3: Use our InputStream to upload file to S3
//                                            switchFromAddButtonToDeleteButton(addImageButton);
//                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);
//
//                                        } catch (FileNotFoundException fnfe)
//                                        {
//                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
//                                        }
//                                    }
//                                }
//                                else
//                                {
//                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
//                                }
//                            }
//                        }
//                );
//
//        return imagePickingActivityResultLauncher;
//    }

//    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename,Uri pickedImageFileUri)
//    {
//        Amplify.Storage.uploadInputStream(
//                pickedImageFilename,  // S3 key
//                pickedImageInputStream,
//                success ->
//                {
//                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
//                    // Part 4: Update/save our Product object to have an image key
//                    saveTask(success.getKey());
//                    updateImageButtons();
//                    ImageView productImageView = findViewById(R.id.addtaskimageView);
//                    InputStream pickedImageInputStreamCopy = null;  // need to make a copy because InputStreams cannot be reused!
//                    try
//                    {
//                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
//                    }
//                    catch (FileNotFoundException fnfe)
//                    {
//                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
//                    }
//                    productImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));
//
//                },
//                failure ->
//                {
//                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
//                }
//        );
//    }

    private void setUpDeleteImageButton()
    {
        Button deleteImageButton = (Button)findViewById(R.id.dleteImage);
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
            ImageView productImageView = findViewById(R.id.addtaskimageView);
            productImageView.setImageResource(android.R.color.transparent);

            saveTask("");
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }

    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.addImageButton);
        Button deleteImageButton = findViewById(R.id.dleteImage);
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
        Button addImageButton = findViewById(R.id.addImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }

    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.dleteImage);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }
//    // Taken from https://stackoverflow.com/a/25005243/16889809
//    @SuppressLint("Range")
//    public String getFileNameFromUri(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }

}