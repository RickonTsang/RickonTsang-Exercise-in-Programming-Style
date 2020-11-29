import java.util.*;
import java.io.*;
import java.util.function.*;

// collection sort
class Frequencies1 implements Function<String[], List<Map.Entry<String, Integer>>>{
	@Override
	public List<Map.Entry<String, Integer>> apply(String[] words) {
		Map<String, Integer> wordFreqs = new HashMap<>();
		for (String word: words) {
			wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
		}
        // sort
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFreqs.entrySet());
		Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		List<Map.Entry<String, Integer>> resList = new ArrayList<>();
		for (int i = 0; i < 25; i++) {
			resList.add(list.get(i));
		}
		return resList;
	}
}