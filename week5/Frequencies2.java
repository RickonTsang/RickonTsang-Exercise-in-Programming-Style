import java.util.*;
import java.io.*;
import java.util.function.*;

// PriorityQueue
class Frequencies2 implements Function<String[], List<Map.Entry<String, Integer>>> {
	@Override
	public List<Map.Entry<String, Integer>> apply(String[] words) {
		Map<String, Integer> wordFreqs = new HashMap<>();
		for (String word: words) {
			wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
		}
        // sort
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFreqs.entrySet());
		PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((o1, o2) -> (o2.getValue() - o1.getValue()));

        for (Map.Entry<String, Integer> entry : wordFreqs.entrySet()) {
            pq.offer(entry);
        }
		
		List<Map.Entry<String, Integer>> resList = new ArrayList<>();
		for (int i = 0; i < 25; i++) {
			resList.add(pq.poll());
		}
		return resList;
	}
}