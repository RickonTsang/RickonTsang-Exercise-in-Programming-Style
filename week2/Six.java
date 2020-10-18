import java.util.*;
import java.io.*;

class Six {

	// read file and return a string "data"
	private static String readFile(String path) {
		String data;
		StringBuilder sb = null;
		try {
            FileInputStream fin = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(fin);
            BufferedReader buffReader = new BufferedReader(reader);
			sb = new StringBuilder();
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
				sb.append(tmp + " ");
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		data = sb.toString();
		return data;
	}
	
	// replace all nonalphanumeric chars in data with white space and normalize to lowercase
	private static String filterCharsAndNormalize(String data) {
		data = data.replaceAll("\\P{Alnum}", " ").toLowerCase();
		return data;
	}

	// remove stop_words and return a list
	private static List<String> removeStopWords(String data) {
		// scan stop_words
		ArrayList<String> stopWords = new ArrayList<>();
		try {
            FileInputStream fin = new FileInputStream("../stop_words.txt");
            InputStreamReader reader = new InputStreamReader(fin);
            BufferedReader buffReader = new BufferedReader(reader);
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
				String[] arr = tmp.split(",");
                for (String str : arr) {
                    stopWords.add(str);
                }
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

		// remove stop_words and load to List "words"
		List<String> words = new ArrayList<>();
		for (String str : data.split(" ")) {
			// remove single character and stop_words
			if (!stopWords.contains(str) && str.length() >= 2) {
				words.add(str);
			}
		}
		return words;
	}

	// count frequency of each word and return a hashmap "wordFreqs"
	private static Map<String, Integer> frequencies(List<String> words) {
		Map<String, Integer> wordFreqs = new HashMap<>();
		for (String str : words) {
			wordFreqs.put(str, wordFreqs.getOrDefault(str, 0) + 1);
		}
		return wordFreqs;
	}

	// sort word frequencies and print top 25 frequent words
	private static void sortAndPrint(Map<String, Integer> wordFreqs) {
		PriorityQueue<String> pq = new PriorityQueue<>((o1, o2) -> wordFreqs.get(o1) - wordFreqs.get(o2));
        for (String str : wordFreqs.keySet()) {
            pq.offer(str);
			if (pq.size() > 25) {
                pq.poll();
            }
        }
		// output the top 25 frequent words
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(0, pq.poll());
        }
        for (String str : list) {
            System.out.println(str + "  -  " + wordFreqs.get(str));
        }
	}


	public static void main(String[] args) {
		// args[0] is the file path of input file
		sortAndPrint(frequencies(removeStopWords(filterCharsAndNormalize(readFile(args[0])))));
	}
}