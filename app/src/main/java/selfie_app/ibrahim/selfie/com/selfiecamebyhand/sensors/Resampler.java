package selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors;


import selfie_app.ibrahim.selfie.com.hand_selfie_app.lib.algs.LengthLimitedAverage;
import selfie_app.ibrahim.selfie.com.hand_selfie_app.threads.SmartThread;

public class Resampler {
    public static int I_RESAMPLING_RATE = 16;
    private static LengthLimitedAverage oCPULoadLA = new LengthLimitedAverage(I_RESAMPLING_RATE);
    private static SmartThread oResamplingFilterThread = null;

    public static long getCPULoad() {
        oCPULoadLA.set((double) oResamplingFilterThread.getCPU());
        return Math.round(oCPULoadLA.get());
    }

    public static void start() {
        if (oResamplingFilterThread == null) {
            AccelerometerProcessor.reset();
            SmartThread.delay(100);
            oResamplingFilterThread = new SmartThread(I_RESAMPLING_RATE, new Runnable() {
                public void run() {
                    AccelerometerProcessor.process();
                }
            }, 1, "Resampler");
            oResamplingFilterThread.start();
            oResamplingFilterThread.setRunning(true);
        }
    }

    public static void stop() {
        if (oResamplingFilterThread != null) {
            oResamplingFilterThread.destroy();
            oResamplingFilterThread = null;
        }
    }

    public static void main(String[] args) {
    }
}
