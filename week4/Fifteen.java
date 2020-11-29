import java.util.*;
import java.util.function.*;
import java.io.*;

class Fifteen {
	public static class WordFrequencyFramework {
		private List<Consumer<String>> loadEventHandlers;
		private List<Runnable> doworkEventHandlers;
		private List<Runnable> endEventHandlers;

		public WordFrequencyFramework() {
			loadEventHandlers = new ArrayList<>();
			doworkEventHandlers = new ArrayList<>();
			endEventHandlers = new ArrayList<>();
		}

		public void registerForLoadEvent(Consumer<String> handler) {
			this.loadEventHandlers.add(handler);
		}

		public void registerForDoworkEvent(Runnable handler) {
			this.doworkEventHandlers.add(handler);
		}

		public void registerForEndEvent(Runnable handler) {
			this.endEventHandlers.add(handler);
		}

		public void run(String path) {
			for (Consumer h : loadEventHandlers) {
				h.accept(path);
			}
			for (Runnable h : doworkEventHandlers) {
				h.run();
			}
			for (Runnable h : endEventHandlers) {
				h.run();
			}
		}
	}


	// entity of application
	public static class DataManager {
		String data;
		List<Consumer> wordEventHandlers;
		StopWordManager stopwordsfilter;

		public DataManager(WordFrequencyFramework wfapp, StopWordManager stopwordsfilter) {
			this.data = "";
			this.wordEventHandlers = new ArrayList<>();
			this.stopwordsfilter = stopwordsfilter;
			wfapp.registerForLoadEvent(this::load);
			wfapp.registerForDoworkEvent(this::words);
		}

		// use buffered reader to read the input file(pride-and-prejudice) 
		public void load(String path) {
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
			this.data = sb.toString().replaceAll("\\P{Alnum}", " ").toLowerCase();
		}

		public void words() {
			for (String word : this.data.split(" ")) {
				if (!stopwordsfilter.checkStopWords(word) && word.length() >= 2) {
					for (Consumer<String> h : wordEventHandlers) {
						h.accept(word);
					}
				}
			}
		}

		private void registerWordEventHandler(Consumer<String> handler) {
            wordEventHandlers.add(handler);
        }
	}

	public static class StopWordManager {
		// stopWords save the list of stop words
		List<String> stopWords;

		public StopWordManager(WordFrequencyFramework wfapp) {
			stopWords = new ArrayList<>();
			wfapp.registerForLoadEvent(this::load);
		}

		// use buffered reader to read the stop words(stop_words.txt) 
		public void load(String path) {
			StringBuilder sb = null;
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader("../stop_words.txt"));
				sb = new StringBuilder();
				String tmp = "";
				while((tmp = buffReader.readLine()) != null){
					String[] arr = tmp.split(",");
					for (String str : arr) {
						this.stopWords.add(str);
					}
				}
				buffReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// return whether is stop words
		public boolean checkStopWords(String word) {
			return this.stopWords.contains(word);
		}

	}


	public static class WordFrequencyManager {
		Map<String, Integer> wordFreqs;

		public WordFrequencyManager(WordFrequencyFramework wfapp, DataManager dataStorage) {
			this.wordFreqs = new HashMap<>();
			dataStorage.registerWordEventHandler(this::count);
			wfapp.registerForEndEvent(this::print);
		}

		// count the frequencies
		public void count(String str) {
			this.wordFreqs.put(str, this.wordFreqs.getOrDefault(str, 0) + 1);
		}

		public void print() {
			List<Map.Entry<String, Integer>> list = new ArrayList<>(this.wordFreqs.entrySet());
			Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
			for (int i = 0; i < 25; i++) {
				System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
			}
		}

	}

	public static class WordsWithZ {
		int counter;
		Set<String> res;
		StopWordManager stopwordsfilter;

		public WordsWithZ(WordFrequencyFramework wfapp, DataManager dataStorage,StopWordManager stopwordsfilter) {
			this.counter = 0;
			this.stopwordsfilter = stopwordsfilter;
			this.res = new HashSet<>();
			dataStorage.registerWordEventHandler(this::count);
			wfapp.registerForEndEvent(this::print);
		}
		
		// count the frequencies
		public void count(String str) {
			if (!stopwordsfilter.checkStopWords(str) && str.contains("z") &&str.length() >=2) {
				counter++;
				this.res.add(str);
			}
		}

		public void print() {
			System.out.println("words containing z" + "  -  " + this.counter);
			System.out.println("words containing z after removing duplicate" + "  -  " + this.res.size());
		}

	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// args[0] is the file path of input FileReader
		WordFrequencyFramework wfapp = new WordFrequencyFramework();
		StopWordManager stopWordFilter = new StopWordManager(wfapp);
		DataManager dataStorage = new DataManager(wfapp, stopWordFilter);
		WordFrequencyManager wordFrequencyCounter = new WordFrequencyManager(wfapp, dataStorage);
		WordsWithZ wordsWithZ = new WordsWithZ(wfapp, dataStorage, stopWordFilter);

		wfapp.run(args[0]);
	}
}

