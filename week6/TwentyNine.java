import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;

// base on classes in Exercise 12
public class TwentyNine {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// args[0] is the file path of input FileReader
		WordFrequencyManager wordFrequencyManager = new WordFrequencyManager();

		StopWordManager stopWordManager = new StopWordManager(wordFrequencyManager);
		Sender.send(stopWordManager, new Message("init", ""));

		DataManager dataManager = new DataManager(stopWordManager);
		Sender.send(dataManager, new Message("init", args[0]));

		WordFrequencyController wordFrequencyController = new WordFrequencyController();
		Sender.send(wordFrequencyController, new Message("init", dataManager));

		for (Thread t : new Thread[]{wordFrequencyManager, stopWordManager, dataManager, wordFrequencyController}) {
			try {
				t.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

// base class Message
class Message {
	String command;
	Object value;

	public Message(String command, Object value) {
		this.command = command;
		this.value = value;
	}
}

// base class Sender
class Sender {
	public static void send(ActiveWFObject receiver, Message message) {
		receiver.queue.offer(message);
	}
}

// base class ActiveWFObject
class ActiveWFObject extends Thread {
	String name;
	Queue<Message> queue;
	boolean stopMe;

	public ActiveWFObject() {
		this.queue = new LinkedBlockingQueue<>();
		this.stopMe = false;
		start();
	}
	
	@Override
	public void run() {
		while (!this.stopMe) {
			Message message;
			if (queue.isEmpty()) {
				try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
			} else {
				message = queue.poll();
				dispatch(message);
				if (message.command.equals("die")) {
					this.stopMe = true;
				}
			}
		}
	}

	public void dispatch(Message message) {
		return;
	}

}


// class DataStorageManager
class DataManager extends ActiveWFObject {
	// data saves the inputfile as string
	String data;
	StopWordManager stopWordManager;

	public DataManager() {
		this.data = "";
	}

	public DataManager(StopWordManager stopWordManager) {
		this.data = "";
		this.stopWordManager = stopWordManager;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void dispatch(Message message) {
		String command = message.command;
		if ("init".equals(command)) {
			this.init((String)message.value);
		} else if ("send_word_freqs".equals(command)) {
			this.processWords((ActiveWFObject)message.value);
		} else {
			Sender.send(this.stopWordManager, message);
		}
		
	}

	// use buffered reader to read the input file(pride-and-prejudice) 
	public void init(String path) {
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
	public void processWords(ActiveWFObject recipient) {
		String[] words = this.data.replaceAll("\\P{Alnum}", " ").toLowerCase().split(" ");
		for (String word: words) {
			Sender.send(stopWordManager, new Message("filter", word));
		}
		Sender.send(this.stopWordManager, new Message("top25", recipient));
	}

}

class StopWordManager extends ActiveWFObject {
	// stopWords save the list of stop words
	List<String> stopWords;
	WordFrequencyManager wordFrequencyManager;

	public StopWordManager() {
		this.stopWords = new ArrayList<>();
	}

	public StopWordManager(WordFrequencyManager wordFrequencyManager) {
		this.stopWords = new ArrayList<>();
		this.wordFrequencyManager = wordFrequencyManager;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void dispatch(Message message) {
		String command = message.command;
		if ("init".equals(command)) {
			this.init();
		} else if ("filter".equals(command)) {
			this.checkStopWords((String)message.value);
		} else {
			Sender.send(this.wordFrequencyManager, message);
		}
		
	}

	// use buffered reader to read the stop words(stop_words.txt) 
	public void init() {
		String path = "../stop_words.txt";
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
	}

	// return whether is stop words
	public void checkStopWords(String word) {
		if (!this.stopWords.contains(word) && word.length() >= 2) {
			Sender.send(this.wordFrequencyManager, new Message("word", word));
		}
	}

}


class WordFrequencyManager extends ActiveWFObject {
	Map<String, Integer> wordFreqs;
	
	public WordFrequencyManager() {
		this.wordFreqs = new HashMap<>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void dispatch(Message message) {
		String command = message.command;
		if ("word".equals(message.command)) {
			this.count((String)message.value);
		} else if ("top25".equals(message.command)) {
			this.sorted((ActiveWFObject)message.value);
		} else {
		}
		
	}

	// count the frequencies
	public void count(String str) {
		this.wordFreqs.put(str, this.wordFreqs.getOrDefault(str, 0) + 1);
	}

	// return sorted list of entry
	public void sorted(ActiveWFObject recipient) {
		List<Map.Entry<String, Integer>> list = new ArrayList<>(this.wordFreqs.entrySet());
		Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
		Sender.send(recipient, new Message("top25", list));
	}

}

class WordFrequencyController extends ActiveWFObject {
	DataManager dataManager;

	public WordFrequencyController() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void dispatch(Message message) {
		String command = message.command;
		if ("init".equals(command)) {
			this.init((DataManager)message.value);
		} else if ("top25".equals(command)) {
			this.display((List<Map.Entry<String, Integer>>)message.value);
		} else {
			throw new IllegalArgumentException("Message not understaood");
		}
	}

	public void init(DataManager dataManager) {
		this.dataManager = dataManager;
		Sender.send(this.dataManager, new Message("send_word_freqs", this));
	}

	public void display(List<Map.Entry<String, Integer>> list) {
		for (int i = 0; i < 25; i++) {
			System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
		}
		Sender.send(this.dataManager, new Message("die", ""));
		this.stopMe = true;
	}
}