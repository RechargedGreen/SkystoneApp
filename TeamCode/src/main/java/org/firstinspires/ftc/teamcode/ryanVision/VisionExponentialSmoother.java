package org.firstinspires.ftc.teamcode.ryanVision;

/**
 * This class implements a simple smoother based on an exponential moving average.
 */

public class VisionExponentialSmoother {

	private double smoothingFactor, average;
	private boolean hasUpdated;

	public VisionExponentialSmoother(double smoothingFactor) {
		this.smoothingFactor = smoothingFactor;
	}

	/**
	 * Update the smoother with a new data value
	 *
	 * @param newValue new data value
	 * @return the smoothed value
	 */
	public double update(double newValue) {
		if(!hasUpdated) {
			average = newValue;
			hasUpdated = true;
		} else {
			average = smoothingFactor * newValue + (1 - smoothingFactor) * average;
		}
		return average;
	}

	public void reset() {
		hasUpdated = false;
	}

	public void setSmoothingFactor(double smoothingFactor) {
		this.smoothingFactor = smoothingFactor;
	}

}
