//Written by Tomer Levy and Kiran Kurian

package application;

import java.io.*;

import java.net.URL;
import java.util.ArrayList;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class MyController implements Initializable{
	@FXML TextField songNameBox;
	@FXML TextField artistBox;
	@FXML TextField yearBox;
	@FXML TextField albumBox;
	@FXML ListView<String> listViewContainer;
	@FXML Button deleteButton;
	@FXML Button editButton;
	@FXML Button addSongButton;
	@FXML Button cancelButtonEdit;
	@FXML Button confirmButtonEdit;
	@FXML Button cancelButtonDelete;
	@FXML Button confirmButtonDelete;
	@FXML Text requiredFieldText;
	@FXML Text confirmChangesText;
	@FXML Text confirmDeleteText;
	ObservableList<String> obsList;
	ArrayList<Song> allSongs = new ArrayList<Song>();
	ArrayList<String> allSongsStrings = new ArrayList<String>();
	final String SAVE_FILE_NAME = "save.txt";
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		readSave();
		System.out.println(allSongs);
	}
	@FXML public void handleMouseClick(MouseEvent arg0){
		int index = listViewContainer.getSelectionModel().getSelectedIndex();
		if(index >= 0) {
			Song selectedSong = allSongs.get(index);
			songNameBox.setText(selectedSong.name);
			artistBox.setText(selectedSong.artist);
			if(selectedSong.year>=0){
				yearBox.setText(String.valueOf(selectedSong.year));
			}else{
				yearBox.setText("");
			}
			albumBox.setText(selectedSong.album);
		}
	}
	public void addSongAll(Song s) {
		if(!checkDuplicate(s)){
			sortAllSongs(s);
			
			//All songs exists for checking duplicates, and later use for indexes with delete/edit
			sortAllSongsStrings(s);
			//If there are no duplicates, correctly place the new song in the arraylist
			int i =getLocation(s);
			listViewContainer.getSelectionModel().select(allSongsStrings.get(i));
			deleteButton.setDisable(false);
			editButton.setDisable(false);
		}else{
			//Oh no! a duplicate!!!!!!
//			Alert alert = new Alert(AlertType.ERROR);
//			alert.setTitle("Song was not added!");
//			alert.setContentText("Please ensure that you have not entered a duplicate entry!");
//			alert.showAndWait();
			return;
		}
		
		obsList=FXCollections.observableArrayList(allSongsStrings);
		listViewContainer.setItems(obsList);
//		int index = listViewContainer.getSelectionModel().getSelectedIndex();
//		Song selectedSong = allSongs.get(index);
//		songNameBox.setText(selectedSong.name);
//		artistBox.setText(selectedSong.artist);
//		if(selectedSong.year>=0){
//			yearBox.setText(String.valueOf(selectedSong.year));
//		}else{
//			yearBox.setText("");
//		}
//		albumBox.setText(selectedSong.album);
	}
	public boolean saveProgress() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(SAVE_FILE_NAME));
			for(Song s: allSongs) {
				writer.write(s.toString() + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	public void readSave() {
		File save = new File(SAVE_FILE_NAME);
//		char [] line = new char[500];
		
		try (BufferedReader br = new BufferedReader(new FileReader(save))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String[] lineSplit = line.split(" - ");
		    	if(lineSplit.length >= 2) {
		    		String name = lineSplit[0];
		    		String artist = lineSplit[1];
		    		String album = null;
		    		int year = 0;
		    		Song s;
		    		if(lineSplit.length == 4) {
		    			album = lineSplit[2];
		    			year = Integer.parseInt(lineSplit[3]);
		    			s = new Song(name, artist, album, year);
		    		}
		    		else if(lineSplit.length == 3) {
		    			if(lineSplit[2].matches("^-?\\d+$")) {
		    				year = Integer.parseInt(lineSplit[2]);
		    				s = new Song(name, artist, year);
		    			}
		    			else {
		    				album = lineSplit[2];
		    				s = new Song(name, artist, album);
		    			}
		    		}
		    		else {
		    			s = new Song(name, artist);
		    		}
		    		addSongAll(s);
//		    		System.out.println(s);
		    	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("Couldn't find " + SAVE_FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("Error reading " + SAVE_FILE_NAME);
		}
//		System.out.println();
//		try {
//			fr.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public void addSong(){
		addToList();
		saveProgress();
	}
	public void addToList(){
		String songName=songNameBox.getText();
		String artist=artistBox.getText();
		
		if(artist.length()==0||songName.length()==0){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Either song name or artist fields are empty");
			alert.setContentText("Please input values for song name / artist.");
			alert.showAndWait();
			return;
		}
		
		int year;
		try{
			year=Integer.parseInt(yearBox.getText());
		} catch(NumberFormatException ex){
			year=-1;
		}
		
		String album=albumBox.getText();
		Song s;
		if(year<0&& album.length()==0){
			s= new Song(songName,artist);
		} else if(year<0&& album.length()>0){
			s= new Song(songName,artist,album);
		} else if(year >0 && album.length()==0){
			s= new Song(songName,artist,year);
		}else{
			s= new Song(songName,artist,album, year);
		}
	
		if(!checkDuplicate(s)){
			sortAllSongs(s);
			
			//All songs exists for checking duplicates, and later use for indexes with delete/edit
			sortAllSongsStrings(s);
			//If there are no duplicates, correctly place the new song in the arraylist
			int i =getLocation(s);
			listViewContainer.getSelectionModel().select(allSongsStrings.get(i));
			deleteButton.setDisable(false);
			editButton.setDisable(false);
		}else{
			//Oh no! a duplicate!!!!!!
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Song was not added!");
			alert.setContentText("Please ensure that you have not entered a duplicate entry!");
			alert.showAndWait();
			return;
		}
		
		obsList=FXCollections.observableArrayList(allSongsStrings);
		listViewContainer.setItems(obsList);
		int index = listViewContainer.getSelectionModel().getSelectedIndex();
		Song selectedSong = allSongs.get(index);
		songNameBox.setText(selectedSong.name);
		artistBox.setText(selectedSong.artist);
		if(selectedSong.year>=0){
			yearBox.setText(String.valueOf(selectedSong.year));
		}else{
			yearBox.setText("");
		}
		albumBox.setText(selectedSong.album);

	}
	public void delete(){
		addSongButton.setVisible(false);
		addSongButton.setDisable(true);
		cancelButtonDelete.setVisible(true);
		cancelButtonDelete.setDisable(false);
		confirmButtonDelete.setVisible(true);
		confirmButtonDelete.setDisable(false);
		requiredFieldText.setVisible(false);
		confirmDeleteText.setVisible(true);
		editButton.setDisable(true);
		deleteButton.setDisable(true);
	}
	public void doDelete(){

		if(listViewContainer.getSelectionModel().getSelectedIndex()!=-1){
			int index = listViewContainer.getSelectionModel().getSelectedIndex();
			allSongs.remove(index);
			allSongsStrings.remove(index);
			obsList=FXCollections.observableArrayList(allSongsStrings);
			listViewContainer.setItems(obsList);
			try{
				Song selectedSong = allSongs.get(index);
				songNameBox.setText(selectedSong.name);
				artistBox.setText(selectedSong.artist);
				if(selectedSong.year>=0){
					yearBox.setText(String.valueOf(selectedSong.year));
				}else{
					yearBox.setText("");
				}
				albumBox.setText(selectedSong.album);
				listViewContainer.getSelectionModel().select(allSongsStrings.get(index));
				deleteButton.setDisable(false);
				editButton.setDisable(false);
			} catch(IndexOutOfBoundsException ex){
				index-=1;
				if(index<0){
					songNameBox.setText("");
					artistBox.setText("");
					yearBox.setText("");
					albumBox.setText("");
					deleteButton.setDisable(true);
					editButton.setDisable(true);
				}else{
					Song selectedSong = allSongs.get(index);
					songNameBox.setText(selectedSong.name);
					artistBox.setText(selectedSong.artist);
					if(selectedSong.year>=0){
						yearBox.setText(String.valueOf(selectedSong.year));
					}else{
						yearBox.setText("");
					}
					albumBox.setText(selectedSong.album);
					listViewContainer.getSelectionModel().select(allSongsStrings.get(index));
					deleteButton.setDisable(false);
					editButton.setDisable(false);
				}
			}
		}
		reset();
	}
	public void edit(){
		addSongButton.setVisible(false);
		addSongButton.setDisable(true);
		cancelButtonEdit.setVisible(true);
		cancelButtonEdit.setDisable(false);
		confirmButtonEdit.setVisible(true);
		confirmButtonEdit.setDisable(false);
		requiredFieldText.setVisible(false);
		confirmChangesText.setVisible(true);
		
		editButton.setDisable(true);
		deleteButton.setDisable(true);

	}
	public void doEdit(){
		String newSongName = songNameBox.getText();
		String newArtistName= artistBox.getText();
		int newYear;
		try{
			newYear=Integer.parseInt(yearBox.getText());
		} catch(NumberFormatException ex){
			newYear=-1;
		}
		String newAlbumName=albumBox.getText();
		Song s;
		if(newYear<0&& newAlbumName.length()==0){
			s= new Song(newSongName,newArtistName);
		} else if(newYear<0&& newAlbumName.length()>0){
			s= new Song(newSongName,newArtistName,newAlbumName);
		} else if(newYear >0 && newAlbumName.length()==0){
			s= new Song(newSongName,newArtistName,newYear);
		}else{
			s= new Song(newSongName,newArtistName,newAlbumName, newYear);
		}
		if(checkDuplicate(s)){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Song was not edited!");
			alert.setContentText("Please ensure that you actually changed song name or artist name!");
			alert.showAndWait();
			return;
		}else{
			newArtistName.trim().replaceAll(" +", " ");
			newSongName.trim().replaceAll(" +", " ");
			if(newArtistName.equals("") || newSongName.equals("")){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Song was not edited!");
				alert.setContentText("Please ensure you properly inputted song name and artist name!");
				alert.showAndWait();
				int index = listViewContainer.getSelectionModel().getSelectedIndex();
				Song selectedSong = allSongs.get(index);
				songNameBox.setText(selectedSong.name);
				artistBox.setText(selectedSong.artist);
				if(selectedSong.year>=0){
					yearBox.setText(String.valueOf(selectedSong.year));
				}else{
					yearBox.setText("");
				}
				albumBox.setText(selectedSong.album);
				return;
			}
			doDelete();
			sortAllSongs(s);
			sortAllSongsStrings(s);
			obsList=FXCollections.observableArrayList(allSongsStrings);
			listViewContainer.setItems(obsList);
			int index =getLocation(s);
			listViewContainer.getSelectionModel().select(allSongsStrings.get(index));
			Song selectedSong = allSongs.get(index);
			songNameBox.setText(selectedSong.name);
			artistBox.setText(selectedSong.artist);
			if(selectedSong.year>=0){
				yearBox.setText(String.valueOf(selectedSong.year));
			}else{
				yearBox.setText("");
			}
			albumBox.setText(selectedSong.album);
			deleteButton.setDisable(false);
			editButton.setDisable(false);
		}
		reset();
	}
	public void reset(){
		addSongButton.setVisible(true);
		addSongButton.setDisable(false);
		cancelButtonEdit.setVisible(false);
		cancelButtonEdit.setDisable(true);
		confirmButtonEdit.setVisible(false);
		confirmButtonEdit.setDisable(true);
		requiredFieldText.setVisible(true);
		confirmChangesText.setVisible(false);
		cancelButtonDelete.setVisible(false);
		cancelButtonDelete.setDisable(true);
		confirmButtonDelete.setVisible(false);
		confirmButtonDelete.setDisable(true);
		requiredFieldText.setVisible(true);
		confirmChangesText.setVisible(false);
		confirmDeleteText.setVisible(false);
		deleteButton.setDisable(false);
		editButton.setDisable(false);
		int index = listViewContainer.getSelectionModel().getSelectedIndex();
		if(index>=0){
			Song selectedSong = allSongs.get(index);
			songNameBox.setText(selectedSong.name);
			artistBox.setText(selectedSong.artist);
			if(selectedSong.year>=0){
				yearBox.setText(String.valueOf(selectedSong.year));
			}else{
				yearBox.setText("");
			}
			albumBox.setText(selectedSong.album);
		}
		saveProgress();
	}
	public boolean checkDuplicate(Song newSong){
		newSong.name= newSong.name.trim().replaceAll(" +", " ");
		newSong.artist=newSong.artist.trim().replaceAll(" +", " ");
		for(int i=0;i<allSongs.size();i++){
			Song check;
			check = allSongs.get(i);
			if(newSong.name.toLowerCase().equals(check.name.toLowerCase()) && newSong.artist.toLowerCase().equals(check.artist.toLowerCase())){
				return true;
			}
		}
		return false;
	}
	public String songToString(Song song){
		return song.name + " - " + song.artist;
	}
	public void sortAllSongsStrings(Song song){
		String s= songToString(song);
		s = s.trim().replaceAll(" +", " ");
		for(int i=0;i<allSongsStrings.size();i++){
			if(s.toLowerCase().compareTo(allSongsStrings.get(i).toLowerCase())<0){
				allSongsStrings.add(i, songToString(song));
				return;
			}
		}
		allSongsStrings.add(s);
	}
	public void sortAllSongs(Song song){
		String s= songToString(song);
		s = s.trim().replaceAll(" +", " ");
		for(int i=0;i<allSongs.size();i++){
			if(s.toLowerCase().compareTo(songToString(allSongs.get(i)).toLowerCase())<0){
				allSongs.add(i, song);
				return;
			}
		}
		allSongs.add(song);
	}
	public int getLocation(Song song){
		for(int i=0;i<allSongs.size();i++){
			if(song.artist==allSongs.get(i).artist && song.name==allSongs.get(i).name){
				return i;
			}
		}
		return 0;
	}
}
