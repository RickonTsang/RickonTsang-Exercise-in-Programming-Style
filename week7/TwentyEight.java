import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.util.stream.*;

// using stream instead of generator

class TwentyEight {

	public static HashMap<String, Integer> wordFreq = new HashMap<String, Integer>();
    public static int count = 0;

	public static Stream<String> line(String path) {
		try {
			Stream<String> stream = Files.lines(Paths.get(path));
			return stream;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

	public static Stream<String> allWords(String path) {
		Stream<String> stream = line(path).flatMap(o1 -> Arrays.stream(o1.toLowerCase().split("\\P{Alnum}"))).filter(o2 -> o2.length() >= 2);
		return stream;
    }

    public static Stream<String> nonStopWords(String path) {
		List<String> stopWords = new ArrayList<String>();
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader("../stop_words.txt"));
			String tmp = "";
			while ((tmp = buffReader.readLine()) != null) {
				String[] arr = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
				for (String str : arr) {
					if (str.length() >= 2) {
						stopWords.add(str);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		Stream<String> stream = allWords(path).filter(o1 -> !stopWords.contains(o1));
        return stream;
    }

    public static Stream<List<Map.Entry<String, Integer>>> countAndSort(String path) {
		Stream<List<Map.Entry<String, Integer>>> stream = nonStopWords(path).map(o1 -> {
			wordFreq.put(o1, wordFreq.getOrDefault(o1, 0) + 1);
			count++;
			if (count % 5000 == 0) {
				return wordFreq.entrySet().stream().sorted((o2, o3) -> o3.getValue() - o2.getValue()).collect(Collectors.toList());
        	} else {
				return null;
			}
		}).filter(s -> s != null);
		return stream;
    }

	public static void main(String[] args) {
		wordFreq = new HashMap<String, Integer>();
		try {
			countAndSort(args[0]).forEach(o1 -> {
				System.out.println("------------");
				o1.stream().limit(25).forEach(o2 -> {

					System.out.println(o2.getKey() + "  -  " + o2.getValue());
				});
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}


