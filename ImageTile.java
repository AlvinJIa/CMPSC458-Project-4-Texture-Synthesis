
public class ImageTile {
    private ImageRepresentation referenceImg;
    private int textonSquareSize;
    private int tileHeightBegin;
    private int tileWidthBegin;

    public int TextonSquareSize() {
        return textonSquareSize;
    }

    ImageTile(ImageRepresentation ir, int tss, int thb, int twb) {
        referenceImg = ir;
        textonSquareSize = tss;
        tileHeightBegin = thb;
        tileWidthBegin = twb;
    }

    void FillTile(ImageTile tile) {
        for (int h = 0; h < this.textonSquareSize; h++) {
            for (int w = 0; w < this.textonSquareSize; w++) {
                for (int i = 0; i < referenceImg.getElementSize(); i++) {
                    this.fill(h, w, i, tile.fetch(h, w, i));
                }
            }
        }
    }

    void FillTile(ImageTile tile, ImageRepresentation fillFlags) {
        for (int h = 0; h < this.textonSquareSize; h++) {
            for (int w = 0; w < this.textonSquareSize; w++) {
                if (fillFlags.fetch(h, w, 0) != 0) {
                    for (int i = 0; i < referenceImg.getElementSize(); i++) {
                        this.fill(h, w, i, tile.fetch(h, w, i));
                    }
                }
            }
        }
    }

    ImageRepresentation getCutFlags(ImageTile inputTile, int overlapSize) {
        ImageRepresentation errorSurface = this.referenceImg.distanceSurfaceMetric(inputTile, this.tileHeightBegin, this.tileWidthBegin,
                overlapSize);
        ImageRepresentation curFlags = new ImageRepresentation(inputTile.textonSquareSize, inputTile.textonSquareSize, 1);
        for (int h = 0; h < curFlags.Height(); h++) {
            for (int w = 0; w < curFlags.Width(); w++) {
                curFlags.set(h, w, 0, 1);
            }
            if (this.tileHeightBegin != 0) {
                ImageRepresentation trackIdx = new ImageRepresentation(overlapSize, this.textonSquareSize, 1);
                ImageRepresentation copyErrorSurface = new ImageRepresentation(errorSurface);
                for (int w = 1; w < this.textonSquareSize; w++) {
                    for (int h1 = 0; h1 < overlapSize; h1++) {
                        int rows[] = { h1 - 1, h1, h1 + 1 };
                        int minI = h1;
                        for (int i = 0; i < 3; i++) {
                            if (rows[i] == minI)
                                continue;
                            if (rows[i] < 0)
                                continue;
                            if (rows[i] >= overlapSize)
                                continue;
                            if (copyErrorSurface.fetch(rows[i], w - 1, 0) < copyErrorSurface.fetch(minI, w - 1, 0)) {
                                minI = rows[i];
                            }
                        }
                        copyErrorSurface.increase(h1, w, 0, copyErrorSurface.fetch(minI, w - 1, 0));
                        trackIdx.set(h1, w, 0, minI);
                    }
                }

                int minI = 0;
                for (int i = 1; i < overlapSize; i++) {
                    if (copyErrorSurface.fetch(i, this.textonSquareSize - 1, 0) < copyErrorSurface.fetch(minI,
                            this.textonSquareSize - 1, 0)) {
                        minI = i;
                    }
                }

                int[] splitIdxs = new int[this.textonSquareSize];
                for (int i = 0; i < this.textonSquareSize; i++) {
                    int currentI = this.textonSquareSize - 1 - i;
                    splitIdxs[currentI] = minI;
                    minI = trackIdx.fetch(minI, currentI, 0);
                }

                for (int w = 0; w < this.textonSquareSize; w++) {
                    for (int h1 = 0; h1 < overlapSize; h1++) {
                        if (h1 > splitIdxs[w])
                            break;
                        else
                            curFlags.set(h1, w, 0, 0);
                    }
                }
            }

            if (this.tileWidthBegin != 0) {
                ImageRepresentation trackIdx = new ImageRepresentation(this.textonSquareSize, overlapSize, 1);
                ImageRepresentation copyErrorSurface = new ImageRepresentation(errorSurface);
                for (int h1 = 1; h1 < this.textonSquareSize; h1++) {
                    for (int w = 0; w < overlapSize; w++) {
                        int cols[] = { w - 1, w, w + 1 };
                        int minI = w;
                        for (int i = 0; i < 3; i++) {
                            if (cols[i] == minI)
                                continue;
                            if (cols[i] < 0)
                                continue;
                            if (cols[i] >= overlapSize)
                                continue;
                            if (copyErrorSurface.fetch(h1 - 1, cols[i], 0) < copyErrorSurface.fetch(h1 - 1, minI, 0)) {
                                minI = cols[i];
                            }
                        }
                        copyErrorSurface.increase(h1, w, 0, copyErrorSurface.fetch(h1 - 1, minI, 0));
                        trackIdx.set(h1, w, 0, minI);
                    }
                }

                int minI = 0;
                for (int i = 1; i < overlapSize; i++) {
                    if (copyErrorSurface.fetch(this.textonSquareSize - 1, i, 0) < copyErrorSurface.fetch(this.textonSquareSize - 1,
                            minI, 0)) {
                        minI = i;
                    }
                }
                int[] splitIdxs = new int[this.textonSquareSize];
                for (int i = 0; i < this.textonSquareSize; i++) {
                    int currentI = this.textonSquareSize - 1 - i;
                    splitIdxs[currentI] = minI;
                    minI = trackIdx.fetch(currentI, minI, 0);
                }

                for (int h1 = 0; h1 < this.textonSquareSize; h1++) {
                    for (int w = 0; w < overlapSize; w++) {
                        if (w > splitIdxs[h1])
                            break;
                        else
                            curFlags.set(h1, w, 0, 0);
                    }
                }
            }
        }
        return curFlags;
    }

    public int fetch(int h, int w, int componentIdx) {
        return referenceImg.fetch(h + tileHeightBegin, w + tileWidthBegin, componentIdx);
    }

    public void fill(int h, int w, int componentIdx, int value) {
        referenceImg.set(h + tileHeightBegin, w + tileWidthBegin, componentIdx, value);
    }

    public String toString() {
        String res = "";
        for (int h = 0; h < this.textonSquareSize; h++) {
            for (int w = 0; w < this.textonSquareSize; w++) {
                res += "(";
                for (int i = 0; i < this.referenceImg.getElementSize(); i++) {
                    res += new Integer(this.fetch(h, w, i)).toString();
                    if (i + 1 != this.referenceImg.getElementSize()) {
                        res += ", ";
                    }
                }
                res += ")";
            }
            res += "\n";
        }
        return res;
    }
}
