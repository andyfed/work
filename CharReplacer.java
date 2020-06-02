/* 
*  "Char replacer" application code
*  readme - "Char replacer - specification"
*/


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.nio.file.StandardOpenOption.*;

public class Main {
    private static long startTime = new Date().getTime();
    private static String basicString;
    private static Map<String, String> rulesMap = new LinkedHashMap<>();
    private static Map<String, Path> filesMap = new HashMap<>();
    private static int totalChangeCounter = 0;


    public static void main(String[] args) {

        // fill Map 'filesMap' by run arguments
        readUsrFiles(args);

        try {
            basicString = Files.readString(filesMap.get("inputFile"));
        } catch (IOException e) {
            System.out.println("Can't read input file: " + e);
        }

        // read rules by strings, convert for Map 'rulesMap'
        try {
            Files.lines(filesMap.get("rulesFile"), StandardCharsets.UTF_8).forEach(Main::setRulesMap);
        } catch (IOException e) {
            System.out.println("Can't read Rules.txt : " + e);
        }

        replaceAll();

        writeResult(filesMap.get("output"), filesMap.get("info"));
    }

    // Start with reading user arguments
    private static void readUsrFiles(String[] args){
        Path inputFile = Paths.get(args[0]);                    // string to change
        Path output = Paths.get(args[1]);                       // string after all changes
        Path rulesFile = Paths.get(args[2]);                    // change rules
        Path info = Paths.get(args[3]);                         // execution info

        filesMap.put("inputFile", inputFile);
        filesMap.put("rulesFile", rulesFile);
        filesMap.put("output", output);
        filesMap.put("info", info);
    }

    // forms right-left rule parts, fill 'Map rulesMap'
    private static void setRulesMap(String rule) {
        String[] ruleParts = rule.split("->");
        rulesMap.put(ruleParts[0].trim(), ruleParts[1].trim());     // no whitespaces!
    }

    // create matchers
    private static Matcher createMatcher(String leftPart, String inputString){
        Pattern p = Pattern.compile(leftPart);
        return p.matcher(inputString);
    }

    // checks the string while at least 1 rule is still suitable, using 'rulesMap' и 'totalChangeCounter'
    private static void replaceAll(){
        while(true) {
            int count = 0;
            count = replacer(rulesMap);                             // do changes, while still can (return 1)
            totalChangeCounter += count;
            if (count == 0) break;                                  // if was no change (return 0), exit cycle
        }

    }


    // main change logic, return change done (1 or 0)
    private static int replacer(Map<String, String> map){
        int replaceCount = 0;

        for (Map.Entry<String, String> entry : map.entrySet()){     // cycle checks for executable rule
            String left = entry.getKey();
            String right = entry.getValue();
            Matcher matcher = createMatcher(left, basicString);

            if (matcher.find()) {                                   // if rule works (1st case)
                basicString = matcher.replaceFirst(right);          // return changed string! write it instead of previous version
                replaceCount++;                                     // counting
                break;                                              // rule works -> break cycle
            }
        }
        return replaceCount;                                        // return 0 - if no one rule wasn't executed
    }

    // writes final version of the string after all changes and technical info
    private static void writeResult(Path outputFile, Path infoFile) {
        try {
            Files.writeString(outputFile, basicString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Smth goes wrong with output file: "  + e);
        }

        String perfResult = "Program work time is " + (calculatePerformance()) + " milliseconds.\n"
                + totalChangeCounter + " change operations were done.\n";
        try {
            Files.writeString(infoFile, perfResult, StandardCharsets.UTF_8, CREATE);
        } catch (IOException e) {
            System.out.println("Smth goes wrong with Info file: "  + e);
        }
    }

    private static long calculatePerformance(){
        long endTime = new Date().getTime();
        return endTime - startTime;
    }
}
