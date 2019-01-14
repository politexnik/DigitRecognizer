package RU.POLITEXNIK;

import RU.POLITEXNIK.Metric.EvklideMetric;
import RU.POLITEXNIK.Metric.MetricsInterface;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DigitRecognizer {
    private byte[][] digitTrainingArr;
    private byte[] labelsTrainingArr;
    private int itemsTrainingNumber;
    private int sizeForDigit;

    public void setClosestTypesCount(int closestTypesCount) {
        this.closestTypesCount = closestTypesCount;
    }

    private int closestTypesCount;

    public DigitRecognizer(Path labelsIdx3, Path imagesIdx3) throws IOException, Exception {
        digitTrainingArr = readIdxPathWithDigits(imagesIdx3);
        labelsTrainingArr = readIdxPathWithLabels(labelsIdx3);
        if (digitTrainingArr.length != labelsTrainingArr.length) {
            throw new Exception("количество значений в массивах с данными цифр и меток не равны!");
        }
        itemsTrainingNumber = labelsTrainingArr.length;
        sizeForDigit = digitTrainingArr[0].length;
        closestTypesCount = 10;
    }

    public byte recognize(byte[] imageArr, Metrics metric) {
        //Создаем массив дистанций, вычисляем по каждому элементу digitTrainingArr, заполняем
        double[] distanceArr = new double[itemsTrainingNumber];
        double currentDistance = 0;
        for (int i = 0; i < itemsTrainingNumber; i++) {
            if (metric == Metrics.EVKLIDE) {
                for (int j = 0; j < sizeForDigit; j++) {
                    int delta = (imageArr[j] - digitTrainingArr[i][j]);
                        currentDistance += delta * delta;
                }
                distanceArr[i] = Math.sqrt(currentDistance);
            } else {
                for (int j = 0; j < sizeForDigit; j++) {
                    int delta = (imageArr[j] - digitTrainingArr[i][j]);
                    currentDistance += (delta >= 0) ? delta : -delta;
                }
                distanceArr[i] = currentDistance;
            }
            currentDistance = 0;
        }

        ArrayForSort arrayForSort = new ArrayForSort(labelsTrainingArr, distanceArr);
        arrayForSort.sort();

        return findClassFromNeighbours(arrayForSort);
    }

    public byte recognizeWithMethods(byte[] imageArr, MetricsInterface metric) {
        //Создаем массив дистанций, вычисляем по каждому элементу digitTrainingArr, заполняем
        double[] distanceArr = new double[itemsTrainingNumber];
        double currentDistance = 0;
        for (int i = 0; i < itemsTrainingNumber; i++) {
            distanceArr[i] = metric.getDistance(imageArr, digitTrainingArr, i);
        }

        ArrayForSort arrayForSort = new ArrayForSort(labelsTrainingArr, distanceArr);
        arrayForSort.sort();

        return findClassFromNeighbours(arrayForSort);
    }

    // Возвращает значение несовпадения в долях от тестировочного массива
    public  double recognize(Path labelsIdx3, Path imagesIdx3, Metrics metric) throws IOException, Exception {
        //читаем файлы, заполняем отдельные массивы
        byte[] labelsTestArr = readIdxPathWithLabels(labelsIdx3);
        byte[][] digitTestArr = readIdxPathWithDigits(imagesIdx3);

        if (labelsTestArr.length != digitTestArr.length) {
            throw new Exception("Размеры массивов данных labels и digits не совпадают!");
        }
        //int count = 0;
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger percent = new AtomicInteger(0);
        System.out.print("Завершено " + percent.get() + "%");
        Thread[] threads = new Thread[4];   //обработку запускаем в 4 потока
        for (AtomicInteger atI = new AtomicInteger(0); atI.get() < threads.length; atI.incrementAndGet()) {
            int k = atI.get();
            Thread t = new Thread( () -> {
                for (int i = k * (labelsTestArr.length / threads.length); i < (k + 1) * (labelsTestArr.length / threads.length); i++) {
                    if (i > 0 && i % (labelsTestArr.length / 100) == 0) {
                        System.out.print("\rЗавершено " + percent.incrementAndGet() + "%");
                    }
                    byte label = labelsTestArr[i];
                    byte labelRecognized = recognize(digitTestArr[i], metric);
                    if (label != labelRecognized) {
                        count.incrementAndGet();
                    }
                }
            });
            threads[atI.get()] = t;
            t.start();
        }
        for (Thread t: threads) {
            t.join();
        }

        System.out.println();
        return count.get() * 1.0 / labelsTestArr.length;
    }

    public void recognizeOneImage(Path labelsIdx3, Path imagesIdx3, Metrics metric) throws IOException, Exception {
        //читаем файлы, заполняем отдельные массивы
        byte[] labelsTestArr = readIdxPathWithLabels(labelsIdx3);
        byte[][] digitTestArr = readIdxPathWithDigits(imagesIdx3);

        if (labelsTestArr.length != digitTestArr.length) {
            throw new Exception("Размеры массивов данных labels и digits не совпадают!");
        }
        //int count = 0;
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger percent = new AtomicInteger(0);
        System.out.print("Завершено " + percent.get() + "%");

        recognize(digitTestArr[5], metric);
    }

    public void recognizeOneImageWithMethods(Path labelsIdx3, Path imagesIdx3, MetricsInterface metric) throws IOException, Exception {
        //читаем файлы, заполняем отдельные массивы
        byte[] labelsTestArr = readIdxPathWithLabels(labelsIdx3);
        byte[][] digitTestArr = readIdxPathWithDigits(imagesIdx3);

        if (labelsTestArr.length != digitTestArr.length) {
            throw new Exception("Размеры массивов данных labels и digits не совпадают!");
        }
        //int count = 0;
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger percent = new AtomicInteger(0);
        System.out.print("Завершено " + percent.get() + "%");

        recognizeWithMethods(digitTestArr[5], metric);
    }

    private byte[][] readIdxPathWithDigits(Path imagesIdx3) throws IOException {
        //images
        DataInputStream dataInputStreamDigits = new DataInputStream(new BufferedInputStream(
                new FileInputStream(imagesIdx3.toString()), 1024*10));
        dataInputStreamDigits.readInt();    //читает magic number Idx3 файла с цифрами
        int itemsNumber = dataInputStreamDigits.readInt();  //количество изображений в наборе
        int rowsInImage = dataInputStreamDigits.readInt();  //размер изображения по вертикали
        int columnsInImage = dataInputStreamDigits.readInt();   //размер изображения по горизонтали

        byte[][] digitArr = new byte[itemsNumber] [rowsInImage * columnsInImage]; //возвращаемый массив
        //заполняем массив
        for (int i = 0; i < itemsNumber; i++) { //заполняем массивы images и labels
            for (int j = 0; j < rowsInImage * columnsInImage; j++) {
                digitArr[i][j] = (byte)(dataInputStreamDigits.readByte() & 0xff - 128);
            }
        }
        dataInputStreamDigits.close();
        return digitArr;
    }

    //читает файл с labels
    private byte[]  readIdxPathWithLabels(Path labelsIdx) throws IOException {
        //labels
        DataInputStream dataInputStreamLabels = new DataInputStream(new BufferedInputStream(
                new FileInputStream(labelsIdx.toString()), 1024 * 10));

        dataInputStreamLabels.readInt();            //читает magic number Idx3 файлов
        int countLabels = dataInputStreamLabels.readInt();  //количество изображений в наборе
        byte[] labelsArr = new byte[countLabels];   //возвращаемый массив
        for (int i = 0; i < countLabels; i++) { //заполняем массив labels
            labelsArr[i] = dataInputStreamLabels.readByte();
        }
        dataInputStreamLabels.close();
        return labelsArr;
    }

    private static class ArrayForSort{
        byte[] labels;
        double[] distanceArr;

        ArrayForSort(byte[] labels, double[] distanceArr){
            this.labels = Arrays.copyOf(labels, labels.length); //новый массив, чтобы не затереть массив label при сортировке
            this.distanceArr = distanceArr; //массив distance при сортировке затрется, он не так важен
        }

        void sort(){    //реализуем свою быструю сортировку с одновременной сортировкой массива цифр, чтобы не связываться с дополнительной памятью.
            sort(0, distanceArr.length - 1);
        }

        private void sort(int low, int high){
            if (low >= high)
                return;//завершить выполнение если уже нечего делить

            // выбрать опорный элемент
            int middle = low + (high - low) / 2;
            double opora = distanceArr[middle];

            // разделить на подмассивы, который больше и меньше опорного элемента
            int i = low, j = high;
            while (i <= j) {
                while (distanceArr[i] < opora) {
                    i++;
                }
                while (distanceArr[j] > opora) {
                    j--;
                }
                if (i <= j) {//меняем местами параллельно с labels
                    double tempDigit = distanceArr[i];
                    distanceArr[i] = distanceArr[j];
                    distanceArr[j] = tempDigit;

                    byte tempLabel = labels[i];
                    labels[i] = labels[j];
                    labels[j] = tempLabel;
                    i++;
                    j--;
                }
            }

            // вызов рекурсии для сортировки левой и правой части
            if (low < j)
                sort(low, j);
            if (high > i)
                sort(i, high);
        }
    }

    //поиск по к-ближайшему соседу с учетом расстояния до соседа (веса)
    private byte findClassFromNeighbours(ArrayForSort arrayForSort) {
        byte[] closestDigits = Arrays.copyOf(arrayForSort.labels, closestTypesCount);
        double[] closestDistance = Arrays.copyOf(arrayForSort.distanceArr, closestTypesCount);
        Map<Byte, Double> freqMap = new TreeMap<>();
        for (int i = 0; i < closestDigits.length; i++) {
            freqMap.put(closestDigits[i], (freqMap.getOrDefault(closestDigits[i], 0.0)
                    + 1.0 / closestDistance[i] ));
        }

        //выводим нужный самый частый клас из соседей
        double max = 0;
        byte digit = 0;
        for (Map.Entry<Byte, Double> entry : freqMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                digit = entry.getKey();
            }
        }
        return digit;
    }
}
