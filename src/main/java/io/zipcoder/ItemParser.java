package io.zipcoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString){ // helper method that splits strings at characters above into ArrayLists
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }

    public Item parseStringIntoItem(String rawItem) throws ItemParseException{
        if (findName(rawItem) == null | findPrice(rawItem)== null) {
            throw new ItemParseException();
        }

        String name = findName(rawItem);
        Double price = Double.parseDouble(findPrice(rawItem));
        String type = findType(rawItem);
        String expDate = findExpiration(rawItem);

        Item foundAnItem = new Item(name, price, type, expDate);
        return foundAnItem;
    }

    public String findName(String rawItem) {
        Pattern patternName = Pattern.compile("(?<=([Nn][Aa][Mm][Ee][^A-Za-z])).*?(?=[^A-Za-z0])"); // matches "MiLk" or "bReaD"
        Matcher matcherName = patternName.matcher(rawItem);
        if (matcherName.find()){
            if(!matcherName.group().equals("")){ // if the name does not equal null --> bc some blank spaces and semicolons were being counted as names
                String fixedName = matcherName.group().replaceAll("\\d", "o"); // will fix C00kie mistake - once you hit a number replace it with an o
                return fixedName.toLowerCase(); // changed to lowercase bc tests require lowercase
            }
        }
        return null;
    }

    //group() --> Matcher method
    //Returns the input subsequent matched by the previous match.

    public String findPrice(String rawItem) {
        Pattern patternPrice = Pattern.compile("\\d(\\.)\\d\\d"); // matches "3.23"
        Matcher matcherPrice = patternPrice.matcher(rawItem);
        if(matcherPrice.find()){
            if (!matcherPrice.group().equals("")){ // some blank prices were getting returned so make sure they're not blank
                return matcherPrice.group();
            }
        }
        return null;
    }

    public String  findExpiration(String rawItem) {
        Pattern patternExp = Pattern.compile("(?<=([Ee][Xx][Pp][Ii][Rr][Aa][Tt][Ii][Oo][Nn][^A-Za-z]))(.)+[^#]"); // matches "1/17/2016"
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
                    groceryList.get(newItem.getName()).add(newItem); // if we already have the item, add the value
                }
            } catch (ItemParseException e){
                exceptionCount++;
            }
        }
        return groceryList;
    }

    public String displayGroceryListToString() throws Exception{
        groceryList = getGroceryList();
        StringBuilder displayGroceryList = new StringBuilder();

        for (Map.Entry<String, ArrayList<Item>> nameAndItem : groceryList.entrySet()){
            String makeUpperCase = nameAndItem.getKey().substring(0,1).toUpperCase() + nameAndItem.getKey().substring(1); // capitalize the first letter of the name ex: M and then add the rest of the work ex: ilk

            displayGroceryList.append("\n" + "name: " + makeUpperCase + "\t\t\t\t" + "seen: " + nameAndItem.getValue().size() + " times");
            displayGroceryList.append("\n" + "------------------------------------------");

            ArrayList<Double> getDiffPrices = getDifferentPrices(nameAndItem);
            for (int i = 0; i < getDiffPrices.size(); i++) {
                if (getPriceOccurrences(nameAndItem.getValue(), getDiffPrices.get(i)) == 1) {
                    String time = " time";
                } else {
                    String time = " times";
                    displayGroceryList.append("\n" + "Price: " + getDiffPrices.get(i) + "\t\t\t\t" + " seen: " + getPriceOccurrences(nameAndItem.getValue(), getDiffPrices.get(i)) + " "+time);
                    displayGroceryList.append("\n" + "==========================================");
                }
            }

        }
        displayGroceryList.append("\n\n" + "Errors: " + exceptionCount + " times\n\n");
        displayGroceryList.append("\n" + "------------------------------------------");
        return displayGroceryList.toString();

    }

    public Integer getPriceOccurrences(ArrayList<Item> listOfItems, Double price){
        int counter = 0;

        for (int i = 0; i < listOfItems.size(); i++){
           if (listOfItems.get(i).getPrice().equals(price)) // if our arrayList of items have the value of our price add it to the count
               counter++;
        }
        return counter;
    }

    public ArrayList<Double> getDifferentPrices(Map.Entry<String, ArrayList<Item>> item){
        ArrayList<Double> diffPrices = new ArrayList<Double>();

        for (int i = 0; i < item.getValue().size(); i ++){
            if (!diffPrices.contains(item.getValue().get(i).getPrice())){ // get the size of our arrayList of items and if the prices != the other prices add them to our list
                diffPrices.add(item.getValue().get(i).getPrice());
            }
        }
        return diffPrices;
    }
}
