package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
	
	public static List<String> readFile(String fileName) throws IOException{
		return Files.lines(Paths.get(fileName)).collect(Collectors.toList());
	}
	
	public static void saveImage(BufferedImage img, String fileName) {
		try {
			ImageIO.write(img, "PNG", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveImageTimestamped(BufferedImage img, String dir, String name) {
		DateFormat df = new SimpleDateFormat("yyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String s = dir + name + '_' + df.format(date) + ".png";
		saveImage(img, s);
	}
}
