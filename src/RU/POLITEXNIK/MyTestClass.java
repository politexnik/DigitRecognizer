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

        /*columnsInImage = */dataInputStreamDigits.readInt();   //размер изображения по горизонтали

        byte[][] digitTrainingArr = new byte[1000] [28*28];
        byte[] labelsTrainingArr = new byte[1000];

        for (int i = 0; i < 1000; i++) { //заполняем массивы images и labels
            labelsTrainingArr[i] = dataInputStreamLabels.readByte();
            dataInputStreamDigits.read(digitTrainingArr[i]);
            for (int j = 0; j < 28*28; j++) {
                digitTrainingArr[i][j] = (byte)(dataInputStreamDigits.readByte() & 0xff);
            }
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(labelsTrainingArr[i] + " vs " + digitRecognizer.recognize(digitTrainingArr[i]));
        }




    }



}
