import java.util.*;
import java.io.*;
import java.util.function.*;
import java.nio.file.*;
class Words2 implements Function<String, String[]>{
	// list remove all
	@Override
	public String[] apply(String filePath) {
		
        List<String> stopWords = new ArrayList<>();
		try {
            stopWords = new ArrayList<String>(
                    Arrays.asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(",")));
        } catch (Exception e) {
            System.out.println(e);
        }

		List<String> words = new ArrayList<>();
        try {
            words = new ArrayList<String>(
                    Arrays.asList(new String(Files.readAllBytes(Paths.get(filePath))).replaceAll("\\P{Alnum}", " ").toLowerCase().split(",")));
        } catch (Exception e) {
            System.out.println(e);
        }
		List<String> resWords = new ArrayList<>();
		for (String word : words) {
			if (word.length() >= 2) {
				resWords.add(word);
			}
		}
        resWords.removeAll(stopWords);
        return resWords.toArray(new String[resWords.size()]);
	}
}