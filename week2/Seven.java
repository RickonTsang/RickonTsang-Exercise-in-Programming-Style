import java.util.*;
import java.io.*;

class Seven {
	public static void main(String[] args) {
		// args[0] is the file path of inputfile

		// scan the stop_words.txt and save the word in the set
        Set<String> set = new HashSet<>();
        try {
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream("../stop_words.txt")));
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
                for (String str : tmp.split(",")) {
                    set.add(str);
                }
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // scan and count words in pride-and-prejudice.txt
        String filePath = args[0];
        Map<String, Integer> map = new HashMap<>();
        try {
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String tmp = "";
            while((tmp = buffReader.readLine()) != null){
                String[] arr = tmp.split("[\\p{Punct} ]");
                for (String str : arr) {
                    str = str.trim().toLowerCase();
                    if (!set.contains(str) && str.length() > 1) {
                        map.put(str, map.getOrDefault(str, 0) + 1);
                    }
                }
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // sort
        List<String> candidates = new ArrayList<>(map.keySet());
        Collections.sort(candidates, (o1, o2) -> map.get(o2) - map.get(o1));
        for (int i = 0; i < 25; i++) {
            System.out.println(candidates.get(i) + "  -  " + map.get(candidates.get(i)));
        }
	}
}