package com.techlung.android.glow.utils;

public class Gauss {
    double[] gauss;
    int screenHeightPx;
    double scale;

    public Gauss(int screenHeightPx, double scale) {
        this.gauss = new double[screenHeightPx];
        this.scale = scale;
        this.screenHeightPx = screenHeightPx;

        for (int y = 0; y < screenHeightPx; ++y) {
            gauss[y] = gauss(screenYToGaussX(y));
        }
    }

    public double gaussForScreenY(int screenY) {
        if (screenY < 0) {
            return gauss[0];
        } else if (screenY > screenHeightPx - 1) {
            return gauss[screenHeightPx - 1];
        } else {
            return gauss[screenY];
        }
    }

    private double gauss(double x) {
        return 1 / (Math.sqrt(2 * Math.PI)) * Math.exp(-Math.pow(x, 2) / 2);
    }

    private double screenYToGaussX(float y) {
        return (((y / screenHeightPx) - 0.5f) * 8.0f * scale);
    }
}
