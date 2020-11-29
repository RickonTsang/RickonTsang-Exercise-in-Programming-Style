import java.util.*;
import java.io.*;
import java.lang.reflect.*;

// base on classes in Exercise 12
public class Seventeen {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// args[0] is the file path of input FileReader
		WordFrequencyController wordFrequencyController = new WordFrequencyController(args[0]);
		wordFrequencyController.getClass().getMethod("run").invoke(wordFrequencyController);

		Scanner sc = new Scanner(System.in);
		System.out.println("input the class name:");
		String className = sc.next();
		Set<String> set = new HashSet<>();
		set.add("DataManager");
		set.add("StopWordManager");
		set.add("WordFrequencyManager");
		set.add("WordFrequencyController");
		if (set.contains(className)) {
			print(className);
		} else {
			System.out.println("class name invalid");
		}
	}

	public static void print(String className) throws Exception {
        Class c = Class.forName(className);

        // print fields
        System.out.println(className + " has the fields:");
        Field fields[] = c.getDeclaredFields();
        if (fields.length != 0){
			for(Field field : fields){
				System.out.println(field);
			}
        } else {
			System.out.println("None");
        }

        // print methods
        System.out.println(className + " has the methods:");
        Method[] methods = c.getDeclaredMethods();
        if (methods.length != 0){
			for (Method method: methods){
            	System.out.println(method);
          	}
        } else{
          	System.out.println("None");
        }

		// print superclass
		System.out.println(className + " has the superclasses:");
        Class superClass = c.getSuperclass();
        if (superClass == null){
          	System.out.println("None");
        } else{
			while (superClass != null){
				System.out.println(superClass);
				superClass = superClass.getSuperclass();
			}
        }

        // print interfaces
        System.out.println(className + " has the implemented interfaces:");
        Class[] interfaces = c.getInterfaces();
        if (interfaces.length != 0){
			for (Class interFace: interfaces){
				System.out.println(interFace);
			}
        } else {
		  	System.out.println("None");
        }

        
	}
}

// base class
class TFExercise {
	public String getInfo() {
		return this.getClass().getName();
	}
}

class DataManager extends TFExercise{
	// data saves the inputfile as string
	String data;

	// init
	public DataManager(String path) {
		this.data = "";
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
	}

	// return a list of words
	public String[] words() {
		return this.data.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
	}

	@Override
	public String getInfo() {
		return super.getInfo() + ": My major data structure is a" + this.data.getClass().getName();
	}


}

class StopWordManager extends TFExercise {
	// stopWords save the list of stop words
	List<String> stopWords;

	// init
	public StopWordManager() {
		this.stopWords = new ArrayList<>();
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

	@Override
	public String getInfo() {
		return super.getInfo() + ": My major data structure is a" + this.stopWords.getClass().getName();
	}

}


class WordFrequencyManager extends TFExercise {
	Map<String, Integer> wordFreqs;
	
	public WordFrequencyManager() {
		this.wordFreqs = new HashMap<>();
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

	@Override
	public String getInfo() {
		return super.getInfo() + ": My major data structure is a" + this.wordFreqs.getClass().getName();
	}
}

class WordFrequencyController {
	DataManager dataManager;
	StopWordManager stopWordManager;
	WordFrequencyManager wordFrequencyManager;

	// init
	public WordFrequencyController(String path) {
		this.dataManager = new DataManager(path);
		this.stopWordManager = new StopWordManager();
		this.wordFrequencyManager = new WordFrequencyManager();
	}

	@SuppressWarnings("unchecked")
	public void run() throws Exception {

		for (String word : (String[])this.dataManager.getClass().getDeclaredMethod("words").invoke(this.dataManager)) {
			if (!(boolean)this.stopWordManager.getClass().getMethod("checkStopWords", "str".getClass()).invoke(this.stopWordManager, word) && word.length() >= 2) {
				this.wordFrequencyManager.getClass().getMethod("count", "str".getClass()).invoke(this.wordFrequencyManager, word);
			}
		}
		List<Map.Entry<String, Integer>> list = (List<Map.Entry<String, Integer>>)this.wordFrequencyManager.getClass().getMethod("sorted").invoke(this.wordFrequencyManager);
		for (int i = 0; i < 25; i++) {
			System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
		}
	}
}
