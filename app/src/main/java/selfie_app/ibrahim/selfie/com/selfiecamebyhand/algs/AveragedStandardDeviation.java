package selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs;

public class AveragedStandardDeviation {
    SmoothingAverage oLA = new SmoothingAverage(1);
    SmoothingAverage oSqrLA = new SmoothingAverage(1);
    SmoothingAverage oValueLA = new SmoothingAverage(1);

    public AveragedStandardDeviation(int iiLength) {
        reset(iiLength);
    }

    private void reset(int iLimit) {
        this.oValueLA.reset(0.0d, 0, iLimit);
        this.oLA.reset(0.0d, 0, iLimit);
        this.oSqrLA.reset(0.0d, 0, iLimit);
    }

    public synchronized boolean isReady() {
        return this.oValueLA.isReady();
    }

    public synchronized void reset() {
        reset(this.oValueLA.getLimit());
    }

    public synchronized void set(double rNewValue) {
        if (!Double.isNaN(rNewValue)) {
            this.oLA.set(rNewValue);
            this.oSqrLA.set(rNewValue * rNewValue);
            double rSqrLA = this.oSqrLA.get();
            double rLASqr = this.oLA.get() * this.oLA.get();
            if (rSqrLA >= rLASqr && this.oSqrLA.isReady() && this.oLA.isReady()) {
                this.oValueLA.set(Math.sqrt(rSqrLA - rLASqr));
            }
        }
    }

    public synchronized void setAny(double rNewValue) {
        if (!Double.isNaN(rNewValue)) {
            this.oLA.set(rNewValue);
            this.oSqrLA.set(rNewValue * rNewValue);
            double rSqrLA = this.oSqrLA.getAny();
            double rLASqr = this.oLA.getAny() * this.oLA.getAny();
            if (rSqrLA >= rLASqr) {
                this.oValueLA.set(Math.sqrt(rSqrLA - rLASqr));
            }
        }
    }

    public double getAny() {
        return this.oValueLA.getAny();
    }

    public synchronized double get() {
        return this.oValueLA.get();
    }

    public synchronized String toStringAny() {
        return String.format("%.3f[%.0f]", new Object[]{Double.valueOf(getAny()), Double.valueOf((double) this.oLA.getLength())});
    }

    public static void main(String[] args) {
        AveragedStandardDeviation oMSD = new AveragedStandardDeviation(3600);
        AveragedStandardDeviation oPosMSD = new AveragedStandardDeviation(3600);
        for (int i = 0; i < 3600; i++) {
            double rRand = Math.random();
            oPosMSD.set(rRand * 100.0d);
            if (Math.random() >= 0.5d) {
                rRand = -rRand;
            }
            oMSD.set(rRand * 100.0d);
        }
        oMSD.set(1000.0d);
        System.out.println(oPosMSD.getAny());
        System.out.println(oMSD.getAny());
    }
}
