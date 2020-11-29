import java.util.*;
import java.io.*;
import java.util.function.*;

// ugly code using casting class
class Thirteen {

	// extract word
	private static void extractWord(Map<String, Object> map, String path) {
		StringBuilder sb = null;
		try {
            BufferedReader buffReader = new BufferedReader(new FileReader(path));
			sb = new StringBuilder();
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
				sb.append(tmp + " ");
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		map.put("data", new ArrayList<String>(Arrays.asList(sb.toString().replaceAll("\\P{Alnum}", " ").toLowerCase().split(" "))));
	}

	// load stop words
	private static void loadStopWords(Map<String, Object> map) {
		StringBuilder sb = null;
		List<String> list = new ArrayList<>(); 
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader("../stop_words.txt"));
			sb = new StringBuilder();
			String tmp = "";
			while((tmp = buffReader.readLine()) != null){
				String[] arr = tmp.split(",");
				for (String str : arr) {
					list.add(str);
				}
			}
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		map.put("stop_words", list);
	}

	// sorted
	public static List<Map.Entry<String, Integer>> sorted(HashMap<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
		return list;
	}
	

	// // count frequency
	// @SuppressWarnings("unchecked")
	// private static void count(Map<String, Object> map, String word) {
	// 	Map<String, Integer> wordFreqs = (Map<String, Integer>)map.get("freqs");
	// 	wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
	// 	map.put("freqs", wordFreqs);
	// }

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// args[0] is the file path of input file

		// data object
		Map<String, Object> dataStorageObj = new HashMap<>();
		dataStorageObj.put("data", new ArrayList<String>());
		dataStorageObj.put("init", (Consumer)(path -> extractWord(dataStorageObj, (String)path)));
		dataStorageObj.put("words", (Supplier)(() ->  dataStorageObj.get("data")));

		// stop words object
		Map<String, Object> stopWordsObj = new HashMap<>();
		stopWordsObj.put("stop_words", new ArrayList<String>());
		stopWordsObj.put("init", (Consumer)((x) ->  loadStopWords(stopWordsObj)));
		stopWordsObj.put("check", (Predicate)((word) -> ((ArrayList<String>)stopWordsObj.get("stop_words")).contains((String)word)));

		// word frequencies object
		Map<String, Object> wordFreqsObj = new HashMap<>();
		wordFreqsObj.put("freqs", new HashMap<String, Integer>());
		wordFreqsObj.put("count", (Consumer)((word) ->  ((HashMap<String, Integer>)wordFreqsObj.get("freqs")).put((String)word, ((HashMap<String, Integer>)wordFreqsObj.get("freqs")).getOrDefault(word, 0) + 1)));
		wordFreqsObj.put("sorted", (Supplier)(() -> {
			List<Map.Entry<String, Integer>> list = new ArrayList<>(((HashMap<String, Integer>)wordFreqsObj.get("freqs")).entrySet());
			Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
			return list;
		}));
		wordFreqsObj.put("print", (Consumer)((x) -> {
			List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>)((Supplier)wordFreqsObj.get("sorted")).get();
			for (int i = 0; i < 25; i++) {
				System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
			}
		}));


		((Consumer)dataStorageObj.get("init")).accept(args[0]);
		((Consumer)stopWordsObj.get("init")).accept(0);
		for (String word : (ArrayList<String>)((Supplier)dataStorageObj.get("words")).get()) {
				if (!((Predicate)stopWordsObj.get("check")).test(word) && word.length() >= 2) {
					((Consumer)wordFreqsObj.get("count")).accept(word);
				}
		}
		// ((Supplier)wordFreqsObj.get("sorted")).get();
		((Consumer)wordFreqsObj.get("print")).accept(0);

	}
}