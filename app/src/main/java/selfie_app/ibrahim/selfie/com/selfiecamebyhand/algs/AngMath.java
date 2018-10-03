package selfie_app.ibrahim.selfie.com.selfiecamebyhand.algs;

public class AngMath {
    public static final double D_DEG_TO_RAD = 0.017453292519943295d;
    public static final double D_RAD_TO_DEG = 57.29577951308232d;
    public static final double PI = 3.141592653589793d;
    public static final double R_DEG_TO_RAD = 0.017453292519943295d;
    public static final double R_RAD_TO_DEG = 57.29577951308232d;

    public static double check(double rInput) {
        if (Double.isNaN(rInput)) {
            throw new IllegalArgumentException("NaN");
        } else if (!Double.isInfinite(rInput)) {
            return rInput;
        } else {
            throw new IllegalArgumentException("Infinity");
        }
    }

    public static synchronized double atan2(double rY, double rX) {
        double check;
        synchronized (AngMath.class) {
            double rResult = Math.atan2(rY, rX) * 57.29577951308232d;
            while (rResult >= 360.0d) {
                rResult -= 360.0d;
            }
            while (rResult < 0.0d) {
                rResult += 360.0d;
            }
            check = check(rResult);
        }
        return check;
    }

    public static synchronized double sqr(double rV) {
        double check;
        synchronized (AngMath.class) {
            check = check(rV * rV);
        }
        return check;
    }

    public static synchronized double sin(double rA) {
        double check;
        synchronized (AngMath.class) {
            check = check(Math.sin(0.017453292519943295d * rA));
        }
        return check;
    }

    public static synchronized double cos(double rA) {
        double check;
        synchronized (AngMath.class) {
            check = check(Math.cos(0.017453292519943295d * rA));
        }
        return check;
    }

    public static synchronized double acos(double rV) {
        double check;
        synchronized (AngMath.class) {
            check = check(Math.acos(rV) * 57.29577951308232d);
        }
        return check;
    }

    public static synchronized double asin(double rV) {
        double check;
        synchronized (AngMath.class) {
            check = check(Math.asin(rV) * 57.29577951308232d);
        }
        return check;
    }

    public static synchronized double add(double rA, double rB) {
        double check;
        synchronized (AngMath.class) {
            check = check(toNormalPositive(rA + rB));
        }
        return check;
    }

    public static synchronized double sub(double rA, double rB) {
        double check;
        synchronized (AngMath.class) {
            check = check(toNormal(rA - rB));
        }
        return check;
    }

    public static synchronized double toNormal(double rA) {
        double check;
        synchronized (AngMath.class) {
            double rResult = toNormalPositive(rA);
            if (rResult > 180.0d) {
                rResult -= 360.0d;
            }
            check = check(rResult);
        }
        return check;
    }

    public static synchronized double toNormalPositive(double rA) {
        double check;
        synchronized (AngMath.class) {
            double rResult = rA;
            while (rResult >= 360.0d) {
                rResult -= 360.0d;
            }
            while (rResult < 0.0d) {
                rResult += 360.0d;
            }
            check = check(rResult);
        }
        return check;
    }

    public static synchronized double diff(double rA, double rB) {
        double check;
        synchronized (AngMath.class) {
            check = check(Math.abs(sub(rA, rB)));
        }
        return check;
    }

    public static void main(String[] args) {
    }
}
