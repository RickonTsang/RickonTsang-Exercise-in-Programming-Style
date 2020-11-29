import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.*;
import java.util.function.*;

class Twenty {
	// global
	static Function<String, String[]> wordsFunction;
    static Function<String[], List<Map.Entry<String, Integer>>> frequenciesFunction;
	@SuppressWarnings("unchecked")
	public static void loadPlugins() throws Exception {
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = Twenty.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String wordsName = prop.getProperty("words");
        String frequenciesName = prop.getProperty("frequencies");
        String path = prop.getProperty("path");

        Class cls = null;
        URL classUrl = null;
        try {
          	classUrl = new URL(path);
        } catch (Exception e) {
          	e.printStackTrace();
        }
        URL[] classUrls = {classUrl};
        URLClassLoader cloader = new URLClassLoader(classUrls);

		// wordsFunction = (Function<String, String[]>)cloader.loadClass(wordsName).newInstance();
		// frequenciesFunction = (Function<String[], List<Map.Entry<String, Integer>>>) cloader.loadClass(frequenciesName).newInstance();
		wordsFunction = (Function<String, String[]>)cloader.loadClass(wordsName).getDeclaredConstructor().newInstance();
		frequenciesFunction = (Function<String[], List<Map.Entry<String, Integer>>>) cloader.loadClass(frequenciesName).getDeclaredConstructor().newInstance();
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
		loadPlugins();

        String[] words = wordsFunction.apply(args[0]);
		List<Map.Entry<String, Integer>> list = frequenciesFunction.apply(words);
        for (int i = 0; i < 25; i++) {
			System.out.println(list.get(i).getKey() + "  -  " + list.get(i).getValue());
		}
    }
}