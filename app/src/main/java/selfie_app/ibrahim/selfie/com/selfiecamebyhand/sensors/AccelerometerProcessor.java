package selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors;



import selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs.ForceVector;
import selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs.ForceVectorLA;

public class AccelerometerProcessor {
    static int I_DYNGRAVITY_LA_LIMIT_STATIC = 4;
    static final double R_STANDARD_GRAVITY = 9.81d;
    public static int iPhoneOrientation = -1;
    private static int iSamplingRate = 0;
    private static int iSamplingRateCurrent = 0;
    private static long iSamplingUpdateTS = System.currentTimeMillis();
    private static Object oAccUpdateMutex = new Object();
    private static ForceVector oForceVector = new ForceVector();
    private static ForceVectorLA oGravityVector = new ForceVectorLA(I_DYNGRAVITY_LA_LIMIT_STATIC);
    private static ForceVector oMeasurementVector = new ForceVector();
    private static ForceVector oResampledValue = null;

    private static void configure() {
        oGravityVector = new ForceVectorLA(I_DYNGRAVITY_LA_LIMIT_STATIC);
    }

    private static void init() {
        iPhoneOrientation = -1;
        configure();
        oGravityVector.reset();
        oMeasurementVector.reset();
        oForceVector.reset();
    }

    public static synchronized void reset() {
        synchronized (AccelerometerProcessor.class) {
            init();
        }
    }

    public static synchronized int getSamplingRate() {
        int i;
        synchronized (AccelerometerProcessor.class) {
            i = iSamplingRate;
        }
        return i;
    }

    public static void setValues(double[] oValues) {
        if (oValues.length >= 3) {
            long iTS = System.currentTimeMillis();
            iSamplingRateCurrent++;
            if (iTS - iSamplingUpdateTS > 1000) {
                iSamplingRate = iSamplingRateCurrent;
                iSamplingRateCurrent = 0;
                iSamplingUpdateTS = iTS;
            }
            synchronized (oAccUpdateMutex) {
                if (oResampledValue == null) {
                    oResampledValue = new ForceVector();
                }
                if (oValues.length >= 3) {
                    oResampledValue.set(oValues);
                }
            }
        }
    }

    private static void fillGravityVectors(ForceVector oVector) {
        oGravityVector.set(oVector);
        if (Math.abs(oGravityVector.rVector[2]) >= 8.829d) {
            return;
        }
        if (Math.abs(oGravityVector.rVector[0]) > Math.abs(oGravityVector.rVector[1])) {
            if (oGravityVector.rVector[0] < 0.0d) {
                iPhoneOrientation = 2;
            } else {
                iPhoneOrientation = 0;
            }
        } else if (oGravityVector.rVector[1] < 0.0d) {
            iPhoneOrientation = 1;
        } else {
            iPhoneOrientation = 3;
        }
    }

    public static double[] getValues(double[] oValues) {
        if (oValues == null || oValues.length < 3) {
            oValues = new double[3];
        }
        synchronized (oAccUpdateMutex) {
            if (oValues.length >= 3 && oResampledValue != null) {
                for (int i = 0; i < 3; i++) {
                    oValues[i] = oResampledValue.rVector[i];
                }
            }
        }
        return oValues;
    }

    public static synchronized void process() {
        synchronized (AccelerometerProcessor.class) {
            synchronized (oAccUpdateMutex) {
                if (oResampledValue == null) {
                } else {
                    double[] oValues = new double[oResampledValue.rVector.length];
                    for (int i = 0; i < oValues.length; i++) {
                        oValues[i] = oResampledValue.rVector[i];
                    }
                    if (oValues.length >= 3) {
                        oMeasurementVector.set(oValues);
                        if (oGravityVector.getAverageLength() == 0) {
                            oForceVector.reset();
                            oGravityVector.set(oValues);
                        } else if (oGravityVector.isReady()) {
                            oForceVector = oMeasurementVector.subtract(oGravityVector, oForceVector);
                        }
                        fillGravityVectors(oMeasurementVector);
                        if (!oGravityVector.isReady()) {
                        }
                    }
                }
            }
        }
    }

    public static synchronized ForceVector getGravityVector() {
        ForceVector forceVector;
        synchronized (AccelerometerProcessor.class) {
            if (oGravityVector.isReady()) {
                forceVector = oGravityVector;
            } else {
                forceVector = null;
            }
        }
        return forceVector;
    }
}
