import java.io.FileNotFoundException;
import java.io.IOException;

public class Synthesis {
    public static void main(String[] argv) throws FileNotFoundException, IOException {
        String inputImage = "";
        String outputImage = "";
        int textonSize = 0;
        int outputSize = 0;
        for (int i = 0; i < argv.length; i++) { 
            if (argv[i].equals("-input") && (i + 1) < argv.length) {
                inputImage = argv[i + 1];
            } else if (argv[i].equals("-texton-size") && (i + 1) < argv.length) {
                textonSize = Integer.parseInt(argv[i + 1]);
            } else if (argv[i].equals("-output-size") && (i + 1) < argv.length) {
                outputSize = Integer.parseInt(argv[i + 1]);
            } else if (argv[i].equals("-output") && (i + 1) < argv.length) {
                outputImage = argv[i + 1];
            }
        }

        if (inputImage.equals("") || outputImage.equals("") || textonSize <= 0 || outputSize <= 0) 
        {
            System.out.printf("error 1\n");
            return;
        }
        IoHelper.WriteImage((new ImageAnalysis(IoHelper.ReadImage(inputImage), textonSize, outputSize, outputSize)).process(), outputImage);
    }
}
