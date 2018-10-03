package selfie_app.ibrahim.selfie.com.selfiecamebyhand;

import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import eyesight.android.Notifier.AccelerometerListener;
import eyesight.android.sdk.EyeSightAPI;
import selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors.EyeCanCallBackImpl;

/*
import eyesight.android.sdk.EyeSightAPI;
import selfie_app.ibrahim.selfie.com.hand_selfie_app.sensors.AccelerometerListener;
import selfie_app.ibrahim.selfie.com.hand_selfie_app.sensors.EyeCanCallBackImpl;
*/

public class SharedData {
    public static final long I_INACTIVITY_TIMEOUT = 120000;
    public static boolean bAntibanding60Hz = false;
    public static boolean bAntibanding60HzDefault = false;
    public static boolean bCameraMirroring = false;
    public static boolean bCameraPreviewIsOn = false;
    public static boolean bOneTouchShutter = false;
    public static boolean bSoundCountdown = true;
    public static boolean bSoundFocus = true;
    public static boolean bSoundShutter = true;
    public static boolean bTutorial = true;
    public static long iAvailableStorageSize = -1;
    public static int iCameraID = 0;
    public static int iCountdownTimerSeconds = 2;
    public static int iDefaultCameraID = 0;
    public static int iDisplayHeight = 0;
    public static int iDisplayWidth = 0;
    public static int iEyesightOrientationAngle = 0;
    public static int iEyesightPreviousOrientation = -1;
    public static int iGeneralError = 0;
    public static int iMainScreenState = 0;
    public static int iPhotoSavingIsInProgress = 0;
    public static int iSavePictureSize = 2;
    public static int iStorage = 2;
    public static int iTutorialNumber = 0;
    public static AccelerometerListener oAccelerometerListener = new AccelerometerListener() {
        @Override
        public void onAccelerationChanged(float f, float f2, float f3) {

        }

        @Override
        public void onShake(float f) {

        }
    };
    public static final Options oBitmapDecodingOptions = new Options();
    public static final Paint oBitmapPaint = new Paint();
    public static Parameters oCameraParameters = null;
    public static Rect oCameraPreviewRect = new Rect();
    public static Rect oCameraRect = new Rect();
    public static Face[] oDetectedFaces = null;
    public static ESAlertDialog oESAlertDialog = new ESAlertDialog();
    public static final EyeCanCallBackImpl oEyeCanCallBackImpl = new EyeCanCallBackImpl();
    public static EyeSightAPI oEyeInstance = null;
    public static Object oEyeInstanceMutex = new Object();
  //  public static Typeface oFontMain;
  //  public static Typeface oFontMainBold;
    public static final Paint oFontMainBoldPaint = new Paint();
    public static final Paint oFontMainPaint = new Paint();
    public static MainActivity oMainActivity = null;
    public static Camera oMainCamera = null;
    public static SharedPreferences oMainPreferences = null;
    public static Resources oMainResources = null;
    public static SensorManager oSensorManager;
    public static String sEyeSightVersion = null;
    public static String sFlashMode = "";
    public static String sLastPhotoPath = "";
    public static String sLastPhotoUri = "";
    public static String sVersion = "0.0.0.0";

    public static void init(MainActivity oActivity) {
        oMainActivity = oActivity;
        oMainResources = oMainActivity.getResources();
        loadSettings();
       // oFontMain = Typeface.createFromAsset(oActivity.getAssets(), "ttf/SourceSansPro-Light.otf");
       // oFontMainPaint.setTypeface(oFontMain);
      //  oFontMainBold = Typeface.createFromAsset(oActivity.getAssets(), "ttf/SourceSansPro-Bold.otf");
      //  oFontMainBoldPaint.setTypeface(oFontMainBold);
        bTutorial = true;
    }

    public static void saveLastPhotoPath(String isPath, String isUri) {
        sLastPhotoPath = isPath;
        sLastPhotoUri = isUri;
        saveSettings();
    }

    public static synchronized void loadSettings() {
        synchronized (SharedData.class) {
            oMainPreferences = oMainActivity.getPreferences(0);
            sLastPhotoPath = oMainPreferences.getString("sLastPhotoPath", "");
            sLastPhotoUri = oMainPreferences.getString("sLastPhotoUri", "");
            iCountdownTimerSeconds = oMainPreferences.getInt("iCountdownTimerSeconds", 2);
            iSavePictureSize = oMainPreferences.getInt("iSavePictureSize", 2);
            iStorage = oMainPreferences.getInt("iStorage", 2);
            bSoundFocus = oMainPreferences.getBoolean("bSoundFocus", true);
            bSoundCountdown = oMainPreferences.getBoolean("bSoundCountdown", true);
            bSoundShutter = oMainPreferences.getBoolean("bSoundShutter", true);
            bOneTouchShutter = oMainPreferences.getBoolean("bOneTouchShutter", false);
            bAntibanding60Hz = oMainPreferences.getBoolean("bAntibanding60Hz", bAntibanding60HzDefault);
            iTutorialNumber = oMainPreferences.getInt("iTutorialNumber", 0);
            iMainScreenState = oMainPreferences.getInt("iMainScreenState", 0);
            iCameraID = oMainPreferences.getInt("iCameraID", iDefaultCameraID);
            sFlashMode = oMainPreferences.getString("sFlashMode", "");
        }
    }

    public static synchronized void saveSettings() {
        synchronized (SharedData.class) {
            Editor oEditor = oMainPreferences.edit();
            oEditor.putString("sLastPhotoPath", sLastPhotoPath);
            oEditor.putString("sLastPhotoUri", sLastPhotoUri);
            oEditor.putInt("iCountdownTimerSeconds", iCountdownTimerSeconds);
            oEditor.putInt("iSavePictureSize", iSavePictureSize);
            oEditor.putInt("iStorage", iStorage);
            oEditor.putBoolean("bSoundFocus", bSoundFocus);
            oEditor.putBoolean("bSoundCountdown", bSoundCountdown);
            oEditor.putBoolean("bSoundShutter", bSoundShutter);
            oEditor.putBoolean("bOneTouchShutter", bOneTouchShutter);
            oEditor.putBoolean("bAntibanding60Hz", bAntibanding60Hz);
            oEditor.putInt("iTutorialNumber", iTutorialNumber);
            oEditor.putInt("iMainScreenState", iMainScreenState);
            oEditor.putInt("iCameraID", iCameraID);
            oEditor.putString("sFlashMode", sFlashMode);
            oEditor.commit();
        }
    }

    public static void systemExit() {
       // oMainActivity.exitActivity();
    }

    public static final synchronized Bitmap loadAssetImage(String sPath) {
        Bitmap oBitmap;
        synchronized (SharedData.class) {
            oBitmap = null;
            try {
                InputStream iS = oMainResources.getAssets().open(sPath);
                oBitmapDecodingOptions.inDither = true;
                oBitmapDecodingOptions.inScaled = false;
                oBitmapDecodingOptions.inPreferredConfig = Config.ARGB_8888;
                oBitmapDecodingOptions.inDither = false;
                oBitmap = BitmapFactory.decodeStream(iS, null, oBitmapDecodingOptions);
                iS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return oBitmap;
    }

    public static final AssetFileDescriptor getAssetFd(String sPath) {
        try {
            return oMainResources.getAssets().openFd(sPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final File getMediaDir() {
        File oImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!oImageDir.exists()) {
            oImageDir.mkdirs();
            if (!oImageDir.mkdirs()) {
                return oMainActivity.getDir("Snapi", 2);
            }
        }
        File oMediaStorageDir = new File(oImageDir, "Snapi");
        return (oMediaStorageDir.exists() || oMediaStorageDir.mkdirs()) ? oMediaStorageDir : null;
    }

    public static void triggerAlertDialog(String positiveText, OnClickListener positivelistener, String negativeText, OnClickListener negativelistener, String alertTitle, String alertText) {
        triggerAlertDialog(positiveText, positivelistener, negativeText, negativelistener, alertTitle, alertText, null);
    }

    public static void triggerAlertDialog(String positiveText, OnClickListener positivelistener, String negativeText, OnClickListener negativelistener, String alertTitle, String alertText, OnKeyListener keyListener) {
        oESAlertDialog.setActivity(oMainActivity);
        oESAlertDialog.setPositiveButton(positiveText, positivelistener);
        oESAlertDialog.setNegativeButton(negativeText, negativelistener);
        oESAlertDialog.setAlertTitle(alertTitle);
        oESAlertDialog.setAlertText(alertText);
        oESAlertDialog.setKeyListener(keyListener);
        oESAlertDialog.triggerAlertDialog();
    }
}
