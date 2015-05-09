package com.learningapp.infoproject.knowledgetogo;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Fabian on 01.05.15.
 */
public class Parser {

    static final int[] PARENTHESIS_VALUES = {123, 125}; // ascii numbers of parenthesis


    /**
     * Adds elements of the provided string to the layout in blank fields and text fields.
     * @param s String to parse
     * @param layout Layout to add to.
     * @param context Context off the app.
     * @return The added editTexts in a list
     */
    public static ArrayList<EditText> createGapText(String s, ViewGroup layout, Context context){
        ArrayList<String> elements = getTextElements(s);

        ArrayList<EditText> editTextList = new ArrayList<>();

        // First element indicates first element type
        boolean isGap = beginsWithGap(s);

        int editTextNumber = 0; // Number for the Ids of the EditTexts
        for (String element : elements) {
            if (!isGap) {
                TextView text = new TextView(context);
                text.setText(element);
                layout.addView(text);
            } else {
                EditText text = new EditText(context);
                text.setHint("Leerstelle");
                text.setMinimumWidth(20);
                text.setId(editTextNumber++);
                editTextList.add(text);
                layout.addView(text);
            }
            isGap = !isGap;
        }

        return editTextList;
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
        ArrayList<String> answers = getEntryElements(s);
        ArrayList<Boolean> correct = correctAnswers(entries, answers);

        // Adds answer-texts
        for(int i = 0; i < answers.size(); i++){
            TextView text = new TextView(context);
            text.setText(answers.get(i));
            if (correct.get(i)){
                text.setTextColor(Color.GREEN);
            } else {
                text.setTextColor(Color.RED);
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
        text.setText(Integer.toString(rightAnswers));
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
            do {
                b++;
            } while (b < s.length() && s.charAt(b) != PARENTHESIS_VALUES[isGap?1:0]); // isGap?1:0 returns 0 if false and 1 if true

            res.add(s.substring(a, b));

            a = b + 1;

            isGap = !isGap;

        }
        return res;
    }

    /**
     * Returns correct fill-in-elements from the text.
     * @param s String to parse
     * @return Right answers in a list
     */
    public static ArrayList<String> getEntryElements(String s){
        int a = 0;
        int b = 0;
        boolean isGap = beginsWithGap(s); //0 - Text; 1 - Empty field

        if (isGap){
            a++;
            b++;
        }

        ArrayList<String> res = new ArrayList<>();

        while (b < s.length()) {
            do {
                b++;
            } while (b < s.length() && s.charAt(b) != PARENTHESIS_VALUES[isGap?1:0]); // isGap?1:0 returns 0 if false and 1 if true

            if (isGap) {
                res.add(s.substring(a, b));
            }

            a = b + 1;

            isGap = !isGap;

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
