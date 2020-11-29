import java.util.*;
import java.io.*;
import java.util.function.BiConsumer;

// override the accept method of each class extends BiConsumer

class Nine {

	// read file and return a string "data"
	public static class readFile implements BiConsumer<String, BiConsumer> {
		@Override
		public void accept(String path, BiConsumer function) {
			StringBuilder sb = null;
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(path));
				sb = new StringBuilder();
				String tmp = "";
				while((tmp = buffReader.readLine()) != null){
					sb.append(tmp + " ");
				}
				buffReader.close();
				// function is filterCharsAndNormalize
				function.accept(sb.toString(), new removeStopWords());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// replace all nonalphanumeric chars in data with white space and normalize to lowercase
	public static class filterCharsAndNormalize implements BiConsumer<String, BiConsumer> {
		@Override
  		public void accept (String data, BiConsumer function){
			data = data.replaceAll("\\P{Alnum}", " ").toLowerCase();
			// function is removeStopWords
			function.accept(data, new frequencies());
		}
		
	}

	// remove stop_words and return a list
	public static class removeStopWords implements BiConsumer<String, BiConsumer>{
		@Override
  		public void accept(String data, BiConsumer function){
			// scan stop_words
			ArrayList<String> stopWords = new ArrayList<>();
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader("../stop_words.txt"));
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
			// function is frequencies
			function.accept(words, new sortAndPrint());
		}
		
	}

	// count frequency of each word and return a hashmap "wordFreqs"
	public static class frequencies implements BiConsumer<List<String>, BiConsumer> {
		@Override
  		public void accept(List<String> words, BiConsumer function){
			Map<String, Integer> wordFreqs = new HashMap<>();
			for (String str : words) {
				wordFreqs.put(str, wordFreqs.getOrDefault(str, 0) + 1);
			}
			// function is sortAndPrint
			function.accept(wordFreqs, new noOps());
		}
		
	}

	// sort word frequencies and print top 25 frequent words
	public static class sortAndPrint implements BiConsumer<Map<String, Integer>, BiConsumer>{
		@Override
  		public void accept(Map<String, Integer> wordFreqs, BiConsumer function){
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
			// function is none
			function.accept(null, null);
		}
		
	}

	// no ops function
    public static class noOps implements BiConsumer<Integer, Integer>{
		@Override
		public void accept(Integer a, Integer b){

		}
	}


    
	public static void main(String[] args) {
		// args[0] is the file path of input file
		new readFile().accept(args[0], new filterCharsAndNormalize());
	}
}