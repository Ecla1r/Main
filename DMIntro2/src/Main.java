import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main (String [] args) throws IOException {

        File file = new File("transactions.csv");
        List<String> prodList = new BufferedReader(new FileReader(file))
                .lines()
                .skip(1)
                .map(s -> s.split(";")[0])
                .collect(Collectors.toList());

        Map<String, Integer> freqs = new HashMap<>();
        for (String x : prodList) {
            if (!freqs.containsKey(x)) {
                freqs.put(x, 1);
            }
            else freqs.put(x, freqs.get(x) + 1);
        }
        Map<String, Integer> sortedMap =
                freqs.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        File outfile = new File("out.csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
        for (Map.Entry<String, Integer> x : sortedMap.entrySet()) {
            try {
                bw.write(x.getKey() + ";" + x.getValue() + "\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
