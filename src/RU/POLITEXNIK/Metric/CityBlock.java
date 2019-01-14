package RU.POLITEXNIK.Metric;

public class CityBlock implements MetricsInterface {
    @Override
    public double getDistance(byte[] imageArr, byte[][] digitTrainingArr, int indexInDigitTrainingArr) {
        double currentDistance = 0;
        for (int j = 0; j < digitTrainingArr[0].length; j++) {
            int delta = (imageArr[j] - digitTrainingArr[indexInDigitTrainingArr][j]);
            currentDistance += (delta >= 0) ? delta : -delta;
        }
        return currentDistance;
    }
}
