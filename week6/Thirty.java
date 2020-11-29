import java.util.*;
import java.util.concurrent.*;
import java.io.*;
a
public class Thirty {
	// data space
	static LinkedBlockingQueue<String> wordSpace;
	static LinkedBlockingQueue<Map<String, Integer>> freqSpace;
	static Set<String> stopWords;

	// word process worker
	static class WordProcessWorker extends Thread {
		Map<String, Integer> wordFreqs;

		public WordProcessWorker() {
			wordFreqs = new HashMap<>();
		}
		
		@Override
		public void run() {
			while (true) {
				String word = null;
				try {
					word = wordSpace.poll(1, TimeUnit.SECONDS);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (word == null) {
					break;
				}
				if (!stopWords.contains(word) && word.length() >= 2) {
					wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
				}
			}
			freqSpace.offer(wordFreqs);
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// args[0] is the file path of input FileReader
		
		// data space
		wordSpace = new LinkedBlockingQueue<>();
		freqSpace = new LinkedBlockingQueue<>();
		
		// read stop words
		stopWords = new HashSet<>();
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

		// read file
		sb = null;
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(args[0]));
			sb = new StringBuilder();
			String tmp = "";
			while((tmp = buffReader.readLine()) != null){
				String[] words = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
				for (String word: words) {
					wordSpace.offer(word);
				}
			}
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// initial 5 workers
		Thread[] workers = new Thread[5];
		for (int i = 0; i < 5; i++) {
			workers[i] = new WordProcessWorker();
		}
		for (Thread t : workers) {
			t.start();
		}
		for (Thread t : workers) {
			try {
				t.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// merge frequency counter
		Map<String, Integer> counter = new HashMap<>();
		for (Map<String, Integer> map : freqSpace) {
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
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


