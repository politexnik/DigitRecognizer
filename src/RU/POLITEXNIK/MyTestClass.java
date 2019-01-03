package RU.POLITEXNIK;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class MyTestClass {
    public static void main(String[] args) throws IOException, Exception {
        DigitRecognizer digitRecognizer = new DigitRecognizer(Paths.get("FileArhives\\train-labels.idx1-ubyte"),
                Paths.get("FileArhives\\train-images.idx3-ubyte"));

        System.out.println(digitRecognizer.recognize(Paths.get("FileArhives\\t10k-labels.idx1-ubyte"),
                Paths.get("FileArhives\\t10k-images.idx3-ubyte"), Metrics.EVKLIDE));
    }
}
