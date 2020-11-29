Step 1:
Change the folder to week5
```bash
cd week5
```

Step 2:
Use javac to compile
```bash
javac *.java
jar cf app1.jar Words1.class Frequencies1.class
jar cf app2.jar Words2.class Frequencies2.class
jar cfm framework.jar MANIFEST.MF *.class config.properties
```

Step 3:
Run the program
Seventeen:
```bash
java Seventeen ../pride-and-prejudice.txt
```
keyboard input the class name(e.g DataManager)

Twenty:
```bash
java -jar framework.jar ../pride-and-prejudice.txt
```

answers for 17.2 with classname input DataManager:
```txt
DataManager has the fields:
java.lang.String DataManager.data
DataManager has the methods:
public java.lang.String DataManager.getInfo()
public java.lang.String[] DataManager.words()
DataManager has the superclasses:
class TFExercise
class java.lang.Object
DataManager has the implemented interfaces:
None
```

Program 20 uses app1 in config.properties, if you want to use app2, you should change the properties file.