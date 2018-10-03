package selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs;

public class ForceVector {
    public final int I_DIMENSIONS;
    private ForceVector oNegative;
    public double[] rUnitVector;
    public double rValue;
    public double[] rVector;

    public ForceVector() {
        this.I_DIMENSIONS = 3;
        this.rVector = new double[3];
        this.rValue = 0.0d;
        this.rUnitVector = new double[3];
        this.oNegative = null;
        reset();
    }

    public ForceVector(double rX, double rY, double rZ) {
        this.I_DIMENSIONS = 3;
        this.rVector = new double[3];
        this.rValue = 0.0d;
        this.rUnitVector = new double[3];
        this.oNegative = null;
        reset();
        this.rVector[0] = rX;
        this.rVector[1] = rY;
        this.rVector[2] = rZ;
        set(this.rVector);
    }

    public boolean isReady() {
        return this.rValue != 0.0d;
    }

    public void reset() {
        for (int i = 0; i < 3; i++) {
            this.rVector[i] = 0.0d;
            this.rUnitVector[i] = 0.0d;
        }
        set(this.rVector);
        this.rValue = 0.0d;
    }

    public void set(double[] irVector) {
        if (irVector != null && irVector.length >= 3) {
            int i;
            for (i = 0; i < 3; i++) {
                this.rVector[i] = irVector[i];
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
            } else {
                for (i = 0; i < 3; i++) {
                    this.rUnitVector[i] = 0.0d;
                }
            }
            this.oNegative = null;
        }
    }

    public void set(ForceVector oVector) {
        if (oVector != null) {
            set(oVector.rVector);
        }
    }

    public void setValue(double rNewValue) {
        for (int i = 0; i < 3; i++) {
            this.rUnitVector[i] = this.rUnitVector[i] * rNewValue;
        }
        set(this.rUnitVector);
    }

    public ForceVector subtract(ForceVector oSubtrahend, ForceVector oResult) {
        if (oResult == null) {
            oResult = new ForceVector();
        }
        for (int i = 0; i < 3; i++) {
            oResult.rVector[i] = this.rVector[i] - oSubtrahend.rVector[i];
        }
        oResult.set(oResult.rVector);
        return oResult;
    }

    public void addDeltaVector(ForceVector oDelta, double rScale) {
        for (int i = 0; i < 3; i++) {
            double[] dArr = this.rVector;
            dArr[i] = dArr[i] + (oDelta.rVector[i] * rScale);
        }
        set(this.rVector);
    }

    public void scale(double rScale) {
        for (int i = 0; i < 3; i++) {
            this.rVector[i] = this.rVector[i] * rScale;
        }
        set(this.rVector);
    }

    public double getAngleTo(ForceVector oVector) {
        double rScalar = ((oVector.rUnitVector[0] * this.rUnitVector[0]) + (oVector.rUnitVector[1] * this.rUnitVector[1])) + (oVector.rUnitVector[2] * this.rUnitVector[2]);
        if (rScalar > 1.0d) {
            rScalar = 1.0d;
        }
        if (rScalar < -1.0d) {
            rScalar = -1.0d;
        }
        return AngMath.acos(rScalar);
    }

    public double getAngleToLine(ForceVector oVector) {
        double rScalar1 = ((oVector.rUnitVector[0] * this.rUnitVector[0]) + (oVector.rUnitVector[1] * this.rUnitVector[1])) + (oVector.rUnitVector[2] * this.rUnitVector[2]);
        if (rScalar1 > 1.0d) {
            rScalar1 = 1.0d;
        }
        if (rScalar1 < -1.0d) {
            rScalar1 = -1.0d;
        }
        double rScalar2 = -rScalar1;
        double rAlpha1 = AngMath.acos(rScalar1);
        double rAlpha2 = AngMath.acos(rScalar2);
        return rAlpha1 < rAlpha2 ? rAlpha1 : rAlpha2;
    }

    public double getUnitChordTo(ForceVector oVector) {
        return Math.sqrt((AngMath.sqr(this.rUnitVector[0] - oVector.rUnitVector[0]) + AngMath.sqr(this.rUnitVector[1] - oVector.rUnitVector[1])) + AngMath.sqr(this.rUnitVector[2] - oVector.rUnitVector[2]));
    }

    public double getMinUnitChordTo(ForceVector oVector) {
        double rChord1 = Math.sqrt((AngMath.sqr(this.rUnitVector[0] - oVector.rUnitVector[0]) + AngMath.sqr(this.rUnitVector[1] - oVector.rUnitVector[1])) + AngMath.sqr(this.rUnitVector[2] - oVector.rUnitVector[2]));
        double rChord2 = Math.sqrt((AngMath.sqr((-this.rUnitVector[0]) - oVector.rUnitVector[0]) + AngMath.sqr((-this.rUnitVector[1]) - oVector.rUnitVector[1])) + AngMath.sqr((-this.rUnitVector[2]) - oVector.rUnitVector[2]));
        return rChord1 < rChord2 ? rChord1 : rChord2;
    }

    public ForceVector getUnitCrossProductTo(ForceVector oVector, ForceVector oOutput) {
        oOutput.rUnitVector[0] = (this.rUnitVector[1] * oVector.rUnitVector[2]) - (this.rUnitVector[2] * oVector.rUnitVector[1]);
        oOutput.rUnitVector[1] = (this.rUnitVector[2] * oVector.rUnitVector[0]) - (this.rUnitVector[0] * oVector.rUnitVector[2]);
        oOutput.rUnitVector[2] = (this.rUnitVector[0] * oVector.rUnitVector[1]) - (this.rUnitVector[1] * oVector.rUnitVector[0]);
        oOutput.set(oOutput.rUnitVector);
        return oOutput;
    }

    public void negate() {
        this.rVector[0] = -this.rVector[0];
        this.rVector[1] = -this.rVector[1];
        this.rVector[2] = -this.rVector[2];
        set(this.rVector);
    }

    public ForceVector getNegative() {
        if (this.oNegative == null) {
            this.oNegative = new ForceVector();
        }
        this.oNegative.set(this);
        this.oNegative.negate();
        return this.oNegative;
    }

    public ForceVector rotateAroundVector(ForceVector oAxisVector, double rAngle) {
        ForceVector oResult = new ForceVector();
        double u = oAxisVector.rUnitVector[0];
        double v = oAxisVector.rUnitVector[1];
        double w = oAxisVector.rUnitVector[2];
        double x = this.rUnitVector[0];
        double y = this.rUnitVector[1];
        double z = this.rUnitVector[2];
        double ux = u * x;
        double uy = u * y;
        double uz = u * z;
        double vx = v * x;
        double vy = v * y;
        double vz = v * z;
        double wx = w * x;
        double wy = w * y;
        double wz = w * z;
        double sa = AngMath.sin(rAngle);
        double ca = AngMath.cos(rAngle);
        oResult.rVector[0] = ((((ux + vy) + wz) * u) + (((((v * v) + (w * w)) * x) - ((vy + wz) * u)) * ca)) + (((-wy) + vz) * sa);
        oResult.rVector[1] = ((((ux + vy) + wz) * v) + (((((u * u) + (w * w)) * y) - ((ux + wz) * v)) * ca)) + ((wx - uz) * sa);
        oResult.rVector[2] = ((((ux + vy) + wz) * w) + (((((u * u) + (v * v)) * z) - ((ux + vy) * w)) * ca)) + (((-vx) + uy) * sa);
        oResult.set(oResult.rVector);
        oResult.setValue(this.rValue);
        return oResult;
    }

    public String toString() {
        return String.format("%.2f,[%.2f,%.2f,%.2f],[%.2f,%.2f,%.2f]", new Object[]{Double.valueOf(this.rValue), Double.valueOf(this.rVector[0]), Double.valueOf(this.rVector[1]), Double.valueOf(this.rVector[2]), Double.valueOf(this.rUnitVector[0]), Double.valueOf(this.rUnitVector[1]), Double.valueOf(this.rUnitVector[2])});
    }

    public String toStringShort() {
        return String.format("%.1f,[%.1f,%.1f,%.1f]", new Object[]{Double.valueOf(this.rValue), Double.valueOf(this.rUnitVector[0]), Double.valueOf(this.rUnitVector[1]), Double.valueOf(this.rUnitVector[2])});
    }

    public static void main(String[] args) {
    }
}
