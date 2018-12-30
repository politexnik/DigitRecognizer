package RU.POLITEXNIK;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class MyTestClass {
    public static void main(String[] args) throws IOException {
        DigitRecognizer digitRecognizer = new DigitRecognizer(Paths.get("FileArhives\\train-labels.idx1-ubyte"),
                Paths.get("FileArhives\\train-images.idx3-ubyte"));

        //images
        DataInputStream dataInputStreamDigits = new DataInputStream(new BufferedInputStream(new FileInputStream("FileArhives\\t10k-images.idx3-ubyte"), 784000));
        //labels
        FileInputStream fileInputStreamLabels = new FileInputStream("FileArhives\\t10k-labels.idx1-ubyte");
        DataInputStream dataInputStreamLabels = new DataInputStream(new BufferedInputStream(new FileInputStream("FileArhives\\t10k-labels.idx1-ubyte"), 784000));

        dataInputStreamDigits.readInt();    //читает magic number Idx3 файлов
        dataInputStreamLabels.readInt();

        /*itemsTrainingNumber = */dataInputStreamDigits.readInt();  //количество изображений в наборе
        dataInputStreamLabels.readInt();

        /*rowsInImage = */dataInputStreamDigits.readInt();  //размер изображения по вертикали

        /*columnsInImage = */dataInputStreamDigits.readInt();   //размер изображения по горизонтали

        int[][] digitTrainingArr = new int[10000] [28*28];
        byte[] labelsTrainingArr = new byte[10000];

        for (int i = 0; i < 10000; i++) { //заполняем массивы images и labels
            labelsTrainingArr[i] = dataInputStreamLabels.readByte();
            //dataInputStreamDigits.read(digitTrainingArr[i]);
            for (int j = 0; j < 28*28; j++) {
                digitTrainingArr[i][j] = dataInputStreamDigits.readByte() & 0xff;
            }
        }
        dataInputStreamDigits.close();
        dataInputStreamLabels.close();

        int count = 0;
        for (int i = 0; i < 10000; i++) {
            byte label = labelsTrainingArr[i];
            byte labelRecognized = digitRecognizer.recognize(digitTrainingArr[i]);
            if (label != labelRecognized) {
                //count++;
                System.out.println(++count);
                if (count == 58) {
                    System.out.println();
                }
                System.out.println(label + " vs " + labelRecognized);
            }
            //System.out.println(labelsTrainingArr[i] + " vs " + digitRecognizer.recognize(digitTrainingArr[i]));
        }
        System.out.println(count);



    }



}
