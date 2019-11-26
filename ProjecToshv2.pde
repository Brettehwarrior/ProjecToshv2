/* ProjecTosh v2 by Trent Baker (c) 2019
 *
 * This is the Processing rewrite of the original PyQt5 Python ProjecTosh program which ended up being beyond my knowledge or desire to learn something I'll never use again
 * I decided Processing is better and I actually know it to some degree already so here we go again
 * Projectosh is a GUI program designed for use on the hallway projectors at Three Oaks Senior High so announcements and video slideshow can be played simultaneously 
 */

// Imports
import processing.video.*;
import org.jsoup.safety.*;
import org.jsoup.helper.*;
import org.jsoup.*;
import org.jsoup.parser.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;

// Global variables
Movie movie;
color colBG = color(96, 117, 73);
ArrayList<String> announcements = new ArrayList<String>();
String annHead = "";
String ann;

// ******************** Initialize ******************** //
void setup() {
  // Canvas
  size(960, 720);

  // Movie object
  movie = new Movie(this, "video.avi");
  movie.loop();

  // Announcements
  getAnnouncements();
  
  for (String s : announcements) {
    // Get header
    if (s.endsWith(":")) {
      annHead = s;
    }
    
    // Print
    if (!s.equals(annHead)) {
      println(annHead+" "+s);
    }
  }
}

// ******************** Loop ******************** //
void draw() {
  background(colBG);
  image(movie, movie.width/2 + 20, 20, movie.width/2 - 40, movie.height/2 - 40);
}

// ******************** Methods ******************** //
// Read frames from movie
void movieEvent(Movie movie) {
  movie.read();
}

// Create ArrayList of announcements
void getAnnouncements() {
  // Load announcements page html
  String[] lines;
  lines = loadStrings("https://threeoakshighschool.wordpress.com/daily-announcements/");
  
  // Convert string array into one String
  String html = join(lines, '\n');
  String txt = Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
  
  // Get announcemnents only
  String[] crop = txt.split("ANNOUNCEMENTS\n");
  crop = crop[3].split("      ");
  crop = crop[0].split("\n");

  // ArrayList and format loop
  for (String s : crop) {
    // Eliminate all goofy characters
    s = s.replace("&nbsp;", "").replace("&amp;", "&");
    // Add to ArrayList
    if (!s.equals("")) {
      announcements.add(s);
    }
  }
}
