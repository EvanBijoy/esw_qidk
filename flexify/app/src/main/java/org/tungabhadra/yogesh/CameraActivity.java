package org.tungabhadra.yogesh;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.qualcomm.qti.snpe.NeuralNetwork;
import org.tungabhadra.yogesh.helpers.CameraHelper;
import org.tungabhadra.yogesh.helpers.ModelHelper;

public class CameraActivity extends AppCompatActivity implements CameraHelper.PoseDetectionListener {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int POSE_CONFIRMATION_TIME = 3000; // 3 seconds

    private NeuralNetwork neuralNetwork;
    private CameraHelper cameraHelper;
    private ModelHelper modelHelper;
    private YogaSequenceManager sequenceManager;
    private TextView poseNameText;
    private TextView poseProgressText;
    private Button nextPoseButton;
    private Handler poseConfirmationHandler;
    private boolean isPoseConfirmed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Get the yoga set type from intent
        String yogaSet = getIntent().getStringExtra("YOGA_SET");

        // Initialize sequence manager
        sequenceManager = new YogaSequenceManager(yogaSet);

        // Initialize handler for pose confirmation
        poseConfirmationHandler = new Handler();

        // Initialize UI elements
        poseNameText = findViewById(R.id.poseNameText);
        poseProgressText = findViewById(R.id.poseProgressText);
        nextPoseButton = findViewById(R.id.nextPoseButton);

        nextPoseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextPose();
            }
        });

        // Initially disable the next pose button until pose is confirmed
        nextPoseButton.setEnabled(false);

        // Update UI with initial pose
        updatePoseDisplay();

        if (checkCameraPermission()) {
            initializeComponents();
        } else {
            requestCameraPermission();
        }
    }

    private void initializeComponents() {
        // Initialize model
        modelHelper = new ModelHelper();
        try {
            neuralNetwork = modelHelper.loadModel(getApplication());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load neural network", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize views
        SurfaceView cameraSurfaceView = findViewById(R.id.cameraSurfaceView);
        SurfaceView overlaySurfaceView = findViewById(R.id.overlaySurfaceView);

        // Initialize camera helper with both surfaces
        cameraHelper = new CameraHelper(this, cameraSurfaceView, overlaySurfaceView, neuralNetwork);
        cameraHelper.setSequenceManager(sequenceManager);
        cameraHelper.setPoseDetectionListener(this);
        cameraHelper.startCamera();
    }

    @Override
    public void onPoseDetected(String detectedPose) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YogaSequenceManager.YogaPose currentPose = sequenceManager.getCurrentPose();
                if (currentPose != null && detectedPose.equals(currentPose.getName())) {
                    if (!isPoseConfirmed) {
                        // Start the confirmation timer
                        poseConfirmationHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isPoseConfirmed = true;
                                nextPoseButton.setEnabled(true);
                                Toast.makeText(CameraActivity.this,
                                        "Pose confirmed! You can move to the next pose.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, POSE_CONFIRMATION_TIME);
                    }
                } else {
                    // Reset confirmation if wrong pose is detected
                    isPoseConfirmed = false;
                    nextPoseButton.setEnabled(false);
                    poseConfirmationHandler.removeCallbacksAndMessages(null);
                }
            }
        });
    }

    @Override
    public void onPoseConfirmed(String confirmedPose) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this,
                        "Great job! Pose maintained!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePoseDisplay() {
        YogaSequenceManager.YogaPose currentPose = sequenceManager.getCurrentPose();
        if (currentPose != null) {
            poseNameText.setText(currentPose.getName());
            poseProgressText.setText(String.format("Pose %d/%d",
                    sequenceManager.getCurrentPoseNumber(),
                    sequenceManager.getTotalPoses()));

            // Reset pose confirmation for new pose
            isPoseConfirmed = false;
            nextPoseButton.setEnabled(false);

            // If this is the last pose, change button text
            if (sequenceManager.isSequenceComplete()) {
                nextPoseButton.setText("Finish");
            }
        }
    }

    private void moveToNextPose() {
        if (sequenceManager.isSequenceComplete()) {
            finish(); // Return to main activity
        } else {
            sequenceManager.moveToNextPose();
            updatePoseDisplay();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CAMERA
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeComponents();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (neuralNetwork != null) {
            neuralNetwork.release();
        }
        if (cameraHelper != null) {
            cameraHelper.shutdown();
        }
        // Remove any pending callbacks
        poseConfirmationHandler.removeCallbacksAndMessages(null);
    }
}
