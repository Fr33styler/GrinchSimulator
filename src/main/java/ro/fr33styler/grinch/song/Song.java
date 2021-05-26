package ro.fr33styler.grinch.song;

import java.io.File;
import java.util.HashMap;

public class Song {

    private HashMap<Integer, Layer> layer = new HashMap<Integer, Layer>();
    private short songHeight;
    private short length;
    private String title;
    private File path;
    private String name;
    private String author;
    private String description;
    private float speed;
    private int delay;
    private int frame = -1;
    private int tick = -40;
    private int id;

    public Song(Song other) {
    	this.id = other.getID();
        this.speed = other.getSpeed();
        delay = Math.round(20 / speed);
        this.layer = other.getLayer();
        this.songHeight = other.getSongHeight();
        this.length = other.getLength();
        this.title = other.getTitle();
        this.author = other.getAuthor();
        this.description = other.getDescription();
        this.path = other.getPath();
        this.name = other.getName();
    }
    
    public Song(int id, float speed, HashMap<Integer, Layer> layer, short songHeight, short length, String title, String name, String author, String description, File path) {
        this.speed = speed;
        this.id = id;
        delay = Math.round(20 / speed);
        this.layer = layer;
        this.songHeight = songHeight;
        this.length = length;
        this.title = title;
        this.author = author;
        this.description = description;
        this.path = path;
        this.name = name;
    }

    public int getID() {
    	return id;
    }
    
    public HashMap<Integer, Layer> getLayer() {
        return layer;
    }

    public short getSongHeight() {
        return songHeight;
    }

    public short getLength() {
        return length;
    }

    public String getTitle() {
        return title;
    }

    public int getTick() {
        return tick;
    }
    
    public int getFrame() {
        return frame;
    }
    
    public String getName() {
        return name;
    }
    
    public void setFrame(int frame) {
        this.frame = frame;
    }
    
    public void setTick(int tick) {
        this.tick = tick;
    }
    
    public String getAuthor() {
        return author;
    }

    public File getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public float getSpeed() {
        return speed;
    }

    public int getDelay() {
        return delay;
    }
}