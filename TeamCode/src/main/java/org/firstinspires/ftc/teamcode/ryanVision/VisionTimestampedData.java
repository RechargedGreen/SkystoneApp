package org.firstinspires.ftc.teamcode.ryanVision;

public class VisionTimestampedData<T> {
	public final T      data;
	/**
	 * timestamp in seconds on the System#nanoTime() timescale
	 */
	public final double timestamp;

	public static double getCurrentTime() {
		return System.nanoTime() / Math.pow(10, 9);
	}

	public VisionTimestampedData(T data) {
		this(data, getCurrentTime());
	}

	/**
	 * timestamp in seconds on the System#nanoTime() timescale
	 */
	public VisionTimestampedData(T data, double timestamp) {
		this.data = data;
		this.timestamp = timestamp;
	}
}
