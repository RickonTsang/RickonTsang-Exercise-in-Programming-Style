import java.util.*;
import java.io.*;

class Main {
	public static void main(String[] args) {
		// args[0] is the file path of stop_words.txt
		// args[1] is the file path of the pride-and-prejudice.txt

		// scan the stop_words.txt and save the word in the set
        String path = args[0];
        Set<String> set = new HashSet<>();
        try {
            FileInputStream fin = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(fin);
            BufferedReader buffReader = new BufferedReader(reader);
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
                String[] arr = tmp.split(",");
                for (String str : arr) {
                    set.add(str);
                }
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // scan and count words in pride-and-prejudice.txt
        String filePath = args[1];
        Map<String, Integer> map = new HashMap<>();
        try {
            FileInputStream fin = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(fin);
            BufferedReader buffReader = new BufferedReader(reader);
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
                String[] arr = tmp.split("[\\p{Punct} ]");
                for (String str : arr) {
                    str = str.toLowerCase();
                    str = str.trim();
                    if (!set.contains(str) && str.length() > 1) {
                        map.put(str, map.getOrDefault(str, 0) + 1);
                    }
                }
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // use heap(PriorityQueue in Java) to get the N most frequent words
        PriorityQueue<String> pq = new PriorityQueue<>((o1, o2) -> map.get(o1) - map.get(o2));
        for (String str : map.keySet()) {
            pq.offer(str);
            if (pq.size() > 25) {
                pq.poll();
            }
        }
        List<String> list = new ArrayList<>();
        while (pq.size() > 0) {
            list.add(0, pq.poll());
        }
        for (String str : list) {
            System.out.println(str + "  -  " + map.get(str));
        }
	}
}