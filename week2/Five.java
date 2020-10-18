import java.util.*;
import java.io.*;

class Five {
	static String data;
	static List<String> words;
	static Map<String, Integer> wordFreqs;
	static PriorityQueue<String> pq;

	// read file and load to a global string "data"
	private static void readFile(String path) {
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
	}
	
	// replace all nonalphanumeric chars in data with white space and normalize to lowercase
	private static void filterCharsAndNormalize() {
		data = data.replaceAll("\\P{Alnum}", " ").toLowerCase();
	}

	// remove stop_words
	private static void removeStopWords() {
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
		words = new ArrayList<>();
		for (String str : data.split(" ")) {
			// remove single character and stop_words
			if (!stopWords.contains(str) && str.length() >= 2) {
				words.add(str);
			}
		}
	}

	// count frequency of each word and save to hashmap "wordFreqs"
	private static void frequencies() {
		wordFreqs = new HashMap<>();
		for (String str : words) {
			wordFreqs.put(str, wordFreqs.getOrDefault(str, 0) + 1);
		}
	}

	// sort word frequencies and save top 25 results into pq
	private static void sort() {
		pq = new PriorityQueue<>((o1, o2) -> wordFreqs.get(o1) - wordFreqs.get(o2));
        for (String str : wordFreqs.keySet()) {
            pq.offer(str);
			if (pq.size() > 25) {
                pq.poll();
            }
        }
	}


	public static void main(String[] args) {
		// args[0] is the file path of input file
        
		readFile(args[0]);
		filterCharsAndNormalize();
		removeStopWords();
		frequencies();
		sort();

		// output the top 25 frequent words
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(0, pq.poll());
        }
        for (String str : list) {
            System.out.println(str + "  -  " + wordFreqs.get(str));
        }
	}
}