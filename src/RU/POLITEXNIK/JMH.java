package RU.POLITEXNIK;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.samples.JMHSample_01_HelloWorld;

import java.nio.file.Paths;

public class JMH {
    public JMH(){}


    @Benchmark
    public void wellHelloThere() throws Exception {
        DigitRecognizer digitRecognizer = new DigitRecognizer(Paths.get("FileArhives\\train-labels.idx1-ubyte"),
                Paths.get("FileArhives\\train-images.idx3-ubyte"));

        digitRecognizer.recognizeOneImage(Paths.get("FileArhives\\t10k-labels.idx1-ubyte"),
                Paths.get("FileArhives\\t10k-images.idx3-ubyte"), Metrics.EVKLIDE);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = (new OptionsBuilder()).include(JMHSample_01_HelloWorld.class.getSimpleName()).forks(1).build();
        (new Runner(opt)).run();
    }
}
