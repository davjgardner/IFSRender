package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
	
	public static List<String> readFile(String fileName) throws IOException{
		return Files.lines(Paths.get(fileName)).collect(Collectors.toList());
	}
}
