//Written by Tomer Levy and Kiran Kurian
package application;

public class Song {
	public String name = "";
	public String artist = "";
	public String album = "";
	public int year = 0;
	
	public Song(String name, String artist, String album, int year) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	public Song(String name, String artist) {
		this(name, artist, "", -1);
	}
	public Song(String name, String artist, int year){
		this(name, artist, "", year);
	}
	public Song(String name, String artist, String album){
		this(name, artist, album, -1);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName(String name) {
		return this.name;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getArtist(String artist) {
		return this.artist;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getAlbum(String album) {
		return this.album;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	public int getYear(int year) {
		return this.year;
	}
	public String toString() {
		return  (this.name + " - " + this.artist) + (this.album != "" ? (" - " + this.album) : "") + (this.year != 0 ? (" - " + this.year) : "");
	}
}