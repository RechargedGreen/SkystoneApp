package org.firstinspires.ftc.teamcode.ryanVision;


import org.opencv.core.*;

import java.util.*;

public class FpsTracker extends Tracker {
	private VisionExponentialSmoother smoother;
	private double                    lastTimestamp, avgTimeDelta;

	public FpsTracker() {
		smoother = new VisionExponentialSmoother(0.2);
	}

	@Override
	public void init(VisionCamera camera) {

	}

	@Override
	public void processFrame(Mat frame, double timestamp) {
		if(lastTimestamp != 0) {
			double timeDelta = timestamp - lastTimestamp;
			avgTimeDelta = smoother.update(timeDelta);
		}
		lastTimestamp = timestamp;
	}

	@Override
	public void drawOverlay(Overlay overlay, int imageWidth, int imageHeight, boolean debug) {
		overlay.putText(String.format(Locale.ENGLISH, "%.2f FPS", 1 / avgTimeDelta), Overlay.TextAlign.RIGHT, new Point(imageWidth - 5, 45), new Scalar(0, 0, 255), 45);
	}
}
