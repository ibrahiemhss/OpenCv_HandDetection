package selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs;

public class ForceVectorLA extends ForceVector {
    public LengthLimitedAverage[] oVectorLA;

    public ForceVectorLA() {
        this.oVectorLA = new LengthLimitedAverage[3];
        for (int i = 0; i < 3; i++) {
            this.oVectorLA[i] = new LengthLimitedAverage(1);
        }
    }

    public ForceVectorLA(int iLimit) {
        this.oVectorLA = new LengthLimitedAverage[3];
        for (int i = 0; i < 3; i++) {
            this.oVectorLA[i] = new LengthLimitedAverage(iLimit);
        }
    }

    public void setAverageLengthAndLimit(int iLength, int iLimit) {
        for (int i = 0; i < 3; i++) {
            this.oVectorLA[i].setLength(iLength);
            this.oVectorLA[i].setLimit(iLimit);
        }
    }

    public boolean isReady() {
        return this.oVectorLA[0].isReady();
    }

    public int getAverageLength() {
        return this.oVectorLA[0].getLength();
    }

    public int getAverageLimit() {
        return this.oVectorLA[0].getLimit();
    }

    public void reset() {
        int i;
        if (this.oVectorLA == null) {
            this.oVectorLA = new LengthLimitedAverage[3];
        }
        for (i = 0; i < 3; i++) {
            if (this.oVectorLA[i] == null) {
                this.oVectorLA[i] = new LengthLimitedAverage(1);
            }
        }
        for (i = 0; i < 3; i++) {
            this.rVector[i] = 0.0d;
        }
        set(this.rVector);
        for (i = 0; i < 3; i++) {
            this.oVectorLA[i].reset();
        }
    }

    public void set(double[] rValues) {
        if (rValues != null && rValues.length >= 3) {
            int i;
            for (i = 0; i < 3; i++) {
                this.oVectorLA[i].set(rValues[i]);
            }
            for (i = 0; i < 3; i++) {
                this.rVector[i] = this.oVectorLA[i].getAny();
            }
            double rSumSqr = 0.0d;
            for (i = 0; i < 3; i++) {
                rSumSqr += AngMath.sqr(this.rVector[i]);
            }
            this.rValue = Math.sqrt(rSumSqr);
            if (this.rValue > 0.0d) {
                for (i = 0; i < 3; i++) {
                    this.rUnitVector[i] = this.rVector[i] / this.rValue;
                }
                return;
            }
            for (i = 0; i < 3; i++) {
                this.rUnitVector[i] = 0.0d;
            }
            this.rUnitVector[0] = 1.0d;
        }
    }

    public void correct(double[] rValues) {
        if (rValues != null && rValues.length >= 3) {
            int i;
            for (i = 0; i < 3; i++) {
                this.oVectorLA[i].correct(rValues[i]);
            }
            for (i = 0; i < 3; i++) {
                this.rVector[i] = this.oVectorLA[i].getAny();
            }
            double rSumSqr = 0.0d;
            for (i = 0; i < 3; i++) {
                rSumSqr += AngMath.sqr(this.rVector[i]);
            }
            this.rValue = Math.sqrt(rSumSqr);
            if (this.rValue > 0.0d) {
                for (i = 0; i < 3; i++) {
                    this.rUnitVector[i] = this.rVector[i] / this.rValue;
                }
                return;
            }
            for (i = 0; i < 3; i++) {
                this.rUnitVector[i] = 0.0d;
            }
            this.rUnitVector[0] = 1.0d;
        }
    }

    public void setAndRepeat(ForceVector oVector, int iRepeater) {
        if (oVector != null) {
            set(oVector.rVector);
            for (int i = 0; i < iRepeater; i++) {
                set(oVector.rVector);
            }
        }
    }

    public void set(ForceVector oVector) {
        if (oVector != null) {
            set(oVector.rVector);
        }
    }

    public void correct(ForceVector oVector) {
        if (oVector != null) {
            correct(oVector.rVector);
        }
    }

    public String toString() {
        return String.format("%.2f[%.0f],[%.2f,%.2f,%.2f],[%.2f,%.2f,%.2f]", new Object[]{Double.valueOf(this.rValue), Double.valueOf((double) this.oVectorLA[0].getLength()), Double.valueOf(this.rVector[0]), Double.valueOf(this.rVector[1]), Double.valueOf(this.rVector[2]), Double.valueOf(this.rUnitVector[0]), Double.valueOf(this.rUnitVector[1]), Double.valueOf(this.rUnitVector[2])});
    }
}
