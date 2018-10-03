package selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors;

import android.graphics.Rect;

import eyesight.android.sdk.EyeSightAPI.EyeCanCallback;
import eyesight.service.common.ActivityConfig;
import eyesight.service.common.ConstAndEnums.EyeCanMessage;
import eyesight.service.common.ConstAndEnums.ObjectType;
import eyesight.service.common.EyeCanMultiUserOutput;
import eyesight.service.common.EyeCanMultiUserOutput.EyeCanOutput.TwoHandsDetectionData.HandDetectionData;
import selfie_app.ibrahim.selfie.com.hand_selfie_app.SharedData;
import selfie_app.ibrahim.selfie.com.hand_selfie_app.touch.TouchProcessor;

public class EyeCanCallBackImpl implements EyeCanCallback {
    private static /* synthetic */ int[] $SWITCH_TABLE$eyesight$service$common$ConstAndEnums$ObjectType = null;
    public static final int I_EYESIGHT_CLICK_EVENT = 4097;
    public static final int I_EYESIGHT_DETECTED_EVENT = 4099;
    public static final int I_EYESIGHT_DETECTED_FIRST_EVENT = 4098;
    public static final int I_EYESIGHT_SMILE_EVENT = 4100;
    private long iLastDetectionTS = 0;
    private DetectionRect[] oDetectionRects = new DetectionRect[]{new DetectionRect(), new DetectionRect()};

    static /* synthetic */ int[] $SWITCH_TABLE$eyesight$service$common$ConstAndEnums$ObjectType() {
        int[] iArr = $SWITCH_TABLE$eyesight$service$common$ConstAndEnums$ObjectType;
        if (iArr == null) {
            iArr = new int[ObjectType.values().length];
            try {
                iArr[ObjectType.CLICK.ordinal()] = 4;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ObjectType.FINGER.ordinal()] = 11;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ObjectType.FINGER_DOWN.ordinal()] = 14;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[ObjectType.FINGER_TO_PINCH.ordinal()] = 15;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[ObjectType.FINGER_UP.ordinal()] = 13;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[ObjectType.FIST.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[ObjectType.FIST_TO_HAND.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[ObjectType.HAND_DOWN.ordinal()] = 9;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[ObjectType.HAND_TO_FIST.ordinal()] = 6;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[ObjectType.HAND_UP.ordinal()] = 8;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[ObjectType.MUTE.ordinal()] = 17;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[ObjectType.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[ObjectType.PALM.ordinal()] = 2;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[ObjectType.PINCH.ordinal()] = 12;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[ObjectType.PINCH_TO_FINGER.ordinal()] = 16;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[ObjectType.UNDEFINED.ordinal()] = 10;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[ObjectType.WAVE.ordinal()] = 5;
            } catch (NoSuchFieldError e17) {
            }
            $SWITCH_TABLE$eyesight$service$common$ConstAndEnums$ObjectType = iArr;
        }
        return iArr;
    }

    private void setType(DetectionRect oDetectionRect, int iType) {
        if (oDetectionRect.getType() != iType) {
            if (iType == 3 && oDetectionRect.getType() == 0) {
                TouchProcessor.process(I_EYESIGHT_DETECTED_FIRST_EVENT, oDetectionRect.getRect().centerX(), oDetectionRect.getRect().centerY());
                this.iLastDetectionTS = System.currentTimeMillis();
            } else if (iType == 4 && oDetectionRect.getType() == 3) {
                TouchProcessor.process(I_EYESIGHT_DETECTED_EVENT, oDetectionRect.getRect().centerX(), oDetectionRect.getRect().centerY());
                this.iLastDetectionTS = System.currentTimeMillis();
            } else if (iType == 3 && oDetectionRect.getType() == 4) {
                TouchProcessor.process(I_EYESIGHT_CLICK_EVENT, oDetectionRect.getRect().centerX(), oDetectionRect.getRect().centerY());
                this.iLastDetectionTS = System.currentTimeMillis();
            } else if (iType == 1 && oDetectionRect.getType() == 0) {
                TouchProcessor.process(I_EYESIGHT_DETECTED_FIRST_EVENT, oDetectionRect.getRect().centerX(), oDetectionRect.getRect().centerY());
                this.iLastDetectionTS = System.currentTimeMillis();
            } else if (iType == 2 && oDetectionRect.getType() == 1) {
                TouchProcessor.process(I_EYESIGHT_DETECTED_EVENT, oDetectionRect.getRect().centerX(), oDetectionRect.getRect().centerY());
                this.iLastDetectionTS = System.currentTimeMillis();
            } else if (iType == 1 && oDetectionRect.getType() == 2) {
                TouchProcessor.process(I_EYESIGHT_CLICK_EVENT, oDetectionRect.getRect().centerX(), oDetectionRect.getRect().centerY());
                this.iLastDetectionTS = System.currentTimeMillis();
            }
            oDetectionRect.setType(iType);
            if (System.currentTimeMillis() - this.iLastDetectionTS > 5000) {
                SharedData.oEyeInstance.ResetEngine();
            }
        }
    }

    private void HandleObjectType(DetectionRect oDetectionRect, HandDetectionData handDetectionType) {
        boolean bLeft = handDetectionType.bIsLeftSide;
        Rect rObjectRect = new Rect(handDetectionType.nObjectX - (handDetectionType.nObjectScaleX / 2), handDetectionType.nObjectY - (handDetectionType.nObjectScaleY / 2), handDetectionType.nObjectX + (handDetectionType.nObjectScaleX / 2), handDetectionType.nObjectY + (handDetectionType.nObjectScaleY / 2));
        switch ($SWITCH_TABLE$eyesight$service$common$ConstAndEnums$ObjectType()[handDetectionType.nObjectType.ordinal()]) {
            case 1:
                oDetectionRect.getRect().setEmpty();
                setType(oDetectionRect, 0);
                return;
            case 2:
                oDetectionRect.getRect().set(rObjectRect);
                if (bLeft) {
                    setType(oDetectionRect, 3);
                    return;
                } else {
                    setType(oDetectionRect, 1);
                    return;
                }
            case 3:
                oDetectionRect.getRect().set(rObjectRect);
                if (bLeft) {
                    setType(oDetectionRect, 4);
                    return;
                } else {
                    setType(oDetectionRect, 2);
                    return;
                }
            default:
                oDetectionRect.getRect().setEmpty();
                setType(oDetectionRect, 0);
                return;
        }
    }

    public synchronized long timePassedSinceLastDetection() {
        return System.currentTimeMillis() - this.iLastDetectionTS;
    }

    public synchronized DetectionRect getRect(int i) {
        return this.oDetectionRects[i];
    }

    public void HandleEyeCanOutput(EyeCanMultiUserOutput sEyeCanOut) {
        for (int i = 0; i < 2; i++) {
            HandleObjectType(this.oDetectionRects[i], sEyeCanOut.aOutputArray[0].sTwoHandsData.sHandsData[i]);
        }
    }

    public void HandleEyeCanError(EyeCanMessage nError) {
    }

    public void HandleEyeCanStatus(EyeCanMessage nStatus) {
    }

    public void HandleCameraStatus(EyeCanMessage nStatus) {
    }

    public boolean IsUserActionStart() {
        return false;
    }

    public void RecordingFinished() {
    }

    public void HandleActivityChange(String name, ActivityConfig config) {
    }
}
