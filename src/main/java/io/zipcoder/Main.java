package io.zipcoder;

import org.apache.commons.io.IOUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;


public class Main {

    public String readRawDataToString() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        return result;
    }

    public static void main(String[] args) throws Exception{
        String output = (new Main()).readRawDataToString();
        ItemParser parser = new ItemParser();

        System.out.println(parser.displayGroceryListToString());


//        for (Map.Entry<String, ArrayList<Item>> mapKey : parser.getGroceryList().entrySet()) {
//            System.out.println(mapKey.getKey());
//            for (Item item : mapKey.getValue()) {
//                System.out.println(item.getPrice());

            }
        }

//        System.out.println(output);
//        // TODO: parse the data in output into items, and display to console.
//        ItemParser itemParser = new ItemParser();
//        ArrayList<String> arrayListWithoutHashTags = itemParser.parseRawDataIntoStringArray(output);
//        for (String s : arrayListWithoutHashTags)
//        {
//            //System.out.println(s);
//            ItemParser itemParser2 = new ItemParser();
//            //System.out.println(itemParser2.findKeyValuePairsInRawItemData(s));
//            ArrayList<String> arrayWithoutVariousCharacters = itemParser2.splitStringsAtSemiColon(s);
//                for (String s1 : arrayWithoutVariousCharacters){
//                    System.out.println(s1);
//                }

