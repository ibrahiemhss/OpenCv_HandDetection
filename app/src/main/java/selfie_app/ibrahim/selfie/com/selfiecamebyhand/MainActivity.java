package selfie_app.ibrahim.selfie.com.selfiecamebyhand;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, Runnable {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    private static final String TAG = "OCVSample::Activity";
    private static final Scalar CLOSED_HAND_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private static final Scalar OPENED_HAND_RECT_COLOR = new Scalar(0, 255, 0, 255);

    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private static final int TM_SQDIFF = 0;
    private static final int TM_SQDIFF_NORMED = 1;
    private static final int TM_CCOEFF = 2;
    private static final int TM_CCOEFF_NORMED = 3;
    private static final int TM_CCORR = 4;
    private static final int TM_CCORR_NORMED = 5;

    private int learn_frames = 0;
    private Mat teplateR;
    private Mat teplateL;
    int method = 0;


    private volatile boolean running = false;
    private volatile int qtdClosedHand;
    private volatile int qtdOpenedHand;


    private MatOfRect mOpenHand;
    private MatOfRect mCloseHand;
    // matrix for zooming
    private Mat mZoomWindow;
    private Mat mZoomWindow2;

    private MenuItem mItemClosedHand50;
    private MenuItem mItemClosedHand40;
    private MenuItem mItemClosedHand30;
    private MenuItem mItemClosedHand20;
    private MenuItem mItemType;

    private Mat mRgba;
    private Mat mGray;
    private File mClosedHandCascadeFile;
    private File mOpenedHandCascadeFile;
    private CascadeClassifier mJavaDetectorOpenedHand;
    private CascadeClassifier mJavaDetectorClosedHand;
    private DetectionBasedTracker mNativeDetectorOpenedHand;
    private DetectionBasedTracker mNativeDetectorClosedHand;

    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;

    private float mRelativeOpenHandSize = 0.2f;
    private int mAbsoluteOpenHandSize = 0;

    private float mRelativeCloseHandSize = 0.2f;
    private int mAbsoluteCloseHandSize = 0;

    private SeekBar mMethodSeekbar;
    private TextView mValue;

    double xCenter = -1;
    double yCenter = -1;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
//                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade files from application resources
                        InputStream openedHand = getResources().openRawResource(R.raw.palm);
                        File openedHandcascadeDir = getDir("palm", Context.MODE_PRIVATE);
                        mOpenedHandCascadeFile = new File(openedHandcascadeDir, "palm.xml");
                        FileOutputStream os1 = new FileOutputStream(mOpenedHandCascadeFile);


                        InputStream closedHand = getResources().openRawResource(R.raw.fist);
                        File closedHandcascadeDir = getDir("fist", Context.MODE_PRIVATE);
                        mClosedHandCascadeFile = new File(closedHandcascadeDir, "fist.xml");
                        FileOutputStream os2 = new FileOutputStream(mClosedHandCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead1;
                        int bytesRead2;

                        while ((bytesRead1 = openedHand.read(buffer)) != -1) {
                            os1.write(buffer, 0, bytesRead1);
                        }

                        while ((bytesRead2 = closedHand.read(buffer)) != -1) {
                            os2.write(buffer, 0, bytesRead2);
                        }

                        openedHand.close();
                        os1.close();
                        closedHand.close();
                        os2.close();

                        mJavaDetectorOpenedHand = new CascadeClassifier(mOpenedHandCascadeFile.getAbsolutePath());
                        mJavaDetectorClosedHand = new CascadeClassifier(mClosedHandCascadeFile.getAbsolutePath());

                        if (mJavaDetectorOpenedHand.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetectorOpenedHand = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mOpenedHandCascadeFile.getAbsolutePath());

                        if (mJavaDetectorClosedHand.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetectorClosedHand = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mClosedHandCascadeFile.getAbsolutePath());

                        mNativeDetectorOpenedHand = new DetectionBasedTracker(mOpenedHandCascadeFile.getAbsolutePath(), 0);

                        mNativeDetectorClosedHand = new DetectionBasedTracker(mClosedHandCascadeFile.getAbsolutePath(), 0);

                        mOpenedHandCascadeFile.delete();
                        mClosedHandCascadeFile.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableFpsMeter();
                    mOpenCvCameraView.setCameraIndex(0);
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public MainActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());



    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mValue = (TextView) findViewById(R.id.method);





    }

    @Override
    public void onPause() {
        super.onPause();
        disableCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }

    public void onDestroy() {
        super.onDestroy();
        disableCamera();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        mZoomWindow.release();
        mZoomWindow2.release();
    }



    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        if (mAbsoluteOpenHandSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeOpenHandSize) > 0) {
                mAbsoluteOpenHandSize = Math.round(height * mRelativeOpenHandSize);
            }

            mNativeDetectorOpenedHand.setMinFaceSize(mAbsoluteOpenHandSize);
        }

        if (mAbsoluteCloseHandSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeCloseHandSize) > 0) {
                mAbsoluteCloseHandSize = Math.round(height * mRelativeCloseHandSize);
            }

            mNativeDetectorClosedHand.setMinFaceSize(mAbsoluteCloseHandSize);
        }


        /////////////////////////////////////
        if (mZoomWindow == null || mZoomWindow2 == null)
            CreateAuxiliaryMats();
        //////////////////////////////////////


        mOpenHand = new MatOfRect();
        mCloseHand = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mNativeDetectorOpenedHand != null)
                mJavaDetectorOpenedHand.detectMultiScale(mGray, mOpenHand, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteOpenHandSize, mAbsoluteOpenHandSize), new Size());

            if (mJavaDetectorClosedHand != null)
                mJavaDetectorClosedHand.detectMultiScale(mGray, mCloseHand, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteCloseHandSize, mAbsoluteCloseHandSize), new Size());

        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetectorOpenedHand != null)
                mNativeDetectorOpenedHand.detect(mGray, mOpenHand);

            if (mNativeDetectorClosedHand != null)
                mNativeDetectorClosedHand.detect(mGray, mCloseHand);
        } else {
            Log.e(TAG, "Detection openHand method is not selected!");
        }


        Rect[] openHandArray = mOpenHand.toArray();

        Rect[] closeHandArray = mCloseHand.toArray();

        for (int i = 0; i < openHandArray.length; i++)
            Imgproc.rectangle(mRgba, openHandArray[i].tl(), openHandArray[i].br(), OPENED_HAND_RECT_COLOR, 3);

        for (int i = 0; i < closeHandArray.length; i++)
            Imgproc.rectangle(mRgba, closeHandArray[i].tl(), closeHandArray[i].br(), CLOSED_HAND_RECT_COLOR, 3);


        startDetect();
        return mRgba;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemClosedHand50 = menu.add("ClosedHand size 50%");
        mItemClosedHand40 = menu.add("ClosedHand size 40%");
        mItemClosedHand30 = menu.add("ClosedHand size 30%");
        mItemClosedHand20 = menu.add("ClosedHand size 20%");
        mItemType = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemClosedHand50)
            setMinFaceSize(0.5f);
        else if (item == mItemClosedHand40)
            setMinFaceSize(0.4f);
        else if (item == mItemClosedHand30)
            setMinFaceSize(0.3f);
        else if (item == mItemClosedHand20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
    }

    private void setMinFaceSize(float handSize) {
        mRelativeOpenHandSize = handSize;
        mAbsoluteOpenHandSize = 0;

        mRelativeCloseHandSize = handSize;
        mRelativeCloseHandSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetectorOpenedHand.start();
                mNativeDetectorClosedHand.start();

            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetectorOpenedHand.stop();
                mNativeDetectorClosedHand.stop();
            }


        }
    }

    private void CreateAuxiliaryMats() {
        if (mGray.empty())
            return;

        int rows = mGray.rows();
        int cols = mGray.cols();

        if (mZoomWindow == null) {
            mZoomWindow = mRgba.submat(rows / 2 + rows / 10, rows, cols / 2
                    + cols / 10, cols);
            mZoomWindow2 = mRgba.submat(0, rows / 2 - rows / 10, cols / 2
                    + cols / 10, cols);
        }

    }
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                if (mRgba != null) {



                    int mNewQtdCloseHands = mCloseHand.toList().size();
                    int mNewQtdOpenHand = mOpenHand.toList().size();


                    if (qtdClosedHand != mNewQtdCloseHands) {
                        qtdClosedHand = mNewQtdCloseHands;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mValue.setText(String.format("close"));
                            }
                        });
                    }else if(qtdOpenedHand!=mNewQtdOpenHand){
                        qtdOpenedHand=mNewQtdOpenHand;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mValue.setText(String.format("open"));
                            }
                        });
                    }
                    Thread.sleep(500);//if you want an interval
                  //  mRgba = null;
                }
                Thread.sleep(50);
            } catch (Throwable t) {
                try {
                    Thread.sleep(10_000);
                } catch (Throwable tt) {
                }
            }
        }
    }

    public void onRecreateClick(View view) {
        learn_frames = 0;
    }

    public void startDetect() {
        if (running) return;
        new Thread(this).start();
    }

    public void disableCamera() {
        System.out.println("disable");
        running = false;
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
}
