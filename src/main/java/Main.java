import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Develop a program that gets an XML file with tags <Person> that has attributes NAME and SURNAME.
 * The program must create a copy of this file in which the value of the SURNAME attribute is combined with NAME.
 *
 * @author Semenchenko V.
 */
public class Main {

    public static void main(String[] args) {
        List<String> list;
        List<String> listNames = new ArrayList<>();
        List<String> listSurnames = new ArrayList<>();
        List<String> listFullNames = new ArrayList<>();

        try (BufferedReader buf = Files.newBufferedReader(Paths.get("./src/main/resources/input.xml"));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get("./src/main/resources/output.xml"))) {

            final String nameRegex = "((\\s)(name)((\\s)*)=((\\s)*)(\"[А-яІіЇї]+\"))";
            final String surnameRegex = "((surname)((\\s)*)=((\\s)*)(\"[А-яІіЇї]+\"))";

            //add all strings from file to List
            list = buf.lines().collect(Collectors.toList());

            Pattern patternNames = Pattern.compile(nameRegex, Pattern.MULTILINE);
            Pattern patternSurnames = Pattern.compile(surnameRegex, Pattern.MULTILINE);

            for (String str : list) {
                Matcher matcherNames = patternNames.matcher(str);
                Matcher matcherSurnames = patternSurnames.matcher(str);

                //looking for names
                while (matcherNames.find()) {
                    int firstIndex = matcherNames.group().indexOf('"');
                    int lastIndex = matcherNames.group().length() - 1;
                    listNames.add(matcherNames.group().substring(firstIndex, lastIndex));
                }

                //looking for surnames
                while (matcherSurnames.find()) {
                    int firstIndex = matcherSurnames.group().indexOf('"') + 1;
                    int lastIndex = matcherSurnames.group().length();
                    listSurnames.add(matcherSurnames.group().substring(firstIndex, lastIndex));
                }
            }

            //get full names
            for (int i = 0; i < listNames.size(); i++) {
                listFullNames.add(listNames.get(i) + " " + listSurnames.get(i));
            }


            //delete tag SURNAME
            int index = 0;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, list.get(i).replaceAll(surnameRegex, ""));

                String regex = "(\"[А-яІіЇї]+\")";
                Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(list.get(i));

                while (matcher.find()) {
                    list.set(i, list.get(i).replaceAll(regex, listFullNames.get(index)));
                    index++;
                }

            }

            //write to new file
            for (String str : list) {
                writer.write(str + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
