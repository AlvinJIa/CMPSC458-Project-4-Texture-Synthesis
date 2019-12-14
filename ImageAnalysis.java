import java.util.ArrayList;
import java.util.Random;

public class ImageAnalysis {
    private ImageRepresentation inputImg;
    private int inputHeight = 0;
    private int inputWidth = 0;
    private int textonSquareSize = 0;
    private int overlapSize = 0;
    private int orgOutputHeight = 0;
    private int orgOutputWidth = 0;
    private int outputHeight = 0;
    private int outputWidth = 0;
    private int overlapProgress = 0;
    private ImageRepresentation outputImg;
    private static Random random = new Random();

    public ImageAnalysis(ImageRepresentation ir, int tss, int ooh, int oow) {
        this.inputImg = ir;
        this.textonSquareSize = tss;
        int minTextonSize = Math.min(ir.Height(), ir.Width());
        if (minTextonSize < this.textonSquareSize) {
            this.textonSquareSize = minTextonSize;
        }
        // recommended by the doc
        this.overlapSize = this.textonSquareSize / 3;
        if (this.overlapSize <= 0) {
            this.overlapSize = 1;
        }
        this.orgOutputHeight = ooh;
        this.orgOutputWidth = oow;

        int Ws = 0;
        int Hs = 0;
        this.overlapProgress = this.textonSquareSize - this.overlapSize;

        if (this.textonSquareSize < this.orgOutputWidth) {
            double tmpValue = (this.orgOutputWidth - this.textonSquareSize) / 1.0 / overlapProgress;
            Ws = (int) tmpValue;
            if (tmpValue != (int) tmpValue)
                Ws++;
        }

        if (this.textonSquareSize < this.orgOutputHeight) {
            double tmpValue = (this.orgOutputHeight - this.textonSquareSize) / 1.0 / overlapProgress;
            Hs = (int) tmpValue;
            if (tmpValue != (int) tmpValue)
                Hs++;
        }

        this.outputHeight = this.textonSquareSize + Hs * overlapProgress;
        this.outputWidth = this.textonSquareSize + Ws * overlapProgress;

        this.outputImg = new ImageRepresentation(this.outputHeight, this.outputWidth, 3);

        this.inputHeight = this.inputImg.Height();
        this.inputWidth = this.inputImg.Width();
    }

    class ErrorInfo {
        int first = 0;
        int second = 0;

        ErrorInfo(int f, int s) {
            first = f;
            second = s;
        }
    };

    public ImageRepresentation process() {
        int totalSteps = 0;
        for (int tileH = 0; tileH + this.textonSquareSize <= this.outputHeight; tileH += this.overlapProgress) {
            totalSteps++;
        }
        int currentStepIdx = 0;
        for (int loopI = 0; loopI < totalSteps; loopI++) {
            int tileH = loopI * this.overlapProgress;
            for (int tileW = 0; tileW + this.textonSquareSize <= this.outputWidth; tileW += this.overlapProgress) {
                ImageTile outputTile = new ImageTile(this.outputImg, this.textonSquareSize, tileH, tileW);
                int tileHeightCount = this.inputHeight - this.textonSquareSize + 1;
                int tileWidthCount = this.inputWidth - this.textonSquareSize + 1;
                if (tileH == 0 && tileW == 0) {
                    int usingTileH = random.nextInt(tileHeightCount);
                    int usingTileW = random.nextInt(tileWidthCount);
                    ImageTile inputTile = new ImageTile(this.inputImg, this.textonSquareSize, usingTileH, usingTileW);
                    outputTile.FillTile(inputTile);
                } else {
                    ArrayList<ErrorInfo> errArr = new ArrayList<ErrorInfo>();
                    for (int inputTileHBegin = 0; inputTileHBegin < tileHeightCount; inputTileHBegin++) {
                        for (int inputTileWBegin = 0; inputTileWBegin < tileWidthCount; inputTileWBegin++) {
                            int loc = inputTileHBegin * tileWidthCount + inputTileWBegin;
                            int err = this.outputImg.distanceSumMetric(new ImageTile(this.inputImg, this.textonSquareSize,inputTileHBegin, inputTileWBegin), tileH, tileW, this.overlapSize);
                            errArr.add(new ErrorInfo(loc, err));
                        }
                    }
                    assert (errArr.size() > 0);
                    int minErr = errArr.get(0).second;
                    for (int i = 0; i < errArr.size(); i++) {
                        if (errArr.get(i).second < minErr) {
                            minErr = errArr.get(i).second;
                        }
                    }
                    double toleranceErr = minErr * 1.1;
                    ArrayList<Integer> candidateArr = new ArrayList<Integer>();
                    for (int i = 0; i < errArr.size(); i++) {
                        if (errArr.get(i).second <= toleranceErr) {
                            candidateArr.add(i);
                        }
                    }

                    int selectI = candidateArr.get(random.nextInt(candidateArr.size()));
                    int usingTileH = selectI / tileWidthCount;
                    int usingTileW = selectI % tileWidthCount;

                    ImageTile inputTile = new ImageTile(this.inputImg, this.textonSquareSize, usingTileH, usingTileW);
                    outputTile.FillTile(inputTile, outputTile.getCutFlags(inputTile, this.overlapSize));
                }
            }
            currentStepIdx++;
            System.out.printf("finised: %.2f%%(%d/%d)\n", currentStepIdx / 1.0 / totalSteps * 100.0, currentStepIdx,
                    totalSteps);
        }

        ImageRepresentation normOutputImage = new ImageRepresentation(this.orgOutputHeight, this.orgOutputWidth, 3);
        for (int h = 0; h < normOutputImage.Height(); h++) {
            for (int w = 0; w < normOutputImage.Width(); w++) {
                for (int i = 0; i < 3; i++) {
                    normOutputImage.set(h, w, i, this.outputImg.fetch(h, w, i));
                }
            }
        }
        return normOutputImage;
    }
}
