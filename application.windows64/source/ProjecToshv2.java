import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import org.jsoup.safety.*; 
import org.jsoup.helper.*; 
import org.jsoup.*; 
import org.jsoup.parser.*; 
import org.jsoup.select.*; 
import org.jsoup.nodes.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ProjecToshv2 extends PApplet {

/* ProjecTosh v2 by Trent Baker (c) 2019
 *
 * This is the Processing rewrite of the original PyQt5 Python ProjecTosh program which ended up being beyond my knowledge or desire to learn something I'll never use again
 * I decided Processing is better and I actually know it to some degree already so here we go again
 * Projectosh is a GUI program designed for use on the hallway projectors at Three Oaks Senior High so announcements and video slideshow can be played simultaneously 
 */

// Imports








// Global variables
Movie movie;
int colBG = color(96, 117, 73);
ArrayList<String> announcements = new ArrayList<String>();
String annHead = "";
String ann;
int time;
int timerAmount = 5000;
int timer = timerAmount; // Announcement timer in seconds
ArrayList<String>[] announcementGrid;
int annI;
int annJ;
PFont fontReg;
PFont fontBold;
float movieWidth, movieHeight;
JSONObject json;
float announcementScale;

// ******************** Initialize ******************** //
public void setup() {
  // Load JSON settings
  json = loadJSONObject("data/settings.json");
  announcementScale = json.getFloat("announcement scale");
  
  // Canvas
  
  //size(960, 720);

  // Movie object
  movie = new Movie(this, "video.avi");
  movie.loop();

  // Announcements
  getAnnouncements();


  // Draw announcements
  int annHeadings = 1;
  for (String s : announcements) {
    // Get header
    if (s.endsWith(":")) {
      annHead = s;
      annHeadings += 1;
    }

    // Print
    if (!s.equals(annHead)) {
      println(annHead+" "+s);
    }
  }
  // Initialize announcementGrid
  announcementGrid = new ArrayList[annHeadings];
  for (int i = 0; i < annHeadings; i++) {
    announcementGrid[i] = new ArrayList<String>();
  }
  int i = 0;
  for (String s : announcements) {
    if (s.endsWith(":")) {
      i++;
    }
    // Add text to List
    announcementGrid[i].add(s);
  }


  // Update time
  time = millis();
  annI = 1;
  annJ = 1;
  
  // Fonts
  String[] fontList = PFont.list();
  for (String f : fontList) {
    println(f);
  }
  fontReg = createFont("Arial", 1);
  fontBold = createFont("Arial Bold", 1);
}

// ******************** Loop ******************** //
public void draw() {
  // BG
  background(colBG);
  
  // Title
  textFont(fontBold);
  textAlign(CENTER, TOP);
  textSize(width/45);
  text("Three Oaks Senior High", width/6+10, 30);
  
  // Info column
  textFont(fontBold);
  textSize(width/40);
  text(announcementGrid[0].get(1), width/6+10, 80);
  textFont(fontReg);
  textSize(width/60);
  text(announcementGrid[0].get(0), width/6+10, 130);
  drawTime();
  
  // Video
  drawMovie();

  // Announcements
  drawAnnouncements();
  
  // Feedback link
  textAlign(CENTER);
  textFont(fontBold);
  textSize(16);
  text("This is a beta. Please give feedback here: https://bit.ly/385xvt8", width/2, height-20);
}

// ******************** Methods ******************** //
// Read frames from movie
public void movieEvent(Movie movie) {
  movie.read();
}

// Draw movie
public void drawMovie() {
  int padding = 30;
  int x = width/3;
  int y = 20;
  movieWidth = 2*width/3;
  movieHeight = movie.height/(movie.width/movieWidth);
  image(movie, x+padding, y+padding, movieWidth-padding*2, movieHeight-padding*2);
}

// Draw announcements
public void drawAnnouncements() {
  textAlign(LEFT);
  textFont(fontBold);
  textSize((width/35)*announcementScale);
  text(announcementGrid[annI].get(0).replace(':',' ')+"ANNOUNCEMENTS:", 200, movieHeight+40, width-400, 100);
  textFont(fontReg);
  textSize((width/40)*announcementScale);
  text(announcementGrid[annI].get(annJ), 200, movieHeight+90, width-400, 200);

  // Update time
  time = millis();
  if (time > timer) {
    timer += timerAmount;
    // Cycle announcement
    if (annJ < announcementGrid[annI].size()-1) {
      annJ++;
    } else {
      if (annI < announcementGrid.length-1) {
        annI++;
      } else {
        annI = 1;
      }
      annJ = 1;
    }
  }
}


// Create ArrayList of announcements
public void getAnnouncements() {
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

public void drawTime() {
  textFont(fontBold);
  textSize(width/30);
  int s = second();  // Values from 0 - 59
  int m = minute();  // Values from 0 - 59
  int h = hour();    // Values from 0 - 23
  h = h % 12;
  text(h+":"+nf(m, 2, 0)+":"+nf(s, 2, 0), width/6+10, 200);
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ProjecToshv2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
