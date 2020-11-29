import java.util.*;
import java.io.*;
import java.util.function.*;

@SuppressWarnings("unchecked")
class TwentySeven {
	public static void main(String[] args) {
		// spreadsheet
		// column is a pair structure
		// key for data, value for lambda function


		// 1st column all_words
		Pair<List<String>, Object> allWords = new Pair<>();
		allWords.setKey(new ArrayList<>());
		allWords.setValue(null);

		// 2nd column stop_words
		Pair<List<String>, Object> stopWords = new Pair<>();
		stopWords.setKey(new ArrayList<>());
		stopWords.setValue(null);

		// 3rd column non_stop_words
		Pair<List<String>, Object> nonStopWords = new Pair<>();
		nonStopWords.setKey(new ArrayList<>());
		nonStopWords.setValue((Supplier)(() -> {
			ArrayList<String> nonStopWordsData = new ArrayList<>();
			for (String str : allWords.key) {
				if (!stopWords.key.contains(str)) {
					nonStopWordsData.add(str);
				}
			}
			return nonStopWordsData;
		}));

		

		// 4th column unique_words
		Pair<List<String>, Object> uniqueWords = new Pair<>();
		uniqueWords.setKey(new ArrayList<>());
		uniqueWords.setValue((Supplier)(() -> {
			ArrayList<String> uniqueWordsData = new ArrayList<>(new HashSet<String>(nonStopWords.key));
			return uniqueWordsData;
		}));

		

		// 5th column count
		Pair<List<Integer>, Object> counts = new Pair<>();
		counts.setKey(new ArrayList<>());
		counts.setValue((Supplier)(() -> {
			ArrayList<Integer> countsData = new ArrayList<>();
			for (int i = 0; i < uniqueWords.key.size(); i++) {
				countsData.add(0);
			}
			
			for (int i = 0; i < nonStopWords.key.size(); i++) {
				int index = uniqueWords.key.indexOf(nonStopWords.key.get(i));
				countsData.set(index, countsData.get(index) + 1);
			}
			return countsData;
		}));
		

		// 6th column sorted_data
		Pair<List<Pair<String, Integer>>, Object> sorted = new Pair<>();
		sorted.setKey(new ArrayList<>());
		sorted.setValue((Supplier)(() -> {
			ArrayList<Pair<String, Integer>> sortedData = new ArrayList<>();
			PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>((o1, o2) -> o2.value - o1.value);
			for (int i = 0; i < uniqueWords.key.size(); i++) {
				Pair<String, Integer> pair = new Pair<>(uniqueWords.key.get(i), counts.key.get(i));
				pq.offer(pair);
			}

			while (!pq.isEmpty()) {
				sortedData.add(pq.poll());
			}
			return sortedData;
		}));
		
		// all columns
		ArrayList<Pair> allColumns = new ArrayList<>();
		allColumns.add(allWords);
		allColumns.add(stopWords);
		allColumns.add(nonStopWords);
		allColumns.add(uniqueWords);
		allColumns.add(counts);
		allColumns.add(sorted);

		// load data for 1st and 2nd column
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(args[0]));
			String tmp = "";
			while ((tmp = buffReader.readLine()) != null) {
				String[] arr = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
				for (String str : arr) {
					if (str.length() >= 2) {
						allWords.key.add(str);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}

		try {
			BufferedReader buffReader = new BufferedReader(new FileReader("../stop_words.txt"));
			String tmp = "";
			while ((tmp = buffReader.readLine()) != null) {
				String[] arr = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
				for (String str : arr) {
					if (str.length() >= 2) {
						stopWords.key.add(str);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}

		// update
		for (Pair cur : allColumns) {
			if (cur.value != null) {
				cur.key = ((Supplier)cur.value).get();
			}
		}
		
		// print top25
		for (int i = 0; i < 25; i++) {
			System.out.println(sorted.key.get(i).getKey() + "  -  " + sorted.key.get(i).getValue());
		}
		
		// interactive
		while (true) {
			System.out.println("");
			System.out.println("input the path of file you want to add or input 'end' to exit");
			Scanner sc = new Scanner(System.in);
			String path = sc.next();
			if (path.equals("end")) {
				break;
			}
			
			// add words
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(path));
				String tmp = "";
				while ((tmp = buffReader.readLine()) != null) {
					String[] arr = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
					for (String str : arr) {
						if (str.length() >= 2) {
							allWords.key.add(str);
						}
					}
				}
			} catch (IOException e){
				e.printStackTrace();
			}

			// update
			for (Pair cur : allColumns) {
				if (cur.value != null) {
					cur.key = ((Supplier)cur.value).get();
				}
			}
			
			// print top25
			for (int i = 0; i < 25; i++) {
				System.out.println(sorted.key.get(i).getKey() + "  -  " + sorted.key.get(i).getValue());
			}
		}
	}
}

class Pair<K, V> {
	public K key;
	public V value;

	public Pair() {
		this.key = null;
		this.value = null;
	}

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return this.key;
	}

	public V getValue() {
		return this.value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}
}

