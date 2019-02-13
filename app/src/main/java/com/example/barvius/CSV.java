package com.example.barvius;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSV {
    public static List<DBItems> parseCSV(File file){
        List<DBItems> list = new ArrayList<>();
        BufferedReader br = null;
        String line;
        String[] CSVdata;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                CSVdata = line.split(",");
                list.add(new DBItems(CSVdata[1].replaceAll("\"", ""),CSVdata[2].replaceAll("\"", "")));
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        } finally {
        }
        return list;
    }
}
