package org.tungabhadra.yogesh.helpers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.SurfaceView;

public class OverlayHelper {

    private final SurfaceView overlaySurfaceView;
    private final Paint paint;

    public OverlayHelper(SurfaceView overlaySurfaceView) {
        this.overlaySurfaceView = overlaySurfaceView;
        paint = new Paint();
        paint.setColor(0xFFFF0000); // Red color for keypoints
        paint.setStrokeWidth(10);
    }

    public void drawKeypoints(PointF[] keypoints) {
        Canvas canvas = overlaySurfaceView.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR); // Clear the canvas
            for (PointF point : keypoints) {
                canvas.drawCircle(point.x, point.y, 10, paint);
            }
            overlaySurfaceView.getHolder().unlockCanvasAndPost(canvas);
        }
    }
}
