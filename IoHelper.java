import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class IoHelper {
	public static ImageRepresentation ReadImage(String filePath) throws FileNotFoundException, IOException {
		BufferedImage src = ImageIO.read(new FileInputStream(filePath));
		ImageRepresentation image = new ImageRepresentation(src.getHeight(), src.getWidth(), 3);
		for (int h = 0; h < image.Height(); h++) {
			for (int w = 0; w < image.Width(); w++) {
				int rgb = src.getRGB(w, h);
				image.set(h, w, 0, (rgb & (255 << 16)) >> 16);
				image.set(h, w, 1, (rgb & (255 << 8)) >> 8);
				image.set(h, w, 2, (rgb & (255)));
			}
		}
		return image;
	}

	public static void WriteImage(ImageRepresentation image, String filePath) throws IOException {

		BufferedImage dest = new BufferedImage(image.Width(), image.Height(), BufferedImage.TYPE_INT_RGB);
		for (int h = 0; h < image.Height(); h++) {
			for (int w = 0; w < image.Width(); w++) {
				dest.setRGB(w, h, (image.fetch(h, w, 0) << 16) | (image.fetch(h, w, 1) << 8) | image.fetch(h, w, 2));
			}
		}
		File outputfile = new File(filePath);
		ImageIO.write(dest, "jpg", outputfile);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ImageRepresentation h = ReadImage("D:/test/1.jpg");
		WriteImage(h, "D:/test/3.jpg");
	}
}
