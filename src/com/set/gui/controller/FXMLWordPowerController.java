package com.set.gui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.set.constants.query.ModeConst;
import com.set.constants.query.QueryConst;
import com.set.ds.query.ClassQ;
import com.set.ds.query.inter.QLLiteral;
import com.set.index.impl.DiskInvertedIndex;
import com.set.index.impl.IndexWriter;
import com.set.math.RankedRetrival;
import com.set.query.ProcessQuery;
import com.set.query.QueryOperations;
import com.set.query.ValidateQuery;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FXMLWordPowerController implements Initializable {
	private Stage stage;
	private String dirPath;
	private DiskInvertedIndex diskIndex = null;
	private int mode = 0; // 1 -> index, 2 -> query
	private boolean init = false; // set to true. Once you pass through the first prompt successfully
	@FXML protected MenuItem mi_index_dir; 
	@FXML protected MenuItem mi_close;
	@FXML protected ImageView img_find;
	@FXML protected TextField txt_find;
	@FXML protected ListView<String> list_files;
	@FXML protected Label lab_count;
	@FXML protected Label lab_sec;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	// Menu -> Index Directory
    @FXML public void handleIndexDir(ActionEvent e){
//    	DirectoryChooser dc = new DirectoryChooser();
//    	dc.setTitle("Choose directory to index");
//    	File dir = dc.showDialog(stage);
//    	
//    	if (dir != null && dir.exists() && dir.isDirectory()) {
//    		dirPath = dir.getAbsolutePath();
//    	} else {
//    		Event.fireEvent(mi_close, new ActionEvent());
//    	}
    	mode = ModeConst.NILL;
    	handleChooseMode();
    }	
    
    @FXML protected void handleOpenFile(MouseEvent e) {
    	if(e.getClickCount() == 2){
	    	String fname = list_files.getSelectionModel().getSelectedItem();
	    	System.out.println(dirPath);
	    	if (!fname.isEmpty()) {
	    		try{
					Runtime.getRuntime().exec("notepad "+ dirPath+"\\"+fname);
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
	    	}
    	}
    }
    
    // Menu -> Close
    @FXML protected void handleClose(ActionEvent e){
    	this.stage.close();
    }
    
    // Menu -> About
    @FXML protected void handleAbout(ActionEvent e){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("About: WordPower");
    	alert.setHeaderText("WordPower: Search Engine Technology");
    	alert.setContentText("Indexes .txt files provided in a directory with support to querying.");
    	alert.initOwner(stage);
    	alert.showAndWait();
    }
    
    // Search -> click the icon
    @FXML protected void handleSearchClick(MouseEvent e) {
    	final long startTime = System.currentTimeMillis();
    	String query = txt_find.getText();
    	if (!ValidateQuery.isValidQuery(query)){
    		txt_find.setStyle("-fx-text-inner-color: red;");
    		List<Integer> empty = new ArrayList<>();
        	setResultList(empty);
        	setExecutionTime(0);
    		return;
		} 
    	List<Integer> orResultSet = null;
    	 switch (mode) {
         case ModeConst.DEFAULT:
        	 orResultSet = RankedRetrival.rankedQuery(query, diskIndex,mode);
         	break;
         case ModeConst.TRADITIONAL:
        	 orResultSet = RankedRetrival.rankedQuery(query, diskIndex,mode);
         	break;
         case ModeConst.OKAPI:
        	 orResultSet = RankedRetrival.rankedQuery(query, diskIndex,mode);
         	break;
         case ModeConst.WACKY:
        	 orResultSet = RankedRetrival.rankedQuery(query, diskIndex,mode);
         	break;
         case ModeConst.BOOLEAN:
        	 orResultSet = parseBooleanQuery(query, orResultSet);
         	break;
         }
//    	if (mode == ModeConst.RANKED) {
//    		orResultSet = RankedRetrival.rankedQuery(query, diskIndex);
//    	} else {
//    		parseBooleanQuery(query, orResultSet);
//    	}
    	final long endTime = System.currentTimeMillis();
    	setExecutionTime(endTime - startTime);
    	setResultList(orResultSet);
    }
    
    // Search -> Enter text
    @FXML protected void handleSearchKey(KeyEvent e) {
    	txt_find.setStyle("-fx-text-inner-color: black;");
    	if (e.getCharacter().equalsIgnoreCase("\r")) {
    		Event.fireEvent(img_find, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
    	}
    }

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	private void setResultList (List<Integer> files) {
		ObservableList<String> items = FXCollections.observableArrayList ();
		if (files == null) {
			files = new ArrayList<>();
		}
		files.stream().forEach((k) -> {
            items.add(diskIndex.getFileNames().get(k));
      });
//		for (final File fileEntry : dir.listFiles()) {
//	        if (!fileEntry.isDirectory() && fileEntry.isFile() && fileEntry.getName().endsWith(".txt")) {
//	        	// Filling list view with folder's files
//	    		items.add(fileEntry.getName());
//	        }
//	    }
		list_files.setItems(items);
		setListCount(items.size());
	}
	
	private void setListCount (int size) {
		lab_count.setText("Results Found: "+size);
	}
	
	private void setExecutionTime (long sec) {
		lab_sec.setText("Sec: "+(float)sec/1000);
	}
    
    private String handleDirChooser(){
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Choose a directory");
    	File dir = dc.showDialog(stage);
    	
    	if (dir != null && dir.exists() && dir.isDirectory()) {
    		dirPath = dir.getAbsolutePath();
    		return dirPath;
    	} else {
//    		Event.fireEvent(mi_close, new ActionEvent());
    	}
		return dirPath;
    }	
    
    private  List<Integer> parseBooleanQuery (String query, List<Integer> orResultSet) {
		ArrayList<ClassQ> orLiteralArr = ProcessQuery.objectifyQuery(query);
    	List<List<Integer>> orSet = new ArrayList<>();
    	for (ClassQ ca: orLiteralArr){
    		ArrayList<String> normalQueries = new ArrayList<>();
    		ArrayList<String> phraseQueries = new ArrayList<>();
    		ArrayList<String> notQueries = new ArrayList<>();
    		ArrayList<String> nearQueries = new ArrayList<>();
    		ArrayList<String> notPhraseQueries = new ArrayList<>();
    		ArrayList<String> notNearQueries = new ArrayList<>();
    		for (QLLiteral ql : ca.getQueryLiterals()) {
    			switch (ql.getType()) {
    			case QueryConst.NORMAL:
    				normalQueries.add(ql.getLiteral());
    				break;
    			case QueryConst.PHRASE:
    				phraseQueries.add(ql.getLiteral());
    				break;
    			case QueryConst.NOT:
    				notQueries.add(ql.getLiteral());
    				break;
    			case QueryConst.NEAR:
    				nearQueries.add(ql.getLiteral());
    				break;
    			case QueryConst.NOTNEAR:
    				notNearQueries.add(ql.getLiteral());
    				break;
    			case QueryConst.NOTPHRASE:
    				notPhraseQueries.add(ql.getLiteral());
    				break;
    			}
    		}
    		List<Integer> andNormalSet = QueryOperations.queryAND(normalQueries, diskIndex, diskIndex.getFileNames());
    		List<Integer> andPhraseSet = QueryOperations.queryPHRASE(phraseQueries, diskIndex, diskIndex.getFileNames());
    		List<Integer> orNotNormalSet = QueryOperations.queryOR(notQueries, diskIndex, diskIndex.getFileNames());
    		List<Integer> andNearSet = QueryOperations.queryNEAR(nearQueries, diskIndex, diskIndex.getFileNames());
    		List<Integer> orNotPhraseSet = QueryOperations.queryPHRASE(notPhraseQueries, diskIndex, diskIndex.getFileNames());
    		List<Integer> orNotNearSet = QueryOperations.queryNEAR(notNearQueries, diskIndex, diskIndex.getFileNames());
    		List<Integer> andResultSet;
    		List<List<Integer>> orNotSet = new ArrayList<>();
			List<List<Integer>> intersectSet = new ArrayList<>();
			if (!andPhraseSet.isEmpty()){
				intersectSet.add(andPhraseSet);
			}
			if (!andNormalSet.isEmpty()){
				intersectSet.add(andNormalSet);
			}
			if (!andNearSet.isEmpty()){
				intersectSet.add(andNearSet);
			}
			andResultSet = QueryOperations.intersection(intersectSet);
			List<List<Integer>> negationSet = new ArrayList<>();
			if (!orNotNormalSet.isEmpty()){
				orNotSet.add(orNotNormalSet);
			}
			if (!orNotPhraseSet.isEmpty()){
				orNotSet.add(orNotPhraseSet);
			}
			if (!orNotNearSet.isEmpty()){
				orNotSet.add(orNotNearSet);
			}
			List<Integer> resultNotSet = QueryOperations.union(orNotSet);
	    	negationSet.add(andResultSet);
	    	negationSet.add(resultNotSet);
	    	List<Integer> nottedAndResultSet = QueryOperations.negation(negationSet);
    		orSet.add(nottedAndResultSet);
    	}
    	orResultSet = QueryOperations.union(orSet);
    	return orResultSet;
    }
    
    public void handleChooseMode(){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Choose mode");
        alert.setHeaderText(null);
        alert.setContentText("Choose your option.");

        ButtonType index = new ButtonType("Index");
        ButtonType query = new ButtonType("Query");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(index, query, buttonTypeCancel);
        alert.initOwner(stage);
        String dirPath = null;
        int queryType =  ModeConst.NILL;
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == index){
        	mode = ModeConst.INDEX;
        	dirPath = handleDirChooser();
        } else if (result.get() == query) {
        	mode = ModeConst.QUERY;
        	queryType = handleChooseQueryType();
        	if (queryType != ModeConst.NILL) {
        		dirPath = handleDirChooser();
        		mode = queryType;
        	}
        } else {
        	 // ... user chose CANCEL or closed the dialog
        	if (!init) {
        		mode = ModeConst.NILL;
            	alert.close();
            	Event.fireEvent(mi_close, new ActionEvent());
        	}
        	return;
        }
        while (true) {
        	 if (dirPath == null) {
        		handleChooseMode();
             	break;
             }
        	 if (!dirPath.trim().isEmpty()) {
             	alert.close();
             	switch (mode) {
	             	case ModeConst.INDEX:
	             		IndexWriter writer = new IndexWriter(dirPath);
	                    writer.buildIndex();
	                    Alert info = new Alert(AlertType.INFORMATION);
	                    info.setTitle("Information: Indexing");
	                    info.setHeaderText(null);
	                    info.setContentText("Indexing has completed successfully.");//, Now exiting.
	                    info.showAndWait();
	                    handleChooseMode();
//	                    handleClose(new ActionEvent());
	             		break;
	             	case ModeConst.QUERY:
	             		break;
	             	case ModeConst.BOOLEAN:
	             		diskIndex = new DiskInvertedIndex(dirPath);
	             		break;
	             	case ModeConst.DEFAULT:
//	             		diskIndex = new DiskInvertedIndex(dirPath);
//	             		break;
	             	case ModeConst.TRADITIONAL:
//	             		diskIndex = new DiskInvertedIndex(dirPath);
//	             		break;
	             	case ModeConst.OKAPI:
//	             		diskIndex = new DiskInvertedIndex(dirPath);
//	             		break;
	             	case ModeConst.WACKY:
	             		// default, traditional, okapi, wacky
	             		// all will fall through to this base case.
	             		diskIndex = new DiskInvertedIndex(dirPath);
	             		break;
             	}
             	 init = true;
             	break;
             } else {
            	 dirPath = handleDirChooser();
             }
        }
    }
    
    public int handleChooseQueryType(){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Choose Query Type");
        alert.setHeaderText(null);
        alert.setContentText("Choose your option.");

        ButtonType bool = new ButtonType("Boolean");
        ButtonType ranked = new ButtonType("Ranked");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bool, ranked, buttonTypeCancel);
        alert.initOwner(stage);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == bool){
        	return ModeConst.BOOLEAN;
        } else if (result.get() == ranked) {
//        	return ModeConst.RANKED;
        	return handleChooseQueryRankedType();
        } else {
        	 // ... user chose CANCEL or closed the dialog
         	return ModeConst.NILL;
        }
    }
    
    public int handleChooseQueryRankedType(){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Choose Ranked Query Type");
        alert.setHeaderText(null);
        alert.setContentText("Choose your option.");

        ButtonType defult = new ButtonType("Default");
        ButtonType traditional = new ButtonType("Traditional");
        ButtonType okapi = new ButtonType("Okapi");
        ButtonType wacky = new ButtonType("Wacky");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(defult, traditional, okapi, wacky, buttonTypeCancel);
        alert.initOwner(stage);
        Optional<ButtonType> result = alert.showAndWait();
        switch (result.get().getText()) {
        case "Default":
        	return ModeConst.DEFAULT;
//        	break;
        case "Traditional":
        	return ModeConst.TRADITIONAL;
//        	break;
        case "Okapi":
        	return ModeConst.OKAPI;
//        	break;
        case "Wacky":
        	return ModeConst.WACKY;
//        	break;
        default:
        	return ModeConst.NILL;
//        	break;
        }
//        if (result.get() == defult){
//        	
//        } else if (result.get() == traditional) {
//        	
//        } else if (result.get() == okapi) {
//        	
//        } else if (result.get() == wacky) {
//        	
//        } else {
//         	
//        }
    }
    
}
