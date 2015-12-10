package qube.qai.data;

import java.util.Arrays;

/**
 * Created by rainbird on 12/4/15.
 */
public class Statistics implements MetricTyped {

    private boolean isCalculated;
    private double[] data;
    private int number;
    private double average;
    private double averageDeviation;
    private double standardDeviation;
    private double variance;
    private double kurtosis;
    private double skew;
    private double minimum;
    private double maximum;
    private double percentile;
    private double percentileOf = 50.0D;
    private boolean roundDown = true;

    public Statistics(Object[] data) {
        this.data = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = ((Number)data[i]).doubleValue();
        }
    }

    public Statistics(double[] data) {
        this.data = data;
    }

    public void calculate() {
        if (!this.isCalculated) {
            this.number = this.data.length;
            this.minimum = this.maximum = 0.0D;
            double s = 0.0D;
            double ep = 0.0D;
            double p = 0.0D;

            int i;
            for (i = 0; i < this.data.length; ++i) {
                if (this.data[i] <= 0.0D) {
                    --this.number;
                } else {
                    s += this.data[i];
                    if (this.minimum > this.data[i]) {
                        this.minimum = this.data[i];
                    }

                    if (this.maximum < this.data[i]) {
                        this.maximum = this.data[i];
                    }
                }
            }

            this.average = s / (double) this.number;
            this.averageDeviation = this.variance = this.skew = this.kurtosis = 0.0D;

            for (i = 0; i < this.data.length; ++i) {
                if (this.data[i] > 0.0D) {
                    s = this.data[i] - this.average;
                    this.averageDeviation += Math.abs(s);
                    ep += s;
                    this.variance += p = s * s;
                    this.skew += p *= s;
                    this.kurtosis += p * s;
                }
            }

            if (this.number > 1) {
                this.averageDeviation /= (double) this.number;
                this.variance = (this.variance - ep * ep / (double) this.number) / (double) (this.number - 1);
                this.standardDeviation = Math.sqrt(this.variance);
            }

            if (this.variance != 0.0D) {
                this.skew /= (double) this.number * this.variance * this.standardDeviation;
                this.kurtosis = this.kurtosis / ((double) this.number * this.variance * this.variance) - 3.0D;
            }

            this.calculatePercentile();
            this.isCalculated = true;
        }
    }

    private void calculatePercentile() {
        double factor = this.percentileOf * 0.01D;
        if (factor <= 1.0D) {
            Arrays.sort(this.data);
            int index;
            if (this.roundDown) {
                index = (int) Math.floor((double) this.number * factor);
            } else {
                index = (int) Math.ceil((double) this.number * factor);
            }

            int offset = this.data.length - this.number;
            int i = index + offset - 1;
            if (i < this.data.length && i >= 0) {
                this.percentile = this.data[i];
            } else if (i <= 0) {
                this.percentile = this.data[0];
            } else {
                this.percentile = this.data[this.data.length - 1];
            }

        }
    }

    public Metrics buildMetrics() {
        Metrics metrics = new Metrics();

        if (!isCalculated) {
            calculate();
        }

        metrics.putValue("number", number);
        metrics.putValue("average", average);
        metrics.putValue("average deviation", averageDeviation);
        metrics.putValue("standard deviation", standardDeviation);
        metrics.putValue("variance", variance);
        metrics.putValue("kurtosis", kurtosis);
        metrics.putValue("skew", skew);
        metrics.putValue("minimum", minimum);
        metrics.putValue("maximum", maximum);
        metrics.putValue("percentile", percentile);
        metrics.putValue("percentile of", percentileOf);

        return metrics;
    }

    public int getNumberSamples() {
        return this.number;
    }

    public double getMinimum() {
        return this.minimum;
    }

    public double getMaximum() {
        return this.maximum;
    }

    public double getAverage() {
        return this.average;
    }

    public double getAverageDeviation() {
        return this.averageDeviation;
    }

    public double getStandardDeviation() {
        return this.standardDeviation;
    }

    public double getVariance() {
        return this.variance;
    }

    public double getKurtosis() {
        return this.kurtosis;
    }

    public double getSkew() {
        return this.skew;
    }

    public double getPercentile() {
        return this.percentile;
    }

    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }

    public double getPercentileOf() {
        return this.percentileOf;
    }

    public void setPercentileOf(double percentileOf) {
        this.percentileOf = percentileOf;
    }

    public boolean isRoundDown() {
        return this.roundDown;
    }

    public void setRoundDown(boolean roundDown) {
        this.roundDown = roundDown;
    }
}
