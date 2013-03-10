package shared.reader;
import shared.Instance;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ArffParser {

    private static String DATA_TAG = "@data";
    private static String ATTRIBUTE_TAG = "@attribute";

    private static int SPLIT_LIMIT = 3;

    public static Instance[] parse(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        List<Map<String, Double>> attributes = processAttributes(in);
        Instance[] instances = processInstances(in, attributes);

        // don't forget to close the buffer
        in.close();
        return instances;
    }

    private static List<Map<String, Double>> processAttributes(BufferedReader in)
            throws IOException {
        String line = in.readLine();
        List<Map<String, Double>> attributes
                = new ArrayList<Map<String, Double>>();
        while (line != null && line.indexOf(DATA_TAG) == -1) {
            if (line.charAt(0) != '%') {
                String[] parts = line.split(" ", SPLIT_LIMIT);
                if (parts[0].equals(ATTRIBUTE_TAG)) {
                    // attribute values
                    String[] values = parts[2].replaceAll(" |\\{|\\}|'","").split(",");
                    double id = 0.0;
                    Map<String, Double> valMap = new HashMap<String, Double>();
                    for (String s : values) {
                        valMap.put(s, id++);
                    }
                    attributes.add(valMap);
                }
            }
            line = in.readLine();
        }

        return attributes;
    }

    private static Instance[] processInstances(BufferedReader in,
                                               List<Map<String, Double>> valueMaps) throws IOException {
        List<Instance> instances = new ArrayList<Instance>();
        String line = in.readLine();
        while (line != null) {
            if (line.charAt(0) != '%') {
                String[] values = line.replaceAll("'", "").split(",");
                double[] ins = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    String v = values[i];
                    // defaulting to 0 if attribute value unknown.
                    double d = 0;
                    if (valueMaps.get(i).containsKey(v)) {
                        d = valueMaps.get(i).get(v);
                    }
                    ins[i] = d;
                }
                Instance i = new Instance(Arrays.copyOfRange(ins, 0, ins.length - 1));
                i.setLabel(new Instance(ins[ins.length - 1]));
                instances.add(i);
            }
            line = in.readLine();
        }
        return instances.toArray(new Instance[instances.size()]);
    }
}