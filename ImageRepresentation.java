
public class ImageRepresentation {
	private int[] content = null;
	private int height = 0;
	private int width = 0;
	private int elementSize = 0;

	public int getElementSize() {
		return elementSize;
	}

	public int Height() {
		return height;
	}

	public int Width() {
		return width;
	}

	public void copyFrom(ImageRepresentation mat) {
		height = mat.height;
		width = mat.width;
		elementSize = mat.elementSize;
		if (mat.content != null) {
			content = new int[height * width * elementSize];
			System.arraycopy(mat.content, 0, content, 0, height * width * elementSize);
		} else {
			content = null;
		}
	}

	public ImageRepresentation(int h, int w, int es) {
		height = h;
		width = w;
		elementSize = es;
		content = new int[height * width * elementSize];
	}

	public ImageRepresentation(ImageRepresentation other) {
		this.copyFrom(other);
	}

	public int fetch(int h, int w, int componentIdx) {
		return this.content[elementSize * (h * this.width + w) + componentIdx];
	}

	public void set(int h, int w, int componentIdx, int value) {
		this.content[elementSize * (h * this.width + w) + componentIdx] = value;
	}

	public void increase(int h, int w, int componentIdx, int value) {
		this.content[elementSize * (h * this.width + w) + componentIdx] += value;
	}

	public String toString() {
		System.out.printf("elementSize: %d\n", elementSize);
		System.out.printf("height: %d\n", height);
		System.out.printf("width: %d\n", width);
		String res = "";
		for (int h = 0; h < this.height; h++) {
			for (int w = 0; w < this.width; w++) {
				res += "(";
				for (int i = 0; i < elementSize; i++) {
					res += new Integer(this.fetch(h, w, i)).toString();
					if (i + 1 != elementSize) {
						res += ", ";
					}
				}
				res += ")";
			}
			res += "\n";
		}
		return res;
	}

	// only for ImageMatrix
	// sum error for distanceSurfaceMetric's result.
	int distanceSumMetric(ImageTile inputTile, int outputHeight, int outputWidth, int overlapSize) {
		int sum = 0;
		ImageRepresentation errorSurcface = this.distanceSurfaceMetric(inputTile, outputHeight, outputWidth,
				overlapSize);
		for (int h = 0; h < errorSurcface.Height(); h++) {
			for (int w = 0; w < errorSurcface.Width(); w++) {
				sum += errorSurcface.fetch(h, w, 0);
			}
		}
		return sum;
	}

	/*
	 * my distance metric implementation A L2-norm distance between color vertor
	 */
	ImageRepresentation distanceSurfaceMetric(ImageTile inputTile, int outputHeight, int outputWidth,
			int overlapSize) {
		int textonSquareSize = inputTile.TextonSquareSize();
		ImageRepresentation result = new ImageRepresentation(textonSquareSize, textonSquareSize, 1);
		ImageTile outputTile = new ImageTile(this, textonSquareSize, outputHeight, outputWidth);
		{
			for (int h = 0; h < overlapSize; h++) {
				for (int w = 0; w < overlapSize; w++) {
					int tmpValue = 0;
					for (int i = 0; i < 3; i++) {
						int diff = outputTile.fetch(h, w, i) - inputTile.fetch(h, w, i);
						tmpValue += diff * diff;
					}
					result.set(h, w, 0, tmpValue);
				}
			}
			if (outputWidth != 0) {
				for (int h = overlapSize; h < textonSquareSize; h++) {
					for (int w = 0; w < overlapSize; w++) {
						int tmpValue = 0;
						for (int i = 0; i < 3; i++) {
							int diff = outputTile.fetch(h, w, i) - inputTile.fetch(h, w, i);
							tmpValue += diff * diff;
						}
						result.set(h, w, 0, tmpValue);
					}
				}
			}

			if (outputHeight != 0) {
				for (int h = 0; h < overlapSize; h++) {
					for (int w = overlapSize; w < textonSquareSize; w++) {
						int tmpValue = 0;
						for (int i = 0; i < 3; i++) {
							int diff = outputTile.fetch(h, w, i) - inputTile.fetch(h, w, i);
							tmpValue += diff * diff;
						}
						result.set(h, w, 0, tmpValue);
					}
				}
			}
		}
		return result;
	}
}
