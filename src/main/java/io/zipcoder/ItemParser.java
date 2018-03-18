package io.zipcoder;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {

    public static int exceptionCount = 0;
    private HashMap<String, ArrayList<Item>> groceryList = new HashMap<String, ArrayList<Item>>();


    public ArrayList<String> parseRawDataIntoStringArray(String rawData){ // entries split by ## with name,price,type and exp Example: [naMe:Milk;price:3.23;type:Food;expiration:1/25/2016, naME:BreaD;price:1.23;type:Food;expiration:1/02/2016 ... etc.]
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawData);
        return response;
    }
    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem){ // entries split by crazy characters Example: [naMe:Milk, price:3.23, type:Food, expiration:1/25/2016] [] [] etc.
        String stringPattern = "[;|^]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawItem);
        return response;
    }
//    public ArrayList<String> splitStringsAtSemiColon(String rawItem){ // entries split by : Example:
//        String stringPattern = ":";
//        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawItem);
//        return response;
//    }

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString){ // helper method that splits strings at characters above into ArrayLists
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }

    public Item parseStringIntoItem(String rawItem) throws ItemParseException{
        if (findName(rawItem) == null || findPrice(rawItem)== null) {
            throw new ItemParseException();
        }

        String name = findName(rawItem);
        Double price = Double.parseDouble(findPrice(rawItem));
        String type = findType(rawItem);
        String expDate = findExpiration(rawItem);

        Item foundAnItem = new Item(name, price, type, expDate);
        return foundAnItem;
    }

    public String findName(String rawItem) throws ItemParseException {
        Pattern patternName = Pattern.compile("(?<=([Nn][Aa][Mm][Ee][^A-Za-z])).*?(?=[^A-Za-z0])"); // matches "nAmE: MiLk"
        Matcher matcherName = patternName.matcher(rawItem);
        if (matcherName.find()){
            if(!matcherName.group().equals("")){ // if the name does not equal null --> some blank spaces were being counted as names
                String fixedName = matcherName.group().replaceAll("\\d", "o"); // will fix C00kie mistake - once you hit a number replace it with an o
                return fixedName.toLowerCase(); // changed to lowercase bc tests requires lowercase
            }
        }
        return null;
    }

    //group() --> Matcher method
    //Returns the input subsequent matched by the previous match.

    public String findPrice(String rawItem) throws ItemParseException {
        Pattern patternPrice = Pattern.compile("\\d(\\.)\\d\\d"); // matches "3.23"
        Matcher matcherPrice = patternPrice.matcher(rawItem);
        if(matcherPrice.find()){
            if (!matcherPrice.group().equals("")){ // some blank prices were getting returned so make sure they're not blank
                return matcherPrice.group();
            }
        }
        return null;
    }

    public String  findExpiration(String rawItem) throws ItemParseException{
        Pattern patternExp = Pattern.compile("(?<=([Ee][Xx][Pp][Ii][Rr][Aa][Tt][Ii][Oo][Nn][^A-Za-z]))(.)+[^#]"); // matches "eXpirAtioN: 1/17/2016
        Matcher matcherExp = patternExp.matcher(rawItem);
        if (matcherExp.find()){
            return matcherExp.group();
        }
        else return null;
    }

    public String findType(String rawItem){
        Pattern patternType = Pattern.compile("(?<=([Tt][Yy][Pp][Ee][^A-Za-z])).*?(?=[^A-Za-z0])"); // matches "type: Food"
        Matcher matcherType = patternType.matcher(rawItem);
        if (matcherType.find()){
            return matcherType.group().toLowerCase();
        }
        else return null;
    }

    public HashMap<String, ArrayList<Item>> getGroceryList() throws Exception {
        Main main = new Main();

        ArrayList<String> listOfItems = parseRawDataIntoStringArray(main.readRawDataToString());

        for(String anItem : listOfItems){
            try {
                Item newItem = parseStringIntoItem(anItem);
                if (!groceryList.containsKey(newItem.getName())){ // if we do not have this key
                    ArrayList<Item> myItemArrayList = new ArrayList<Item>(); // we will have to create a new arrayList to hold our items
                    myItemArrayList.add(newItem); // then we can add our item to our arrayList
                    groceryList.put(newItem.getName(), myItemArrayList); // we will add it to our map next
                } else {
                    groceryList.get(newItem.getName()).add(newItem);
                }
            } catch (ItemParseException e){
                exceptionCount++;
            }
        }
        return groceryList;
    }
}
