package RU.POLITEXNIK;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DigitRecognizer {
    private byte[][] digitTrainingArr;
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
        FileInputStream fileInputStreamDigits = new FileInputStream(imagesIdx3.toString());
        DataInputStream dataInputStreamDigits = new DataInputStream(fileInputStreamDigits);
        //labels
        FileInputStream fileInputStreamLabels = new FileInputStream(labelsIdx3.toString());
        DataInputStream dataInputStreamLabels = new DataInputStream(fileInputStreamLabels);

        dataInputStreamDigits.readInt();    //читает magic number Idx3 файлов
        dataInputStreamLabels.readInt();

        itemsTrainingNumber = dataInputStreamDigits.readInt();  //количество изображений в наборе
        System.out.println("itemsTrainingNumber = " + itemsTrainingNumber);
        System.out.println(dataInputStreamLabels.readInt());

        rowsInImage = dataInputStreamDigits.readInt();  //размер изображения по вертикали
        System.out.println("rowsInImage = " + rowsInImage);

        columnsInImage = dataInputStreamDigits.readInt();   //размер изображения по горизонтали
        System.out.println("columnsInImage = " + columnsInImage);

        digitTrainingArr = new byte[itemsTrainingNumber] [rowsInImage * columnsInImage];
        labelsTrainingArr = new byte[itemsTrainingNumber];

        for (int i = 0; i < itemsTrainingNumber; i++) { //заполняем массивы images и labels
            labelsTrainingArr[i] = dataInputStreamLabels.readByte();
            digitTrainingArr[i] = dataInputStreamDigits.readNBytes(rowsInImage * columnsInImage);
        }
        closestTypesCount = 30;
    }

    public byte recognize(byte[] imageArr) {
        //Создаем массив дистанций, вычисляем по каждому элементу digitTrainingArr, заполняем
        double[] distanceArr = new double[itemsTrainingNumber];
        double currentDistance = 0;
        for (int i = 0; i < itemsTrainingNumber; i++) {
            for (int j = 0; j < rowsInImage * columnsInImage; j++) {
                currentDistance += imageArr[j] * imageArr[j] - digitTrainingArr[i][j] * digitTrainingArr[i][j];
            }
            distanceArr[i] = Math.sqrt(currentDistance);
        }

        ArrayForSort arrayForSort = new ArrayForSort(labelsTrainingArr, distanceArr);
        arrayForSort.sort();

        byte[] closestDigits = Arrays.copyOf(arrayForSort.labels, closestTypesCount);
        Map<Byte, Byte> freqMap = new TreeMap<>();
        for (byte b: closestDigits) {
            freqMap.put(b, (byte)(freqMap.getOrDefault(b, (byte)0) + 1));
        }
        return ((TreeMap<Byte, Byte>) freqMap).firstKey();
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
