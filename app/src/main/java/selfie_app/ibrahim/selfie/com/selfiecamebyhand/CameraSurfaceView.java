package selfie_app.ibrahim.selfie.com.selfiecamebyhand;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout.LayoutParams;

import java.util.List;

import eyesight.android.sdk.EyeSightAPI;
import selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors.AccelerometerProcessor;

class CameraSurfaceView extends SurfaceView implements Callback {
    static final int I_MEGAPIXEL = 1000000;
    final boolean B_USE_EYSIGHT = true;
    boolean bFaseDetectionStarted = false;
    boolean bPreparingCameraNow = false;
    FaceDetectionListener oCameraFaceDetectorCallback = new FaceDetectionListener() {
        public void onFaceDetection(Face[] faces, Camera camera) {
            SharedData.oDetectedFaces = faces;
        }
    };
    Size oCameraPreviewSize = null;
    Context oContext;

    CameraSurfaceView(Context context) {
        super(context);
        this.oContext = context;
        getHolder().addCallback(this);
    }

    private Size findCameraLargestNotSquarePictureSize(Parameters oParameters, long iMaxAreaSize) {
        List<Size> oSizes = oParameters.getSupportedPictureSizes();
        long iBiggestArea = Long.MIN_VALUE;
        Size oBiggestSize = null;
        for (int i = 0; i < oSizes.size(); i++) {
            Size oSize = (Size) oSizes.get(i);
            long iArea = (long) (oSize.height * oSize.width);
            if (iArea <= iMaxAreaSize && iArea > iBiggestArea && oSize.width > oSize.height) {
                iBiggestArea = iArea;
                oBiggestSize = oSize;
            }
        }
        return oBiggestSize;
    }

    private Size findCameraLargestEyesightPreviewSize(Parameters oParameters, long iMaxAreaSize) {
        List<Size> oSizes = oParameters.getSupportedPreviewSizes();
        long iBiggestArea = Long.MIN_VALUE;
        Size oBiggestSize = null;
        for (int i = 0; i < oSizes.size(); i++) {
            Size oSize = (Size) oSizes.get(i);
            long iArea = (long) (oSize.height * oSize.width);
            if (iArea <= iMaxAreaSize && iArea > iBiggestArea && oSize.width > oSize.height && oSize.height % 8 == 0 && oSize.width % 8 == 0) {
                iBiggestArea = iArea;
                oBiggestSize = oSize;
            }
        }
        return oBiggestSize;
    }

    private void setAntibandingMode(Parameters oParameters) {
        String sCurrentAntibanding = oParameters.getAntibanding();
        if (sCurrentAntibanding != null && !"".equals(sCurrentAntibanding)) {
            List<String> sAntibandingValues = oParameters.getSupportedAntibanding();
            String sResult = "50hz";
            if (SharedData.bAntibanding60Hz) {
                sResult = "60hz";
            }
            if (sAntibandingValues.contains(sResult)) {
                oParameters.setAntibanding(sResult);
                oParameters.set("antibanding", sResult);
            }
        }
    }

    private void prepareCamera() {
        if (SharedData.oMainCamera != null) {
            try {
                SharedData.oMainCamera.stopPreview();
                Camera.getCameraInfo(SharedData.iCameraID, new CameraInfo());
                Parameters oParameters = SharedData.oMainCamera.getParameters();
                setAntibandingMode(oParameters);
                Size oBestPreviewSize = findCameraLargestEyesightPreviewSize(oParameters, 307200);
                if (oBestPreviewSize != null) {
                    oParameters.setPreviewSize(oBestPreviewSize.width, oBestPreviewSize.height);
                }
                double rCameraWidth = (double) oParameters.getPreviewSize().width;
                double rCameraHeight = (double) oParameters.getPreviewSize().height;
                double rCameraRatio = rCameraWidth / rCameraHeight;
                double rViewWidth = (double) SharedData.iDisplayWidth;
                double rViewHeight = (double) SharedData.iDisplayHeight;
                double rNewViewWidth = rViewWidth;
                double rNewViewHeight = rNewViewWidth / rCameraRatio;
                if (rNewViewHeight < rViewHeight) {
                    rNewViewHeight = rViewHeight;
                    rNewViewWidth = rNewViewHeight * rCameraRatio;
                }
                oParameters.setPreviewFormat(17);
                oParameters.set("jpeg-quality", 90);
                Size oBestPictureSize = findCameraLargestNotSquarePictureSize(oParameters, 20000000);
                if (oBestPictureSize != null) {
                    oParameters.setPictureSize(oBestPictureSize.width, oBestPictureSize.height);
                }
                SharedData.oMainCamera.setParameters(oParameters);
                SharedData.oCameraRect.set(0, 0, ((int) rCameraWidth) - 1, ((int) rCameraHeight) - 1);
                double rNewX = (rViewWidth - rNewViewWidth) / 2.0d;
                double rNewY = (rViewHeight - rNewViewHeight) / 2.0d;
                SharedData.oCameraPreviewRect.set((int) rNewX, (int) rNewY, (int) ((rNewX + rNewViewWidth) - 1.0d), (int) ((rNewY + rNewViewHeight) - 1.0d));
                setLayoutParams(new LayoutParams((int) rNewViewWidth, (int) rNewViewHeight));
                setX((float) SharedData.oCameraPreviewRect.left);
                setY((float) SharedData.oCameraPreviewRect.top);
                synchronized (SharedData.oEyeInstanceMutex) {
                    if (SharedData.oEyeInstance == null) {
                        SharedData.oEyeInstance = EyeSightAPI.getInstance(this.oContext);
                        SharedData.oEyeInstance.InitEyeSightEngine(this.oContext, "", null);
                        SharedData.oEyeInstance.RegisterEyeCanCallback(SharedData.oEyeCanCallBackImpl);
                        SharedData.oEyeInstance.StartEyeSightEngine((int) rCameraWidth, (int) rCameraHeight, oBestPreviewSize.width, oBestPreviewSize.height);
                    } else {
                        SharedData.oEyeInstance.StopEyeSightEngine();
                        SharedData.oEyeInstance.StartEyeSightEngine((int) rCameraWidth, (int) rCameraHeight, oBestPreviewSize.width, oBestPreviewSize.height);
                    }
                    SharedData.iEyesightPreviousOrientation = -1;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void initCamera() {
        releaseCamera();
        try {
            int iCameras = Camera.getNumberOfCameras();
            if (iCameras == 0) {
                SharedData.iCameraID = -1;
                return;
            }
            if (SharedData.iCameraID >= iCameras) {
                SharedData.iCameraID = 0;
            }
            SharedData.bCameraMirroring = false;
            CameraInfo oCameraInfo = new CameraInfo();
            Camera.getCameraInfo(SharedData.iCameraID, oCameraInfo);
            if (oCameraInfo.facing == 1) {
                SharedData.bCameraMirroring = true;
            } else {
                SharedData.bCameraMirroring = false;
            }
            SharedData.oMainCamera = Camera.open(SharedData.iCameraID);
            SharedData.oMainCamera.setPreviewDisplay(getHolder());
            SharedData.saveSettings();
        } catch (Exception ioe) {
            SharedData.iGeneralError = -1;
            ioe.printStackTrace(System.out);
        }
    }

    private void startPreview() {
        if (SharedData.oMainCamera != null) {
            SharedData.oMainCamera.setPreviewCallback(new PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    synchronized (SharedData.oEyeInstanceMutex) {
                        CameraSurfaceView.this.setEyeSightEngineOrientation(AccelerometerProcessor.iPhoneOrientation);
                        if (SharedData.oEyeInstance != null) {
                            SharedData.bCameraPreviewIsOn = true;
                            SharedData.oEyeInstance.AnalyzeFrame(data);
                        }
                    }
                }
            });
            try {
                SharedData.oMainCamera.startPreview();
            } catch (Throwable th) {
                SharedData.iGeneralError = -1;
            }
            if (this.bFaseDetectionStarted) {
                SharedData.oMainCamera.stopFaceDetection();
                this.bFaseDetectionStarted = false;
            }
            if (SharedData.oMainCamera.getParameters().getMaxNumDetectedFaces() > 0) {
                try {
                    SharedData.oMainCamera.setFaceDetectionListener(this.oCameraFaceDetectorCallback);
                    SharedData.oMainCamera.startFaceDetection();
                    this.bFaseDetectionStarted = true;
                    return;
                } catch (Throwable th2) {
                }
            } else {
                return;
            }
        }
        return;
    }

    private void focusCamera() {
        if (SharedData.oMainCamera != null) {
            String sFocusMode = SharedData.oMainCamera.getParameters().getFocusMode();
            if (sFocusMode.equals("auto") || sFocusMode.equals("macro")) {
                SharedData.oMainCamera.autoFocus(null);
            }
        }
    }

    public void activateCamera() {
        this.bPreparingCameraNow = true;
        initCamera();
        if (SharedData.oMainCamera != null) {
            prepareCamera();
            startPreview();
            focusCamera();
            SharedData.oCameraParameters = SharedData.oMainCamera.getParameters();
            this.bPreparingCameraNow = false;
        }
    }

    private void setEyeSightEngineOrientation(int iOrientation) {
        if (SharedData.iEyesightPreviousOrientation != iOrientation) {
            int iEyesightOrientationAngle;
            if (iOrientation == 0) {
                iEyesightOrientationAngle = 0;
            } else if (iOrientation == 1) {
                if (SharedData.bCameraMirroring) {
                    iEyesightOrientationAngle = 90;
                } else {
                    iEyesightOrientationAngle = 270;
                }
            } else if (iOrientation == 2) {
                iEyesightOrientationAngle = 180;
            } else if (iOrientation != 3) {
                return;
            } else {
                if (SharedData.bCameraMirroring) {
                    iEyesightOrientationAngle = 270;
                } else {
                    iEyesightOrientationAngle = 90;
                }
            }
            if (SharedData.oEyeInstance != null) {
                SharedData.oEyeInstance.setGestureOrientation(iEyesightOrientationAngle);
            }
            SharedData.iEyesightOrientationAngle = iEyesightOrientationAngle;
            SharedData.iEyesightPreviousOrientation = iOrientation;
        }
    }

    public void releaseCamera() {
        if (SharedData.oMainCamera != null) {
            Camera oCamera = SharedData.oMainCamera;
            SharedData.oMainCamera = null;
            SharedData.oCameraParameters = null;
            oCamera.setPreviewCallback(null);
            if (this.bFaseDetectionStarted) {
                try {
                    oCamera.stopFaceDetection();
                } catch (Exception e) {
                    Log.e("camera", "stop face detection failed");
                }
                this.bFaseDetectionStarted = false;
            }
            oCamera.stopPreview();
            SharedData.bCameraPreviewIsOn = false;
            oCamera.release();
            if (SharedData.oEyeInstance != null) {
                SharedData.oEyeInstance.StopEyeSightEngine();
                SharedData.oEyeInstance = null;
            }
        }
        SharedData.iEyesightPreviousOrientation = -1;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public void delay(long iMillis) {
        try {
            Thread.sleep(iMillis);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
