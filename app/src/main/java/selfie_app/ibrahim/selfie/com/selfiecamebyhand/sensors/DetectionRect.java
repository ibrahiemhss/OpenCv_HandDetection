package selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors;

import android.graphics.Rect;

public class DetectionRect {
    public static final int I_LEFT_FIST = 4;
    public static final int I_LEFT_PALM = 3;
    public static final int I_NONE = 0;
    public static final int I_RIGHT_FIST = 2;
    public static final int I_RIGHT_PALM = 1;
    int iType = 0;
    Rect oRect = new Rect();

    public Rect getRect() {
        return this.oRect;
    }

    public int getType() {
        return this.iType;
    }

    public void setType(int iVar) {
        this.iType = iVar;
    }
}
