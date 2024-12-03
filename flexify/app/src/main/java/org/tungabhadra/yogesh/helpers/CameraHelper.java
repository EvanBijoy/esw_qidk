package org.tungabhadra.yogesh.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.qualcomm.qti.snpe.NeuralNetwork;
import org.tungabhadra.yogesh.YogaSequenceManager;

public class CameraHelper implements SurfaceHolder.Callback {
    private final Context context;
    private final SurfaceView cameraSurfaceView;
    private final SurfaceView overlaySurfaceView;
    private final NeuralNetwork neuralNetwork;
    private final Paint paint;
    private YogaSequenceManager sequenceManager;
    private PoseDetectionListener poseDetectionListener;

    public interface PoseDetectionListener {
        void onPoseDetected(String detectedPose);
        void onPoseConfirmed(String confirmedPose);
    }

    public CameraHelper(Context context, SurfaceView cameraSurfaceView,
                        SurfaceView overlaySurfaceView, NeuralNetwork neuralNetwork) {
        this.context = context;
        this.cameraSurfaceView = cameraSurfaceView;
        this.overlaySurfaceView = overlaySurfaceView;
        this.neuralNetwork = neuralNetwork;

        // Initialize paint for drawing
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        paint.setTextSize(48f);

        // Set up surface holders
        cameraSurfaceView.getHolder().addCallback(this);
        overlaySurfaceView.getHolder().setFormat(android.graphics.PixelFormat.TRANSPARENT);
        overlaySurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                drawOverlay();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                drawOverlay();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    public void setSequenceManager(YogaSequenceManager sequenceManager) {
        this.sequenceManager = sequenceManager;
    }

    public void setPoseDetectionListener(PoseDetectionListener listener) {
        this.poseDetectionListener = listener;
    }

    private void drawOverlay() {
        if (sequenceManager != null) {
            YogaSequenceManager.YogaPose currentPose = sequenceManager.getCurrentPose();
            if (currentPose != null) {
                Canvas canvas = overlaySurfaceView.getHolder().lockCanvas();
                if (canvas != null) {
                    try {
                        // Clear the canvas
                        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);

                        // Draw the target pose name
                        String targetPose = "Target Pose: " + currentPose.getName();
                        canvas.drawText(targetPose, 50, 100, paint);

                        // You can add more overlay drawing here
                    } finally {
                        overlaySurfaceView.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    // Add this method to handle pose detection results
    public void onPoseDetected(String detectedPose) {
        if (sequenceManager != null && poseDetectionListener != null) {
            YogaSequenceManager.YogaPose currentPose = sequenceManager.getCurrentPose();
            if (currentPose != null && detectedPose.equals(currentPose.getName())) {
                poseDetectionListener.onPoseConfirmed(detectedPose);
            }
            poseDetectionListener.onPoseDetected(detectedPose);
        }
        updateOverlay(detectedPose);
    }

    private void updateOverlay(String detectedPose) {
        Canvas canvas = overlaySurfaceView.getHolder().lockCanvas();
        if (canvas != null) {
            try {
                // Clear the canvas
                canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);

                // Draw the detected pose
                canvas.drawText("Detected: " + detectedPose, 50, 200, paint);

                // Draw the target pose if available
                if (sequenceManager != null) {
                    YogaSequenceManager.YogaPose currentPose = sequenceManager.getCurrentPose();
                    if (currentPose != null) {
                        String targetPose = "Target: " + currentPose.getName();
                        canvas.drawText(targetPose, 50, 100, paint);
                    }
                }
            } finally {
                overlaySurfaceView.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    // Implement remaining SurfaceHolder.Callback methods
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Initialize camera
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Clean up camera resources
        shutdown();
    }

    public void startCamera() {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            // Add your camera initialization code here
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        // Add your cleanup code here
    }
}