import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static SortedMap<Integer, Integer> pairs = new TreeMap<>();
    static ArrayList<String> firstInput = new ArrayList<>();
    static ArrayList<String> secondInput = new ArrayList<>();
    static File input = new File("src\\main\\resources\\input.txt");
    static File output = new File("src\\main\\resources\\output.txt");

    public static void main(String[] args) throws FileNotFoundException {

        getInput(firstInput, secondInput);
        getPairs(getTable(firstInput, secondInput));
        executeSingles();
        outputToFile(output);

    }

    public static void getInput(ArrayList<String> firstInput, ArrayList<String> secondInput) throws FileNotFoundException {
        Scanner scanner = new Scanner(input); //в примере все строки разделены пробелами, возможно это дефект форматирования,
        int lineNumber = Integer.parseInt(scanner.nextLine()); // но я решил считать, что такой формат инпута правильный.
        for (int i =0; i < lineNumber; i++) {
            scanner.nextLine();         //поэтому я пропускаю строки после каждого считанного элемента
            String line = scanner.nextLine();
            firstInput.add(line);
        }
        scanner.nextLine();
        lineNumber = Integer.parseInt(scanner.nextLine());
        for (int i =0; i < lineNumber; i++) {
            scanner.nextLine();
            String line = scanner.nextLine();
            secondInput.add(line);
        }

    }

    public static int[][] getTable(ArrayList<String> firstInput, ArrayList<String> secondInput) {
        //здесь я создаю таблицу, в каждой ячейке - длина максимальной найденной подстроки
        int[][] subStringTable = new int[firstInput.size()][secondInput.size()];
        for (int i =0; i < firstInput.size(); i++) {
            for (int j = 0; j < secondInput.size(); j++) {
                subStringTable[i][j] = maxSubStr(firstInput.get(i), secondInput.get(j),
                        firstInput.get(i).length(), secondInput.get(j).length(), 0);
            }
        }
        return subStringTable;
    }

    public static void getPairs(int[][] subStringTable) {
        //этот метод разбивает два списка по парам, начиная с самых похожих, заканчивая самыми непохожими
        int cycles = Math.min(subStringTable.length, subStringTable[0].length);
        int firstIndex = -1, secondIndex = -1; //эти переменные инициированы отрицательными величинами, чтобы в первый раз
        while (cycles > 0) {
            int theMostMaxSubstring = 0;
            int iTemp = 0, jTemp = 0;
            for (int i = 0; i < subStringTable.length; i++) {
                for (int j = 0; j < subStringTable[0].length; j++) {
                    if (i == firstIndex || j == secondIndex) { // не произошло обнуления вот здесь
                        subStringTable[i][j] = 0;
                    }
                    if (theMostMaxSubstring < subStringTable[i][j]) {
                        theMostMaxSubstring = subStringTable[i][j];
                        iTemp = i;
                        jTemp = j;
                    }
                }
            }
            firstIndex = iTemp;
            secondIndex = jTemp;
            firstInput.set(firstIndex, firstInput.get(firstIndex) + " : " + secondInput.get(secondIndex));
            secondInput.set(secondIndex, "");
            cycles--;
        }
    }

    static int maxSubStr(String first, String second, int firstLength, int secondLength, int count) {
        // Этот метод ищет максимальную совпадающую подстроку у двух строк.
        // Вообще говоря, в задании нечетко указано, что значит "максимально похожие строки".
        // В общем случае, можно было бы использовать дистанцию Левенштейна,
        // но посмотрев примеры, я решил, что здесь важнее вхождения подстрок.
        // Этот метод рабочий, но не оптимальный - при наличии в двух строках двух и более совпадающих подстрок
        // время работы программы значительно увеличивается
        if (firstLength == 0 || secondLength == 0)    
            return count;
        if (first.charAt(firstLength - 1) == second.charAt(secondLength - 1)) {
            count = maxSubStr(first, second,firstLength - 1, secondLength - 1, count + 1);
        }
        count = Math.max(count,
                Math.max(maxSubStr(first, second, firstLength, secondLength - 1, 0),
                        maxSubStr(first, second, firstLength - 1, secondLength, 0)));
        return count;
    }

    static void executeSingles() {
        if (firstInput.size() > secondInput.size()) {
            for(String line : firstInput) {
                if(!line.contains(" : ")) {
                    firstInput.set(firstInput.indexOf(line), line + " : ?");
                }
            }
        }
        if (firstInput.size() < secondInput.size()) {
            for(String line : secondInput) {
                if (!line.isEmpty()) {
                    firstInput.add(line + " : ?");
                }
            }
        }
    }

    static void outputToFile(File output) {
        try {
            Path write = Files.write(output.toPath(), firstInput, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}