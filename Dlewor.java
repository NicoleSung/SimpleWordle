/*
lab 3
cloning wordle game
Nicole Sung
Feb 24, 2022
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class Dlewor {
    // constants to allow colored text and backgrounds
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void main(String[] args) {
        // print welcome message
        System.out.println("Welcome to Dlewor(TM)");

        // initiate variables
        Scanner scnr = new Scanner(System.in);
        Random randGen = new Random();
        boolean win = false;
        ArrayList<String> compare = new ArrayList<>();

        // read command line argument, handle exception, and create a Scanner object
        FileInputStream file = null;
        try{
            file = new FileInputStream(args[0]);
        }
        catch (FileNotFoundException e){
            System.out.println("Error: " + e);
            System.exit(1);
        }
        Scanner fileReader = new Scanner(file); // read the file

        // set search method
        String SearchMethod = args[0];
        String [] Method = SearchMethod.split("\\.");
        boolean BinarySearch;
        BinarySearch = Method[2].equals("sorted");

        // input 5 character word into dictionary arraylist
        ArrayList<String> dictionary = new ArrayList<>();
        String tempVocab;
        while (fileReader.hasNextLine()) {
            tempVocab = fileReader.nextLine();
            if (tempVocab.length()==5) dictionary.add(tempVocab);
        }

        // set up a target word
        int targetIndex = randGen.nextInt(dictionary.size());
        String target = dictionary.get(targetIndex);

        // start of the game with 6 tries
        String attempt = ""; // user's input
        int index = -1; // index to determine validity of the word
        int [] match; // "match" integer array

        for (int i=1;i<7;i++){
            // Use binary or linear search method to check whether the word exists in the dictionary
            while (index == -1){ // word is not valid
                System.out.printf("Enter word (%d): ", i);
                attempt = scnr.next().toLowerCase();
                if (BinarySearch){
                    index = binarySearch(dictionary,0,dictionary.size(),attempt);
                }
                else{
                    index = linearSearch(dictionary,0,dictionary.size()-1,attempt);
                }
                if (index == -1) System.out.println("Invalid word.");
            }

            // check for special cases (repeat letters)
            // ex. abbey (use algae, orbit, abate, and abbey to test)
            compare.clear();
            compareRepeatLetter(repeatLetter(target),repeatLetter(attempt),compare);

            // create match int array and print corresponding colors
            match = matchDlewor(target,attempt,compare);
            printDlewor(attempt,match);

            // Check answer
            if (foundMatch(match)){
                win = true;
                break;
            }
            else{
                index = -1;
            }
        }

        // end of the game
        if (win) System.out.println("Yesss!");
        else System.out.println("The correct answer is: " + target);
    }

    // Compare target and attempt string and store results in an integer array
    public static int[] matchDlewor(String target, String attempt, ArrayList<String> compare){
        int [] match = new int [5];

        // Compare target and attempt string
        for (int i = 0; i<attempt.length();i++){
            for (int k = 0;k<target.length();k++){
                if (attempt.charAt(i)==target.charAt(k)){ // right letter
                    if (i==k) { // right location
                        match[i] = 0; // green
                    }
                    else{ // wrong location
                        if (compare.size()==0){ // no repeat letter
                            match[i] = 1; // yellow
                        }
                        else{ // have repeat letters
                            // compare with the listed repeat letters
                            for (int j = 0; j<compare.size();j+=2){
                                if (attempt.charAt(i)==compare.get(j).charAt(0)){ // matches with the listed letter
                                    match[i] = -1; // grey

                                    // delete the repeat letter
                                    if (Integer.parseInt(compare.get(j+1))>1){
                                        compare.set(j+1,Integer.toString((Integer.parseInt(compare.get(j+1))-1)));
                                    }
                                    else{
                                        compare.remove(j+1);
                                        compare.remove(j);
                                    }
                                    break;
                                }
                                else{ // does not match any repeat letters
                                    match[i] = 1; // yellow
                                }
                            }
                        }
                    }
                    break;
                }
                else{ // wrong letter
                    match[i] = -1; //grey
                }
            }
        }

        // final checking repeat letters situation
        for (int i = 0; i<target.length();i++){
            if (attempt.charAt(i)==target.charAt(i)){
                match[i] = 0;
            }
        }
        return match;
    }

    // print word with colors based on match array
    public static void printDlewor(String attempt, int [] match){
        // print out color and letter
        for (int i = 0; i < match.length;i++){
            if (match[i] == 0) System.out.print(ANSI_GREEN_BACKGROUND + ANSI_BLACK + attempt.charAt(i)); // green
            else if (match[i] == 1) System.out.print(ANSI_YELLOW_BACKGROUND + ANSI_BLACK + attempt.charAt(i)); // yellow
            else System.out.print(ANSI_WHITE_BACKGROUND + ANSI_BLACK + attempt.charAt(i)); // grey
        }
        System.out.println(ANSI_RESET); // no color background after
    }

    // check if user get the right word
    public static Boolean foundMatch(int [] match){
        for (int j : match) {
            if (j != 0) {  // not green
                return false;
            }
        }
        return true;
    }

    // binary search if the word exist
    public static int binarySearch(ArrayList<String> dictionary, int start, int end, String attempt){ // Is this target or attempt
        if (start +1 == end){ // base case, did not find a matching word in the dictionary
            return -1;
        }
        else if (dictionary.get((start+end)/2).equals(attempt)){ //word of middle index is the input word
            return (start+end)/2;
        }
        else if (dictionary.get((start+end)/2).compareTo(attempt)>0){ // word of middle index is greater than the input word
            return binarySearch(dictionary,start,(start+end)/2,attempt);
        }
        else{ // word of middle index is less than the input word
            return binarySearch(dictionary,(start+end)/2,end,attempt);
        }
    }

    // linear search if the word exist, compare the start and end word
    public static int linearSearch(ArrayList<String> dictionary, int start, int end, String attempt){
        if (start>end){ // base case, did not find a matching word in the dictionary
            return -1;
        }
        else if (dictionary.get(start).equals(attempt)){ // compare the first word with user input
            return start;
        }
        else if (dictionary.get(end).equals(attempt)){ // compare the last word with user input
            return end;
        }
        else{ // no match, move to the center of the array
            return linearSearch(dictionary, start+1, end -1, attempt);
        }
    }

    // create an arraylist with unique character and the corresponding quantity
    public static ArrayList<String> repeatLetter(String word){
        // create a new arraylist
        ArrayList<String> targetChar = new ArrayList<>();

        // track unique character
        int repeat = 0;
        while (word.length()>0) {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(0) == word.charAt(i)) {
                    repeat++;
                }
            }
            // add each unique character in the format of unique character and the times they repeat
            targetChar.add(Character.toString(word.charAt(0)));
            targetChar.add(Integer.toString(repeat));
            // delete the one we compared
            word = word.replace(Character.toString(word.charAt(0)), "");
            repeat = 0;
        }
        return targetChar;
    }

    // compare if any overlap repeating characters from 2 arraylist
    public static void compareRepeatLetter(ArrayList<String> target,ArrayList<String> attempt, ArrayList<String> compare){
        for (int i = 0; i < target.size(); i+=2) {
            for (int k = 0; k < attempt.size();k+=2){
                // For the same character, check if the user inputs more character than the target
                // add the difference and that character to the arraylist
                if (target.get(i).equals(attempt.get(k)) && (attempt.get(k+1).compareTo(target.get(i+1))>0)){
                    compare.add(target.get(i));
                    compare.add(Integer.toString(Integer.parseInt(attempt.get(k+1))-Integer.parseInt(target.get(i+1))));
                }
            }
        }
    }

}
