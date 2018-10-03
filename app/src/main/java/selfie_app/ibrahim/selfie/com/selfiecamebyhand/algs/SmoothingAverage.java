package selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs;

public class SmoothingAverage {
    public static final int I_MAX_LENGTH = 100000000;
    protected int iLength = 0;
    protected int iLeveling = 0;
    protected int iLimit = 1;
    protected double rAvgMax = Double.MIN_VALUE;
    protected double rAvgMin = Double.MAX_VALUE;
    protected double rLevelingDirPrev = 0.0d;
    protected double rMax = Double.MIN_VALUE;
    protected double rMin = Double.MAX_VALUE;
    protected double rValue = 0.0d;

    public SmoothingAverage(int iiLimit) {
        reset(iiLimit);
    }

    protected void reset(int iiLimit) {
        this.rValue = 0.0d;
        this.iLength = 0;
        if (iiLimit < 1) {
            iiLimit = 1;
        }
        if (iiLimit > 100000000) {
            iiLimit = 100000000;
        }
        this.iLimit = iiLimit;
        this.rMax = Double.MIN_VALUE;
        this.rMin = Double.MAX_VALUE;
        this.rAvgMax = Double.MIN_VALUE;
        this.rAvgMin = Double.MAX_VALUE;
        this.rLevelingDirPrev = 0.0d;
        this.iLeveling = 0;
    }

    protected void setMinMax(double rNewValue) {
        if (this.iLength == this.iLimit) {
            if (this.rValue > this.rAvgMax) {
                this.rAvgMax = this.rValue;
            }
            if (this.rValue < this.rAvgMin) {
                this.rAvgMin = this.rValue;
            }
        }
        if (rNewValue > this.rMax) {
            this.rMax = rNewValue;
        }
        if (rNewValue < this.rMin) {
            this.rMin = rNewValue;
        }
    }

    protected void incLength() {
        this.iLength++;
        if (this.iLength > this.iLimit) {
            this.iLength = this.iLimit;
        }
        if (this.iLength < 1) {
            this.iLength = 1;
        }
    }

    protected void decLength() {
        this.iLength--;
        if (this.iLength < 0) {
            this.iLength = 0;
        }
    }

    protected void setPrivateAccelerated(double rNewValue, int iAccelerator) {
        incLength();
        iAccelerator = Math.abs(iAccelerator);
        if (iAccelerator > this.iLength) {
            iAccelerator = this.iLength;
        }
        if (iAccelerator < 1) {
            iAccelerator = 1;
        }
        if (this.iLength == iAccelerator) {
            this.rValue = rNewValue;
            return;
        }
        double rAlpha = (1.0d / ((double) this.iLength)) * ((double) iAccelerator);
        this.rValue = (this.rValue * (1.0d - rAlpha)) + (rNewValue * rAlpha);
        setMinMax(rNewValue);
    }

    protected void setPrivate(double rNewValue) {
        incLength();
        if (this.iLength == 1) {
            this.rValue = rNewValue;
            return;
        }
        double rAlpha = 1.0d / ((double) this.iLength);
        this.rValue = (this.rValue * (1.0d - rAlpha)) + (rNewValue * rAlpha);
        setMinMax(rNewValue);
    }

    protected void setWeightedPrivate(double rNewValue, double rWeight) {
        if (rWeight < 0.0d) {
            rWeight = 0.0d;
        }
        if (rWeight > 1.0d) {
            rWeight = 1.0d;
        }
        incLength();
        if (this.iLength == 1) {
            this.rValue = rNewValue;
            return;
        }
        double rAlpha = (1.0d / ((double) this.iLength)) * rWeight;
        this.rValue = (this.rValue * (1.0d - rAlpha)) + (rNewValue * rAlpha);
        setMinMax(rNewValue);
    }

    private boolean isReadyPrivate() {
        return this.iLength == this.iLimit;
    }

    public synchronized void reset() {
        reset(this.iLimit);
    }

    public synchronized void reset(double irValue, int iiLength, int iiLimit) {
        reset(iiLimit);
        this.rValue = irValue;
        this.iLength = iiLength;
    }

    public void setLimit(int iiLimit) {
        if (iiLimit < 1) {
            iiLimit = 1;
        }
        if (iiLimit > 100000000) {
            iiLimit = 100000000;
        }
        this.iLimit = iiLimit;
    }

    public int getLimit() {
        return this.iLimit;
    }

    public double getAny() {
        return this.rValue;
    }

    public synchronized double get() {
        double d;
        if (isReadyPrivate()) {
            d = this.rValue;
        } else {
            d = 0.0d;
        }
        return d;
    }

    public synchronized double getMax() {
        return this.rMax;
    }

    public synchronized double getMaxAbs() {
        return Math.abs(this.rMax);
    }

    public synchronized double getMin() {
        return this.rMin;
    }

    public synchronized double getAvgMax() {
        double d;
        if (isReadyPrivate()) {
            d = this.rAvgMax;
        } else {
            d = 0.0d;
        }
        return d;
    }

    public synchronized double getAvgMaxAbs() {
        double abs;
        if (isReadyPrivate()) {
            abs = Math.abs(this.rAvgMax);
        } else {
            abs = 0.0d;
        }
        return abs;
    }

    public synchronized double getAvgMin() {
        double d;
        if (isReadyPrivate()) {
            d = this.rAvgMin;
        } else {
            d = 0.0d;
        }
        return d;
    }

    public synchronized int getLength() {
        return this.iLength;
    }

    public synchronized void setLength(int iiLength) {
        this.iLength = iiLength - 1;
        incLength();
        if (this.iLength < 0) {
            this.iLength = 0;
        }
        if (this.iLength > 100000000) {
            this.iLength = 100000000;
        }
    }

    public synchronized void setWithFastLeveling(double rNewValue) {
        double rLevelingDir = rNewValue - this.rValue;
        if (rLevelingDir <= 0.0d || this.rLevelingDirPrev <= 0.0d) {
            if (rLevelingDir < 0.0d) {
                if (this.rLevelingDirPrev < 0.0d) {
                    this.iLeveling--;
                    if (this.iLeveling < (-this.iLength)) {
                        this.iLeveling = -this.iLength;
                    }
                }
            }
            if (this.iLeveling > 0) {
                this.iLeveling--;
            } else if (this.iLeveling < 0) {
                this.iLeveling++;
            }
            if (this.iLeveling > 0) {
                this.iLeveling--;
            } else if (this.iLeveling < 0) {
                this.iLeveling++;
            }
        } else {
            this.iLeveling++;
            if (this.iLeveling > this.iLength) {
                this.iLeveling = this.iLength;
            }
        }
        setPrivateAccelerated(rNewValue, this.iLeveling);
        this.rLevelingDirPrev = rLevelingDir;
    }

    public synchronized double getLeveling() {
        return Math.abs(((double) this.iLeveling) / ((double) this.iLimit));
    }

    public synchronized void set(double rNewValue) {
        setPrivate(rNewValue);
    }

    public synchronized void correct(double rNewValue) {
        this.rValue = rNewValue;
        setMinMax(this.rValue);
    }

    public synchronized void setAndReady(double rNewValue) {
        setPrivate(rNewValue);
        while (this.iLength < this.iLimit) {
            setPrivate(rNewValue);
        }
    }

    public void setAndRepeat(double rNewValue, int iRepeater) {
        set(rNewValue);
        for (int i = 0; i < iRepeater; i++) {
            set(rNewValue);
        }
    }

    public void setMultiple(double rNewValue, int iTimes) {
        for (int i = 0; i < iTimes; i++) {
            set(rNewValue);
        }
    }

    public synchronized void setWeighted(double rNewValue, double rWeight) {
        setWeightedPrivate(rNewValue, rWeight);
    }

    public synchronized boolean isReady() {
        return isReadyPrivate();
    }

    public synchronized String toString() {
        return String.format("%.3f", new Object[]{Double.valueOf(get())});
    }

    public synchronized String toStringAny() {
        return String.format("%.3f[%.0f]", new Object[]{Double.valueOf(getAny()), Integer.valueOf(this.iLength)});
    }

    public static void main(String[] args) {
        SmoothingAverage oLA = new SmoothingAverage(1000);
        SmoothingAverage oLAFast = new SmoothingAverage(1000);
        double rMaxNoiseValue = 100.0d / 5.0d;
        double rTrueValue = 100.0d / 5.0d;
        for (int i = 0; i < 10000; i++) {
            if (i == 1666) {
                rTrueValue = 100.0d;
            }
            if (i == 1766) {
                rTrueValue = 100.0d / 5.0d;
            }
            if (i == 3333) {
                rTrueValue = 100.0d;
            }
            if (i == 6666) {
                rTrueValue = 100.0d / 5.0d;
            }
            double rNoisyValue = rTrueValue + ((Math.random() * rMaxNoiseValue) - (rMaxNoiseValue / 2.0d));
            oLA.set(rNoisyValue);
            oLAFast.setWithFastLeveling(rNoisyValue);
            System.out.println(new StringBuilder(String.valueOf(rNoisyValue)).append(", ").append(oLA.getAny()).append(",").append(oLAFast.getAny()).append(",").append(oLAFast.getLeveling() * 10.0d).toString());
        }
    }
}
