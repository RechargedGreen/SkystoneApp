package org.firstinspires.ftc.teamcode.ryanVision;

import android.app.*;
import android.content.*;
import android.support.annotation.*;
import android.util.*;

import com.qualcomm.robotcore.eventloop.opmode.*;

import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.robotcore.internal.opmode.*;
import org.firstinspires.ftc.robotcore.internal.system.*;
import org.opencv.android.*;
import org.opencv.core.*;

import java.util.*;
import java.util.concurrent.*;

public abstract class VisionCamera implements OpModeManagerNotifier.Notifications {
	public static final String TAG = "VisionCamera";

	public static class Parameters {
		@IdRes
		public int                              cameraMonitorViewId;
		public VuforiaLocalizer.CameraDirection cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

		public Parameters() {
			Context context = AppUtil.getDefContext();
			cameraMonitorViewId = context.getResources().getIdentifier("cameraMonitorViewId", "id", context.getPackageName());
		}
	}

	protected final List<Tracker> trackers;

	protected AppUtil           appUtil = AppUtil.getInstance();
	protected Activity          activity;
	protected OpModeManagerImpl opModeManager;

	protected Parameters parameters;

	private boolean initialized;

	public VisionCamera(Parameters parameters) {
		this.parameters = parameters;
		this.activity = appUtil.getActivity();
		this.trackers = new ArrayList<>();
		opModeManager = OpModeManagerImpl.getOpModeManagerOfActivity(activity);
		if(opModeManager != null) {
			opModeManager.registerListener(this);
		}
	}

	public void initialize() {
		if(!initialized) {
			final CountDownLatch openCvInitialized = new CountDownLatch(1);

			final BaseLoaderCallback loaderCallback = new BaseLoaderCallback(activity) {
				@Override
				public void onManagerConnected(int status) {
					switch(status) {
						case LoaderCallbackInterface.SUCCESS: {
							Log.i(TAG, "OpenCV loaded successfully");
							openCvInitialized.countDown();
							break;
						}
						default: {
							super.onManagerConnected(status);
							break;
						}
					}
				}
			};

			appUtil.runOnUiThread(() -> OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, activity, loaderCallback));

			try {
				openCvInitialized.await();
			} catch(InterruptedException e) {
				Log.w(TAG, e);
			}

			doInitialize();

			synchronized(trackers) {
				for(Tracker tracker : trackers) {
					tracker.init(this);
				}

				initialized = true;
			}
		}
	}

	public void addTracker(Tracker tracker) {
		synchronized(trackers) {
			this.trackers.add(tracker);
		}

		if(initialized) {
			tracker.init(this);
		}
	}

	public List<Tracker> getTrackers() {
		return trackers;
	}

	protected void onFrame(Mat frame, double timestamp) {
		synchronized(trackers) {
			for(Tracker tracker : trackers) {
				tracker.internalProcessFrame(frame, timestamp);
			}
		}
	}

	@Override
	public void onOpModePreInit(OpMode opMode) {

	}

	@Override
	public void onOpModePreStart(OpMode opMode) {

	}

	@Override
	public void onOpModePostStop(OpMode opMode) {
		close();
		if(opModeManager != null) {
			opModeManager.unregisterListener(this);
		}
	}

	protected abstract void doInitialize();

	public abstract void close();

	public abstract Properties getProperties();

	public interface Properties {
		/**
		 * @return camera's horizontal (along x-axis) focal length in pixels
		 */
		double getHorizontalFocalLengthPx(double imageWidth);
	}
}
