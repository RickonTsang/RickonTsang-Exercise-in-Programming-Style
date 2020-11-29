import java.util.*;
import java.io.*;

class Twelve {
	public static class DataManager {
		// data saves the inputfile as string
		String data;

		public DataManager() {
			this.data = "";
		}

		public Object dispatch(String[] message) {
			if ("init".equals(message[0])) {
				return this.init(message[1]);
			} else if ("words".equals(message[0])) {
				return this.words();
			} else {
				throw new IllegalArgumentException("Message not understaood" + message[0]);
			}
		}

		// use buffered reader to read the input file(pride-and-prejudice) 
		public String init(String path) {
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
			this.data = sb.toString();
			return this.data;
		}

		// return a list of words
		public String[] words() {
			return this.data.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
		}

	}

	public static class StopWordManager {
		// stopWords save the list of stop words
		List<String> stopWords;

		public StopWordManager() {
			stopWords = new ArrayList<>();
		}

		public Object dispatch(String[] message) {
			if ("init".equals(message[0])) {
				return this.init(message[1]);
			} else if ("checkStopWords".equals(message[0])) {
				return this.checkStopWords(message[1]);
			} else {
				throw new IllegalArgumentException("Message not understaood" + message[0]);
			}
		}

		// use buffered reader to read the stop words(stop_words.txt) 
		public List<String> init(String path) {
			StringBuilder sb = null;
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(path));
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
			return this.stopWords;
		}

		// return whether is stop words
		public boolean checkStopWords(String word) {
			return this.stopWords.contains(word);
		}

	}


	public static class WordFrequencyManager {
		Map<String, Integer> wordFreqs;
		
		public WordFrequencyManager() {
			this.wordFreqs = new HashMap<>();
		}

		public Object dispatch(String[] message) {
			if ("count".equals(message[0])) {
				return this.count(message[1]);
			} else if ("sorted".equals(message[0])) {
				return this.sorted();
			} else {
				throw new IllegalArgumentException("Message not understaood" + message[0]);
			}
		}

		// count the frequencies
		public Map<String, Integer> count(String str) {
			this.wordFreqs.put(str, this.wordFreqs.getOrDefault(str, 0) + 1);
			return this.wordFreqs;
		}

		// return sorted list of entry
		public List<Map.Entry<String, Integer>> sorted() {
			List<Map.Entry<String, Integer>> list = new ArrayList<>(this.wordFreqs.entrySet());
			Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
			return list;
		}

	}

	public static class WordFrequencyController {
		DataManager dataManager;
		StopWordManager stopWordManager;
		WordFrequencyManager wordFrequencyManager;

		public WordFrequencyController() {
		}

		public void dispatch(String[] message) {
			if ("init".equals(message[0])) {
				this.init(message[1]);
			} else if ("run".equals(message[0])) {
				this.run();
			} else {
				throw new IllegalArgumentException("Message not understaood" + message[0]);
			}
		}

		public void init(String path) {
			this.dataManager = new DataManager();
			this.stopWordManager = new StopWordManager();
			this.wordFrequencyManager = new WordFrequencyManager();
			this.dataManager.dispatch(new String[]{"init", path});
			this.stopWordManager.dispatch(new String[]{"init", "../stop_words.txt"});
		}

		@SuppressWarnings("unchecked")
		public void run() {
			for (String word : (String[])this.dataManager.dispatch(new String[]{"words"})) {
				if (!(boolean)this.stopWordManager.dispatch(new String[]{"checkStopWords", word}) && word.length() >= 2) {
					this.wordFrequencyManager.dispatch(new String[]{"count", word});
				}
			}
			List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>)this.wordFrequencyManager.dispatch(new String[]{"sorted"});
			for (int i = 0; i < 25; i++) {
				System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
			}
		}
	}

	
	public static void main(String[] args) {
		// args[0] is the file path of input file
		WordFrequencyController wordFrequencyController = new WordFrequencyController();
		wordFrequencyController.dispatch(new String[]{"init", args[0]});
		wordFrequencyController.dispatch(new String[]{"run"});
	}
}

