package org.firstinspires.ftc.teamcode.ryanVision;

import org.opencv.core.*;

public interface Overlay {
	enum TextAlign {
		LEFT,
		CENTER,
		RIGHT
	}

	void strokeRect(Rect rect, Scalar color, int thickness);

	void fillRect(Rect rect, Scalar color);

	void strokeLine(Point p1, Point p2, Scalar color, int thickness);

	void putText(String text, TextAlign align, Point org, Scalar color, int fontSize);

	void strokeContour(MatOfPoint contour, Scalar color, int thickness);

	void fillContour(MatOfPoint contour, Scalar color);

	void strokeCircle(Point center, double radius, Scalar color, int thickness);

	void fillCircle(Point center, double radius, Scalar color);

	double getScalingFactor();

	void setScalingFactor(double factor);
}
