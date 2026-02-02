package ethernetproject;

import java.io.*;
import java.util.*;

public class ConfigParser {

    public static Config parse(String filename) throws Exception {
        Config cfg = new Config();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        boolean readingDevices = false;
        boolean readingLinks = false;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // section headers MUST be handled before skipping comments
            if (line.equalsIgnoreCase("# devices")) {
                readingDevices = true;
                readingLinks = false;
                continue;
            }

            if (line.equalsIgnoreCase("# links")) {
                readingDevices = false;
                readingLinks = true;
                continue;
            }

            // other comments
            if (line.startsWith("#")) continue;

            if (readingDevices) {
                String[] parts = line.split("\\s+");
                cfg.devices.put(
                        parts[0],
                        new DeviceInfo(parts[0], parts[1], Integer.parseInt(parts[2]))
                );
            }

            if (readingLinks) {
                String[] parts = line.split("\\s+");
                cfg.links
                        .computeIfAbsent(parts[0], k -> new ArrayList<>())
                        .add(parts[1]);
                cfg.links
                        .computeIfAbsent(parts[1], k -> new ArrayList<>())
                        .add(parts[0]);
            }
        }

        br.close();
        return cfg;
    }
}
