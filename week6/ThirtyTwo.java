import java.util.*;
import java.util.concurrent.*;
import java.io.*;

class Pair<K, V> {
	public K key;
	public V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
}

public class ThirtyTwo {
	static String data;

	
	public static void readFile(String path) {
		StringBuilder sb = null;
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(path));
			sb = new StringBuilder();
			String tmp = "";
			while((tmp = buffReader.readLine()) != null){
				sb.append(tmp);
				sb.append("\n");
			}
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = sb.toString();
	}

	public static List<String[]> partition(int n) {
        List<String[]> res = new ArrayList<>();
        String[] list = data.split("\n");
        
        for (int i = 0; i < list.length; i = i + n) {
            res.add(Arrays.copyOfRange(list, i, Math.min(i + n, list.length)));
        }
        return res;
    }

	static class WordProcessWorker {
		Set<String> stopWords;
        List<Pair<String, Integer>> list;

		public WordProcessWorker() {
			stopWords = new HashSet<>();
			list = new ArrayList<>();
		}

		public List<Pair<String, Integer>> getList() {
			return list;
		}

        public void removeStopWords(String[] wordList) {
			// read stop words
			String path = "../stop_words.txt";
			StringBuilder sb = null;
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(path));
				sb = new StringBuilder();
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

            for (String str : wordList) {
                String[] words = str.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
                for (String word : words) {
                    if (!stopWords.contains(word) && word.length() >= 2) {
                        list.add(new Pair(word, 1));
                    }
                }
            }
        }
    }

	
    public static void map(WordProcessWorker wordProcessWorker, List<String[]> list) {
        for (String[] wordList : list) {
            wordProcessWorker.removeStopWords(wordList);
        }
    }

	static class Reducer extends Thread {
        List<Pair<String, Integer>> list;
        Map<String, Integer> map;

		public Reducer() {
			list = new ArrayList<>();
			map = new HashMap<>();
		}

		@Override
        public void run() {
            for (Pair<String, Integer> p : list) {
                String word = p.key;
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
    }

	public static void regroup(List<Pair<String, Integer>> list, Reducer[] reducer) {
        for (Pair<String, Integer> pair : list) {
            char ch = pair.key.charAt(0);
            if (ch >= 'a' && ch <= 'e') {
                reducer[0].list.add(pair);
            } else if (ch >= 'f' && ch <= 'j') {
                reducer[1].list.add(pair);
            } else if (ch >= 'k' && ch <= 'o') {
                reducer[2].list.add(pair);
            } else if (ch >= 'p' && ch <= 't') {
                reducer[3].list.add(pair);
            } else if (ch >= 'u' && ch <= 'z') {
                reducer[4].list.add(pair);
            }
        }
    }

	@SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception{
		// read word of input file
		readFile(args[0]);

        WordProcessWorker wordProcessWorker = new WordProcessWorker();
		// map wordProcessWorker with wordList
        map(wordProcessWorker, partition(200));


        Reducer[] reducers = new Reducer[5];
        for (int i = 0; i < 5; i++) {
            reducers[i] = new Reducer();
        }

        regroup(wordProcessWorker.getList(), reducers);

        for (Thread t : reducers) {
            t.start();
        }

        for (Thread t : reducers) {
            t.join();
        }

		// merge frequency counter
		Map<String, Integer> counter = new HashMap<>();

		for (Reducer reducer : reducers) {
			for (Map.Entry<String, Integer> entry : reducer.map.entrySet()) {
				counter.put(entry.getKey(), counter.getOrDefault(entry.getKey(), 0) + entry.getValue());
			}
		}

		// sort
		List<Map.Entry<String, Integer>> list = new ArrayList<>(counter.entrySet());	
		Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		// display
		for (int i = 0; i < 25; i++) {
			System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
		}
    }

}