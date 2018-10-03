package selfie_app.ibrahim.selfie.com.selfiecamebyhand.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import selfie_app.ibrahim.selfie.com.hand_selfie_app.SharedData;

public class AccelerometerListener implements SensorEventListener {
    boolean bActive = false;
    double[] oAccValues = new double[3];

    public void activate() {
        if (!this.bActive && SharedData.oSensorManager != null) {
            SharedData.oSensorManager.registerListener(this, SharedData.oSensorManager.getDefaultSensor(1), 3);
            this.bActive = true;
        }
    }

    public void passivate() {
        if (this.bActive && SharedData.oSensorManager != null) {
            SharedData.oSensorManager.unregisterListener(this);
            this.bActive = false;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 1) {
            if (event.values.length >= 3) {
                int i = 0;
                while (i < 3) {
                    if (((double) event.values[i]) <= 1000.0d && ((double) event.values[i]) >= -1000.0d && ((double) event.values[i]) != 0.0d) {
                        this.oAccValues[i] = (double) event.values[i];
                        i++;
                    } else {
                        return;
                    }
                }
            }
            AccelerometerProcessor.setValues(this.oAccValues);
        }
    }
}
