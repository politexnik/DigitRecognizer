package RU.POLITEXNIK;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DigitRecognizer {
    private byte[][] digitTrainingArr;
    private byte[] labelsTrainingArr;
    private int itemsTrainingNumber;
    private int rowsInImage;
    private int columnsInImage;

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
        dataInputStreamLabels.readInt();

        rowsInImage = dataInputStreamDigits.readInt();  //размер изображения по вертикали
        dataInputStreamLabels.readInt();

        columnsInImage = dataInputStreamLabels.readInt();   //размер изображения по горизонтали
        dataInputStreamLabels.readInt();

        digitTrainingArr = new byte[itemsTrainingNumber] [rowsInImage * columnsInImage];
        labelsTrainingArr = new byte[itemsTrainingNumber];

        for (int i = 0; i < itemsTrainingNumber; i++) {
            labelsTrainingArr[i] = dataInputStreamLabels.readByte();
            for (int j = 0; j < rowsInImage * columnsInImage; j++) {
                digitTrainingArr[i] [j] = dataInputStreamDigits.readByte();
            }
        }



    }



}
