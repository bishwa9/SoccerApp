package com.example.footballapp;

import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

// -------------------------------------------------------------------------
/**
 * The activity that harbours the GUI and holds the button listeners.
 *
 * @author Bishwamoy Sinha Roy
 * @version Oct 5, 2013
 */
public class MainActivity
    extends Activity
{
    private Button   playerB_; // button for player search
    private Button   teamB_; // button for team search
    private EditText input_; // text box for user input
    private TextView output_; // TextView for output
    private String   inputedString_; // holds the inputted string


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        playerB_ = (Button)findViewById(R.id.playerB);
        teamB_ = (Button)findViewById(R.id.teamB);
        input_ = (EditText)findViewById(R.id.input);
        output_ = (TextView)findViewById(R.id.out);
        inputedString_ = "";

        // listener for the player button
        OnClickListener listenerP = new OnClickListener() {

            public void onClick(View v)
            {
                // player request
                inputedString_ = input_.getText().toString();
                String formatted = inputedString_;
                if (formatted.contains(" "))
                {
                    formatted = formatted.replaceAll(" ", "%20");
                }
                final hyperlinkGetterTask task =
                    new hyperlinkGetterTask(MainActivity.this);
                task.execute(formatted, "player");
                // update GUI
                MainActivity.this.display("p." + "Processing request...");
            }
        };

        // listener for the team button
        OnClickListener listenerT = new OnClickListener() {

            public void onClick(View v)
            {
                // team request
                inputedString_ = input_.getText().toString();
                String formatted = inputedString_;
                if (formatted.contains(" "))
                {
                    formatted = formatted.replaceAll(" ", "%20");
                }
                final hyperlinkGetterTask task =
                    new hyperlinkGetterTask(MainActivity.this);
                task.execute(formatted, "team");
                // update GUI
                MainActivity.this.display("p." + "Processing request...");
            }
        };

        playerB_.setOnClickListener(listenerP);
        teamB_.setOnClickListener(listenerT);
    }


    // ----------------------------------------------------------
    /**
     * Called to display on the TextView
     *
     * @param outText : what to display (must prepend a message descriptor)
     */
    public void display(String outText)
    {
        String result = outText;
        int delim = result.indexOf(".");
        String source = result.substring(0, delim);
        result = result.substring(delim + 1);
        if( source.equals("h") || source.equals("s") )
        { // no need to erase what was already being displayed
            output_.append(result);
        }
        else if(source.equals("p"))
        { // delete what was there before...
            output_.setText(result);
        }
    }


    // ----------------------------------------------------------
    /**
     * To access what was entered
     * @return : inputted string
     */
    public String inputedString()
    {
        return inputedString_;
    }
}
