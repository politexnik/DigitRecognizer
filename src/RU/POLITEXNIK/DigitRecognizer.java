package RU.POLITEXNIK;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DigitRecognizer {
    private int[][] digitTrainingArr;
    private byte[] labelsTrainingArr;
    private int itemsTrainingNumber;
    private int rowsInImage;
    private int columnsInImage;

    public void setClosestTypesCount(int closestTypesCount) {
        this.closestTypesCount = closestTypesCount;
    }

    private int closestTypesCount;

    public DigitRecognizer(Path labelsIdx3, Path imagesIdx3) throws IOException {
        //images
        DataInputStream dataInputStreamDigits = new DataInputStream(new BufferedInputStream(new FileInputStream(imagesIdx3.toString()),784000));
        //labels
        DataInputStream dataInputStreamLabels = new DataInputStream(new BufferedInputStream(new FileInputStream(labelsIdx3.toString()), 784000));

        dataInputStreamDigits.readInt();    //читает magic number Idx3 файлов
        dataInputStreamLabels.readInt();

        itemsTrainingNumber = dataInputStreamDigits.readInt();  //количество изображений в наборе
        System.out.println("itemsTrainingNumber = " + itemsTrainingNumber);
        System.out.println(dataInputStreamLabels.readInt());

        rowsInImage = dataInputStreamDigits.readInt();  //размер изображения по вертикали
        System.out.println("rowsInImage = " + rowsInImage);

        columnsInImage = dataInputStreamDigits.readInt();   //размер изображения по горизонтали
        System.out.println("columnsInImage = " + columnsInImage);

        digitTrainingArr = new int[itemsTrainingNumber] [rowsInImage * columnsInImage];
        labelsTrainingArr = new byte[itemsTrainingNumber];

        for (int i = 0; i < itemsTrainingNumber; i++) { //заполняем массивы images и labels
            labelsTrainingArr[i] = dataInputStreamLabels.readByte();
            //dataInputStreamDigits.read(digitTrainingArr[i]);
            for (int j = 0; j < rowsInImage * columnsInImage; j++) {
                digitTrainingArr[i][j] = (dataInputStreamDigits.readByte() & 0xff);
            }
        }
        closestTypesCount = 30;
        dataInputStreamDigits.close();
        dataInputStreamLabels.close();

    }

    public byte recognize(int[] imageArr) {
        //Создаем массив дистанций, вычисляем по каждому элементу digitTrainingArr, заполняем
        double[] distanceArr = new double[itemsTrainingNumber];
        double currentDistance = 0;
        for (int i = 0; i < itemsTrainingNumber; i++) {
            for (int j = 0; j < rowsInImage * columnsInImage; j++) {
                currentDistance += (imageArr[j] - digitTrainingArr[i][j]) * (imageArr[j] - digitTrainingArr[i][j]);
            }
            distanceArr[i] = Math.sqrt(currentDistance);
            currentDistance = 0;
        }

        ArrayForSort arrayForSort = new ArrayForSort(labelsTrainingArr, distanceArr);
        arrayForSort.sort();

        byte[] closestDigits = Arrays.copyOf(arrayForSort.labels, closestTypesCount);
        Map<Byte, Integer> freqMap = new TreeMap<>();
        for (byte b: closestDigits) {
            freqMap.put(b, (freqMap.getOrDefault(b, 0) + 1));
        }

        //выводим нужный самый частый клас из соседей
        int max = 0;
        byte digit = 0;
        for (Map.Entry<Byte, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                digit = entry.getKey();
            }
        }
        return digit;
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



}
