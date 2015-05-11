package com.learningapp.infoproject.knowledgetogo;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Fabian on 01.05.15.
 */
public class Parser {

    static final int[] PARENTHESIS_VALUES = {123, 125}; // ascii numbers of parenthesis
    static final float EDIT_TEXT_SIZE = 16;
    static final float TEXT_VIEW_SIZE = 16;
    static final int TEXT_HEIGHT = 50;
    static final int TEXT_PADDING = 5;

    /**
     * Adds elements of the provided string to the layout in blank fields and text fields.
     * @param s String to parse
     * @param layout Layout to add to.
     * @param context Context off the app.
     * @return The added editTexts in a list
     */
    public static ArrayList<EditText> createGapText(String s, ViewGroup layout, Context context){
        ArrayList<EditText> editTextList = new ArrayList<>();

        ArrayList<Boolean> isGap = getTextElementBooleans(s);
        ArrayList<String> elements = getTextElements(s);

        int editTextNumber = 0; // Number for the Ids of the EditTexts
        for (int i = 0; i < elements.size(); i++) {
            if (!isGap.get(i)) {
                TextView text = new TextView(context);
                text.setText(elements.get(i));
                text.setHeight(TEXT_HEIGHT);
                text.setTextSize(TEXT_VIEW_SIZE);
                text.setPadding(TEXT_PADDING,TEXT_PADDING,TEXT_PADDING,TEXT_PADDING);
                layout.addView(text);
            } else {
                EditText text = new EditText(context);
                text.setHint(context.getString(R.string.text_gap));
                text.setHeight(TEXT_HEIGHT);
                text.setTextSize(EDIT_TEXT_SIZE);
                text.setPadding(TEXT_PADDING,TEXT_PADDING,TEXT_PADDING,TEXT_PADDING);
                //text.setMinimumWidth(20);
                text.setId(editTextNumber++);
                editTextList.add(text);
                layout.addView(text);
            }
        }

        return editTextList;
    }


    /**
     * Checks if s has (at least one) filled and closed parenthesis-pairs
     * @param s input string
     * @return true if correct
     */
    public static boolean checkParenthesis(String s){
        boolean correct = false;
        boolean parenthesisOpen = false;

        for(int i = 0; i < s.length(); i++){
            if (!parenthesisOpen && s.charAt(i) == PARENTHESIS_VALUES[0]){
                i++; // at least on char inside
                correct = false;
                parenthesisOpen = true;
            } else if (parenthesisOpen && s.charAt(i) == PARENTHESIS_VALUES[1]){
                correct = true;
                parenthesisOpen = false;
            } else if ((!parenthesisOpen && s.charAt(i) == PARENTHESIS_VALUES[1])
                    || (parenthesisOpen && s.charAt(i) == PARENTHESIS_VALUES[0])
                    || (parenthesisOpen && s.charAt(i) == ' ')){
                return false;
            }
        }

        return correct;
    }

    /**
     * Creates the solve of the text.
     * @param s String to parse
     * @param entries Entered answers in a list.
     * @param layout Layout to add to.
     * @param context Context off the app.
     * @return The number of correct answers
     */
    public static int createSolve(String s, ArrayList<String> entries, ViewGroup layout, Context context){
        ArrayList<String> elements = getTextElements(s);
        ArrayList<Boolean> isGap = getTextElementBooleans(s);
        ArrayList<Boolean> correct = correctAnswers(entries, getEntryElements(elements,isGap));
        int k = 0; // counter for gaps

        // Adds answer-texts
        for(int i = 0; i < elements.size(); i++){
            TextView text = new TextView(context);
            text.setText(elements.get(i));
            text.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            if (isGap.get(i)) {
                if (correct.get(k)) {
                    text.setTextColor(Color.GREEN);
                } else {
                    text.setTextColor(Color.RED);
                }
                k++;
            }
            layout.addView(text);
        }

        // Adds correct-answers-counter
        int rightAnswers = 0;
        for (Boolean answer : correct){
            if (answer){
                rightAnswers++;
            }
        }

        TextView text = new TextView(context);
        text.setText("Du hast "+Integer.toString(rightAnswers)+" von "+Integer.toString(correct.size())+" richtig.");
        text.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
        layout.addView(text);

        return rightAnswers;
    }

    /**
     * Returns text elements divided by { and } in a list.
     * @param s String to parse
     * @return Elements in a list.
     */
    public static ArrayList<String> getTextElements(String s) {
        int a = 0;
        int b = 0;
        boolean isGap = beginsWithGap(s); //0 - Text; 1 - Empty field

        if (isGap){
            a++;
            b++;
        }

        ArrayList<String> res = new ArrayList<>();

        while (b < s.length()) {

            while (b < s.length() && (s.charAt(b) != PARENTHESIS_VALUES[0]
                    && s.charAt(b) != PARENTHESIS_VALUES[1]
                    && s.charAt(b) != ' ')){
                b++;
            }

            if (s.charAt(a) == PARENTHESIS_VALUES[1]){
                a++;
            }

            if (b - a > 0) {
                res.add(s.substring(a, b));
            }

            a = ++b;
        }

        return res;
    }

    /**
     * Returns correct fill-in-elements from the text.
     * @param elements All text elements
     * @param isGap ArrayList with booleans
     * @return Right answers in a list
     */
    public static ArrayList<String> getEntryElements(ArrayList<String> elements, ArrayList<Boolean> isGap){

        ArrayList<String> res = new ArrayList<>();

        for (int a = 0; a < isGap.size(); a++){
            if (isGap.get(a))
                res.add(elements.get(a));
        }

        return res;
    }

    /**
     * @param s String to check
     * @return true if first Element will be gap
     */
    public static boolean beginsWithGap(String s){
        return s.charAt(0) == PARENTHESIS_VALUES[0];
    }

    /**
     * @param s String to check
     * @return for every element a boolean
     */
    public static ArrayList<Boolean> getTextElementBooleans(String s){

        ArrayList<Boolean> res = new ArrayList<>();

        if(!beginsWithGap(s)){
            res.add(false);
        }

        for (int i = 0; i < s.length(); i++) {

            if (s.charAt(i) == ' ' && i + 1 < s.length() && s.charAt(i+1) != ' ' && s.charAt(i+1) != PARENTHESIS_VALUES[0]){
                res.add(false);
            }

            if (s.charAt(i) == PARENTHESIS_VALUES[1] && i + 1 < s.length() && s.charAt(i+1) != ' '){
                res.add(false);
            }

            if (s.charAt(i) == PARENTHESIS_VALUES[0]){
                res.add(true);
            }

        }
        return res;

    }

    /**
     * Compares two Lists of Strings.
     * @param entries List to check
     * @param answers Right answers
     * @return a boolean list with right and wrong
     */
    public static ArrayList<Boolean> correctAnswers(ArrayList<String> entries, ArrayList<String> answers){
        ArrayList<Boolean> correct = new ArrayList<>();

        for (int i = 0; i < answers.size(); i++){
            correct.add(answers.get(i).equalsIgnoreCase(entries.get(i)));
        }

        return correct;
    }

}
