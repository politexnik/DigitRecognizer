package RU.POLITEXNIK;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class MyTestClass {
    public static void main(String[] args) throws IOException {
        DigitRecognizer digitRecognizer = new DigitRecognizer(Paths.get("FileArhives\\train-labels.idx1-ubyte"),
                Paths.get("FileArhives\\train-images.idx3-ubyte"));

        //images
        FileInputStream fileInputStreamDigits = new FileInputStream("FileArhives\\t10k-images.idx3-ubyte");
        DataInputStream dataInputStreamDigits = new DataInputStream(fileInputStreamDigits);
        //labels
        FileInputStream fileInputStreamLabels = new FileInputStream("FileArhives\\t10k-labels.idx1-ubyte");
        DataInputStream dataInputStreamLabels = new DataInputStream(fileInputStreamLabels);

        dataInputStreamDigits.readInt();    //читает magic number Idx3 файлов
        dataInputStreamLabels.readInt();

        /*itemsTrainingNumber = */dataInputStreamDigits.readInt();  //количество изображений в наборе
        dataInputStreamLabels.readInt();

        /*rowsInImage = */dataInputStreamDigits.readInt();  //размер изображения по вертикали

        /*columnsInImage = */dataInputStreamLabels.readInt();   //размер изображения по горизонтали

        byte[][] digitTrainingArr = new byte[1000] [28*28];
        byte[] labelsTrainingArr = new byte[1000];

        for (int i = 0; i < 1000; i++) { //заполняем массивы images и labels
            labelsTrainingArr[i] = dataInputStreamLabels.readByte();
            digitTrainingArr[i] = dataInputStreamDigits.readNBytes(28*28);
        }

        System.out.println(labelsTrainingArr[0] + " vs " + digitRecognizer.recognize(digitTrainingArr[0]));
        System.out.println(labelsTrainingArr[1] + " vs " + digitRecognizer.recognize(digitTrainingArr[1]));
        System.out.println(labelsTrainingArr[2] + " vs " + digitRecognizer.recognize(digitTrainingArr[2]));
        System.out.println(labelsTrainingArr[3] + " vs " + digitRecognizer.recognize(digitTrainingArr[3]));



    }



}
