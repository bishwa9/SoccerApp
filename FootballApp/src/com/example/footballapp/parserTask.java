package com.example.footballapp;

import java.util.ArrayList;
import android.os.AsyncTask;

// -------------------------------------------------------------------------
/**
 * This class inherits from AsyncTask to provide threading opportunities An
 * instance of this class takes in an html and spits out meaningful information
 * to the GUI THread to display onto the GUI
 *
 * @author Bishwamoy Sinha Roy
 * @version Oct 5, 2013
 */
public class parserTask
    extends AsyncTask<String, Void, String>
{
    private MainActivity guiThread_;


    // ----------------------------------------------------------
    /**
     * Create a new parserTask object.
     *
     * @param thread
     *            - instance of the main thread (GUI)
     */
    public parserTask(MainActivity thread)
    {
        guiThread_ = thread;
    }


    // Background task is to parse HTML input
    protected String doInBackground(String... params)
    {
        return callAppropriateParse(params[0]);
    }


    // after background task is done, the result is put onto the screen
    protected void onPostExecute(String result)
    {
        guiThread_.display(result + "\n");
    }


    // ----------------------------------------------------------
    /**
     * Looks at the input (other tasks put in information) to determine what
     * type of HTML is being passed The followin are prepended to HTML string
     * for this method p.p. = player search results p.t. = team search result
     * s.p. = player stats s.t. = team stats
     *
     * @param result
     *            - relevant information formatter for output
     */
    public String callAppropriateParse(String result)
    {
        int delim = result.indexOf(".");
        String type = result.substring(0, delim);
        result = result.substring(delim + 1);
        if (type.equals("p"))
        {
            // hyperlink search
            delim = result.indexOf(".");
            type = result.substring(0, delim);
            if (type.equals("p"))
            {
                // player hyperlink
                result = result.substring(delim + 1);
                return "p." + parsePlayerSearch(result);
            }
            else if (type.equals("t"))
            {
                // team hyperlink
                result = result.substring(delim + 1);
                return "p." + parseTeamSearch(result);
            }
        }
        else if (type.equals("s"))
        {
            // stats parsing
            delim = result.indexOf(".");
            type = result.substring(0, delim);
            if (type.equals("p"))
            {
                // player stats
                result = result.substring(delim + 1);
                return "p." + parsePlayerStats(result);
            }
            else if (type.equals("t"))
            {
                // team stats
                result = result.substring(delim + 1);
                return "p." + parseTeamStats(result);
            }
        }
        // it should never come here
        return "p." + "Problem with what was passed to parser!";
    }


    // ----------------------------------------------------------
    /**
     * Parse appropriate HTML to extract relevant team stat info
     *
     * @param response
     *            - rank and upcoming matches formatted into a string
     */
    public String parseTeamStats(String response)
    {
        String output = "";

        // determine rank
        int start = response.indexOf("UEFA RANKING 2013/14");
        // only output UEFA ranked teams
        if (start == -1)
            return "No UEFA Ranking for the team";
        response = response.substring(start);
        start = response.indexOf("<div>");
        response = response.substring(start + "<div>".length() + 1);
        String rank =
            response.substring(
                response.indexOf(">") + 1,
                response.indexOf("</span>"));
        output += "Team Rank: " + rank + "\n";

        // determine 5 matches
        ArrayList<String> matches = new ArrayList<String>(0);
        response = response.substring(response.indexOf("matchesindex"));
        while (matches.size() < 5)
        {
            // many games are provided, only interested in upcomin matches
            // extract a single game
            String game =
                response.substring(
                    response.indexOf("<tbody"),
                    response.indexOf("</tbody>"));
            // delete game from the original HTML
            response =
                response.substring(response.indexOf("</tbody>")
                    + "</tbody>".length());
            // parse date of game
            game = game.substring(game.indexOf("b dateT") + "b dateT".length());
            String date =
                game.substring(game.indexOf(">") + 1, game.indexOf(" </span>"));
            // parse home team
            game = game.substring(game.indexOf("home nob"));
            String homeTeam =
                game.substring(
                    game.indexOf("title=") + "title=".length() + 1,
                    game.indexOf(" class=") - 1);
            // parse score (determines if the current game is in the future or
            // not)
            game = game.substring(game.indexOf("c b score nob"));
            game = game.substring(game.indexOf(">") + 1);
            String score =
                game.substring(game.indexOf(">") + 1, game.indexOf("</a>"));
            // parse away team
            game = game.substring(game.indexOf("away nob"));
            String awayTeam =
                game.substring(
                    game.indexOf("title=") + "title=".length() + 1,
                    game.indexOf(" class=") - 1);
            // future games have the time as 16.20 so doesn't contain '-'
            if (score.contains("."))
            {
                // add to the arraylist
                String match =
                    "Date: " + date + "\nHome team: " + homeTeam
                        + "\nAway team: " + awayTeam + "\n";
                matches.add(match);
            }
        }
        // traverse arraylist and format into intelligible output
        for (int i = 0; i < matches.size(); i++)
        {
            output += "Match# " + (i + 1) + "\n";
            output += matches.get(i);
        }
        return output;
    }


    // ----------------------------------------------------------
    /**
     * Parse the HTML response to extract the team's hyperlink
     *
     * @param response: the html response from the hyperlinkgettertask
     */
    public String parseTeamSearch(String response)
    {
        // cut off everything till the teams section
        response = response.substring(response.indexOf(">Teams</h3>"));
        int start = response.indexOf("<li>");
        int end = response.indexOf("</ul>");
        if (start != -1 && end != -1 && start < end)
        {
            response = response.substring(start, end);
        }
        else
        {
            return "Couldn't parse team!";
        }
        boolean found = false;
        String resource = "";
        String name = "";
        // go through HTML till the team is found
        while ((start = response.indexOf("<li>")) != -1 && !found)
        {
            response = response.substring(start);
            start = response.indexOf("href=") + "href=".length() + 1;
            resource =
                response.substring(start, response.indexOf(" title") - 1);
            response = response.substring(response.indexOf(" title="));
            name =
                response.substring(
                    response.indexOf("title=") + "title=".length(),
                    response.indexOf(">"));
            // for team search have to enter proper name
            if (name.toLowerCase().contains(
                guiThread_.inputedString().toLowerCase())
                && !name.toLowerCase().contains("youth".toLowerCase())
                && !name.toLowerCase().contains("women".toLowerCase()))
            {
                found = true;
            }
            response =
                response
                    .substring(response.indexOf("</li>") + "</li>".length());
        }
        if (found)
        {
            statsGetterTask actual = new statsGetterTask(this.guiThread_);
            actual.execute(resource, "team");
            return "Searching for " + name + "...";
        }
        else
        {
            return "Error: Team not found!";
        }
    }


    // ----------------------------------------------------------
    /**
     * Parse the specific player's html document to get stats
     *
     * @param response: result of httpRequest in statsGetterTask
     */
    public String parsePlayerStats(String response)
    {
        String output = "";
        // extract the table with the information of the player
        response = response.substring(response.indexOf("<tbody>"));
        int delim = 0;
        // go through the table
        while ((delim = response.indexOf("<tr>")) < response
            .indexOf("</tbody>"))
        {
            if (delim == -1)
                break;
            response = response.substring(delim + "<tr>".length());
            // get the stats' descriptor
            String statDescriptor =
                response.substring(
                    response.indexOf("<td>") + "<td>".length(),
                    response.indexOf("</td>"));
            response =
                response
                    .substring(response.indexOf("</td>") + "</td>".length());
            // extract the stat
            String stat =
                response.substring(
                    response.indexOf("<td>") + "<td>".length(),
                    response.indexOf("</td>"));
            response =
                response
                    .substring(response.indexOf("</td>") + "</td>".length());
            // add to the string
            output += statDescriptor + " " + stat + "\n";
        }
        // return string to print
        return output;
    }


    // ----------------------------------------------------------
    /**
     * To extract the hyperlink for the specific player
     *
     * @param response: result of HttpRequest from the hyperlinkGetterTask
     */
    public String parsePlayerSearch(String response)
    {
        // store all results in case more than one player was returned
        ArrayList<String> searchResults = new ArrayList<String>(0);
        int delim = response.indexOf("Search Results");
        String player = "";
        if (delim != -1)
        {
            // search for the beginning of the search results
            response = response.substring(delim + "Search Results".length());

            // read every result
            while ((delim = response.indexOf("<li>")) != -1)
            {
                if (response.indexOf("First Team Players") < delim)
                    break;
                // add the string corresponding to each player into the
                // arraylist
                searchResults.add(response.substring(
                    delim + "<li>".length(),
                    response.indexOf("</li>")));
                // cut off the player just considered
                delim = response.indexOf("</li>") + "</li>".length();
                response = response.substring(delim + 1);
            }
            // if only one player was found then initiate another http
            // request and output to the screen an update
            if (searchResults.size() == 1)
            {
                player = searchResults.get(0);
                int start = player.indexOf("<a href=");
                int end = player.indexOf(">");
                String secondaryResource =
                    player.substring(
                        start + "<a href=".length() + 1,
                        player.indexOf(">") - 1);
                player = player.substring(end + 1); // update the string
                // display on GUI that this player is being searched for
                start = player.indexOf("alt=") + "alt=".length();
                end = player.indexOf(">") - 1;
                String name = player.substring(start, end);
                // create new task for an http request for the actual player
                // info
                statsGetterTask actual = new statsGetterTask(this.guiThread_);
                actual.execute(secondaryResource, "player");
                return "Searching for " + name + "...";
            }
            else if (searchResults.size() == 0)
            {
                return "No player by " + this.guiThread_.inputedString();
            }
            else
            {
                String output =
                    "Following players were found, enter one name!\n";
                for (int i = 0; i < searchResults.size(); i++)
                {
                    player = searchResults.get(i);
                    delim = player.indexOf(">");
                    player = player.substring(delim + 1); // update the string
                    delim = player.indexOf("alt=") + "alt=".length();
                    int end = player.indexOf(">") - 1;
                    String name = player.substring(delim, end);
                    output += "Player :" + name + "\n";
                }
                return output;
            }
        }
        else
        {
            return "Couldn't parse Player!";
        }
    }
}
