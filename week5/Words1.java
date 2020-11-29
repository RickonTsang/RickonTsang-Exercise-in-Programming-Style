import java.util.*;
import java.io.*;
import java.util.function.*;

class Words1 implements Function<String, String[]>{

	// using set to remove duplicate
	@Override
	public String[] apply(String path) {
		// filePath is the file path of inputfile

		// scan the stop_words.txt and save the word in the set
        Set<String> set = new HashSet<>();
        try {
            BufferedReader buffReader = new BufferedReader(new FileReader("../stop_words.txt"));
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

        StringBuilder sb = null;
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(path));
			sb = new StringBuilder();
			String tmp = "";
			while((tmp = buffReader.readLine()) != null){
				String[] strArray = tmp.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");

                for(String str : strArray) {
                  if (!set.contains(str) && str.length() >= 2) {
                    sb.append(str + " ");
                  }
                }
			}
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().split(" ");
	}
}