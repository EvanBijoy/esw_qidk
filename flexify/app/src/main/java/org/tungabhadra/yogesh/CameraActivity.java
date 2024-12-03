package org.tungabhadra.yogesh;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private PreviewView cameraPreviewView;
    private TextView exerciseTitleText;
    private ImageButton btnExit;
    private ImageButton btnToggleCamera;
    private ImageButton btnFlashToggle;

    private ExecutorService cameraExecutor;
    private String currentExercise;
    private boolean isFrontCamera = false;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    // Check and request camera permission
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission granted, start the camera
            startCamera();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                startCamera();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get exercise from intent
        currentExercise = getIntent().getStringExtra("EXERCISE");
        if (currentExercise == null) {
            Toast.makeText(this, "No exercise selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        cameraPreviewView = findViewById(R.id.camera_preview);
        exerciseTitleText = findViewById(R.id.exercise_title);
        btnExit = findViewById(R.id.btn_exit);
        btnToggleCamera = findViewById(R.id.btn_toggle_camera);
        btnFlashToggle = findViewById(R.id.btn_flash_toggle);

        // Set exercise title
        exerciseTitleText.setText(formatExerciseName(currentExercise));

        // Setup camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Setup button listeners
        setupButtonListeners();

        // Start camera
        startCamera();
    }

    private String formatExerciseName(String exercise) {
        switch (exercise) {
            case "BicepCurls": return "Bicep Curls";
            default: return exercise;
        }
    }

    private void setupButtonListeners() {
        // Exit button
        btnExit.setOnClickListener(v -> finish());

        // Camera toggle button
        btnToggleCamera.setOnClickListener(v -> {
            isFrontCamera = !isFrontCamera;
            startCamera();
        });

        // Flash toggle button (placeholder - you'll need to implement actual flash logic)
        btnFlashToggle.setOnClickListener(v -> {
            Toast.makeText(this, "Flash toggle not implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Setup preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());

                // Select camera
                CameraSelector cameraSelector = isFrontCamera
                        ? CameraSelector.DEFAULT_FRONT_CAMERA
                        : CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind any previous use cases
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview
                );

            } catch (Exception e) {
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}