package RU.POLITEXNIK;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadingDigitsArchiveClass {

    public static void main(String[] args) throws IOException {
        // write your code here
        FileInputStream fileInputStreamDigits = new FileInputStream("FileArhives\\t10k-images.idx3-ubyte");
        DataInputStream dataInputStreamDigits = new DataInputStream(fileInputStreamDigits);

        FileInputStream fileInputStreamLabels = new FileInputStream("FileArhives\\t10k-labels.idx1-ubyte");
        DataInputStream dataInputStreamLabels = new DataInputStream(fileInputStreamLabels);
        //читаем magic number Digits
        try(dataInputStreamDigits; dataInputStreamLabels) {
            System.out.println("magic number digits");
            for (int i = 0; i < 4; i++) {
                System.out.println(dataInputStreamDigits.readByte());
            }
            //параметры digits
            System.out.println("Параметры digits");
            System.out.println("number of items" + " " + dataInputStreamDigits.readInt());
            System.out.println("number of rows" + " " + dataInputStreamDigits.readInt());
            System.out.println("number of columns" + " " + dataInputStreamDigits.readInt());

            //magic number labels
            System.out.println("magic number labels");
            for (int i = 0; i < 4; i++) {
                System.out.println(dataInputStreamLabels.readByte());
            }

            System.out.println("number of items Labels" + " " + dataInputStreamLabels.readInt());

            for (int m = 0; m < 30; m++) {
                System.out.println("Цифра " + dataInputStreamLabels.readByte() + "\n");

                for (int i = 0; i < 28; i++) {
                    for (int j = 0; j < 28; j++) {
                        System.out.printf("%4d", dataInputStreamDigits.readByte() & 0xff);
                    }
                    System.out.println();
                }
                System.out.println();
            }
        }





    }
}
