package com.mkyong.android;

import java.util.ArrayList;
import java.util.List;

import com.mkyong.android.adapter.ImageAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

/*	FIX BUGS
 * 	1. 	when i get treausure adventurer stays there
 *  2.	if i dont click on the right sequence it stops 
 *  3.	just click on character once ...then move him that is all. 
 */


public class GridViewActivity extends Activity {
	
	//Declare Variables// 
    public static final int DEFAULT_ROWS = 11;      
    public static final int DEFAULT_COLS = 6;    
	private Board gameBoard; // Underlying board game.
	private List<Character> characters; // Characters on the board.
	private int selected; // Which character is currently selected.
	private int clickStatus = 4, initialClick = 1; 
	private boolean treasureClaimed;
	
    //ADDED, VARIABLES USED TO KEEP TRACK OF MOVES
    String advMoves = ("Adventurer's moves: ");
    String minerMoves = ("Miner's moves: ");
    String fillMoves = ("Filler's moves: ");
    String totalMoves = ("Total moves in game: ");
    int advNum = 0;
    int minerNum = 0;
    int fillNum = 0;
    int total = 0;
    Character ch;

	//set up android gui//
	GridView gridView;
	public static String[] MOBILE_OS = new String[]  { 
		"Ground","Treasure", "Hat",  "Pickaxe", "Pit", "Portal", 
		"Wheelbarrow","Ground","Ground","Ground","Ground","Ground",
		"Ground", "Ground","Ground","Ground","Ground","Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"
		,"Ground","Ground","Ground","Ground","Ground", "Ground"};
	public static String[] MOBILE_tmp = new String[]  { "Ground" }; 
	public static String[] sCharacters = new String[]  { "Hat",  "Pickaxe", "Wheelbarrow"};          


	private ImageAdapter myAdapter = new ImageAdapter(this, MOBILE_OS); 
	private int clickCount = 0; 
	private int posTmp = 0; 
	private int row, col; 
	private int jumpPos; 



	//this is where all the majic happens// 
	@Override  
	public void onCreate(Bundle savedInstanceState) {
		
		//start first game// 

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//start first game// 
		newGame();
		updateGameBoard(); 
		
		//set the GUI// 
		final ImageAdapter myAdapter = new ImageAdapter(this, MOBILE_OS);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(myAdapter);


		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				row = returnRow(position); col = returnCol(position); 
				
				Toast.makeText(getApplicationContext(), "pos("+position+") :"
						, Toast.LENGTH_SHORT).show();
				if( initialClick == 1 ){//choose a character for the first time 
					MOBILE_OS[0] = "Hat"; 
					MOBILE_OS[65] = "Treasure"; 
					if(checkIfValid(position) < 3){
						posTmp = position; 					//GUI
						MOBILE_tmp[0] = MOBILE_OS[posTmp]; 	//GUI
						initialClick = 0; 
						Toast.makeText(getApplicationContext(), "Initial choice "+checkIfValid(position)
								, Toast.LENGTH_SHORT).show();	
					}
				}//choose a new character 
				else if(checkIfValid(position) < 3){
					posTmp = position; 					//GUI
					MOBILE_tmp[0] = MOBILE_OS[posTmp]; 	//GUI
					Toast.makeText(getApplicationContext(), "New choice "+checkIfValid(position)
							, Toast.LENGTH_SHORT).show();	
				}//move chosen character 
				else{
					if(validIndex(posTmp, position) == 1){
						 switch ( keyPressed(position, posTmp) ) {
				            case 0:  
				                newGame();
				                updateGameBoard();
				        		gridView.setAdapter(myAdapter);
				        		initialClick = 1; 
				                     break;
				            case 1:  
				            	MOBILE_OS[posTmp] = "Ground"; 			//GUI
								MOBILE_OS[position] = MOBILE_tmp[0]; 	//GUI
								gridView.setAdapter(myAdapter);			//GUI
				                     break;
				            case 2:  ;
				            	MOBILE_OS[posTmp] = "Ground"; 			//GUI
								MOBILE_OS[jumpPos] = MOBILE_tmp[0]; 	//GUI
								gridView.setAdapter(myAdapter);			//GUI
				                     break;
				            default: ;
				                     break;
				        }		
					}
					if( initialClick < 7){// only if we are not starting a new game
						posTmp = position; 					//GUI
						MOBILE_tmp[0] = MOBILE_OS[posTmp]; 	//GUI
					}

				}

				clickCount++;
			}//end of onItmeClick 
		});//end of onClick..
		


	}//end of onCreate 
	
	//ported from AdventureGame.java 
	/** Initialize a new underlying game. */
	private void newGame() {
		// Set up the game board.
            
                advNum = 0;
                minerNum = 0;
                fillNum = 0;
                total = 0;
                
                
		gameBoard = new Board(DEFAULT_ROWS, DEFAULT_COLS);
		
		// Set up the 3 characters.
		characters = new ArrayList<Character>();
		//[Adventure->hat],[Minor->pickaxe],[Filler->Wheelbarrow]//
		// Add the adventurer (always in the top left).
		Adventurer Adv = new Adventurer(gameBoard.getCave(0, 0));
		characters.add(Adv);
		selected = 0; // Initially select the adventurer.
		
		// Get a location for the miner and add him.
		Cave minerLoc = gameBoard.getUnoccupiedOpenLocation();
		characters.add(new Miner(gameBoard.getCave(minerLoc.getRow(), minerLoc.getCol())));
		
		// Get a location for the filler and add him.
		Cave fillerLoc = gameBoard.getUnoccupiedOpenLocation();
		characters.add(new Filler(gameBoard.getCave(fillerLoc.getRow(), fillerLoc.getCol())));
		
		// We seek the treasure!
		treasureClaimed = false;
		
		//updateStatus("Welcome! Select characters with the mouse (or just use TAB) " +
		//		"and use the arrow keys to move.");
	}//end of newGame method 
	
	
	/** Update the visible state of the game board based on the internal state of the game. */
	private void updateGameBoard() {
		
		int arrayPos; 
		
		//"Ground","Treasure", "Hat",  "Pickaxe", "Pit", "Portal", 
		//"Wheelbarrow","Ground","Ground","Ground","Ground","Ground",
            
		// Put icons on the board.
		for (int i=0; i<DEFAULT_ROWS; ++i){
			for (int j=0; j<DEFAULT_COLS; ++j) {
				Cave c = gameBoard.getCave(i, j);
				String img = "";
				if (c.isBlocked())
					img ="Cave"; //should be cave.jpg
				else if (c.isPit())
					img ="Pit";
				else if (c.isTeleport() && c.isMarked())
					img ="Portal";
				else // open OR teleport and not marked
					img ="Ground";
                 
				arrayPos = singleArrayPosition(j, i);
				MOBILE_OS[arrayPos] = img; 
				//grid[i][j].setIcon(img);
				//grid[i][j].setBorder(BorderFactory.createBevelBorder(1));
			}
		}
         
		//do this laterif it is i even need to do this
		// Show the characters. Highlight the selected one.
		
		//int idx=0;
		for (Character ch : characters) {
			Cave c = ch.getLocation();
			if (ch instanceof Adventurer){
				arrayPos = singleArrayPosition(c.getCol(), c.getRow() ); 
				MOBILE_OS[arrayPos] = "Hat"; 
			}
			else if (ch instanceof Miner){
				arrayPos = singleArrayPosition(c.getCol(), c.getRow() ); 
				MOBILE_OS[arrayPos] = "Pickaxe"; 
			}
			else if (ch instanceof Filler){
				arrayPos = singleArrayPosition(c.getCol(), c.getRow() ); 
				MOBILE_OS[arrayPos] = "Wheelbarrow"; 
			}
	
		}
		
		// Show the treasure if it's not already claimed.
		if (!treasureClaimed){
			arrayPos = singleArrayPosition(DEFAULT_COLS - 1, DEFAULT_ROWS);
			MOBILE_OS[65] = "Treasure"; 
		}
                
	}//end of update gameBoard
	
	private int singleArrayPosition(int col, int row){
		int newInt; 
		newInt = 6*row + col ;
		
		return newInt; 
	}	
	
	private int returnRow(int position){
		return position/6; 
	}
	
	private int returnCol(int position){
		int row; 
		row = returnRow(position); 
		
		return position - 6*row; 
	}
	
	//returns 1 if valid, returns 0 if invalid; 
	private int validIndex(int position1, int position2){
		int row1, col1, row2, col2;
		row1 = returnRow(position1); col1 = returnCol(position1); 
		row2 = returnRow(position2); col2 = returnCol(position2); 
		
		if( Math.abs( row1 - row2 ) > 1 || Math.abs( col1 - col2) > 1)
			return 0; 
		else
			return 1; 
	}
	
	/*return 0	= invalid move or death of a character 
	 * 		 1  = valid move 
	 * 		 2  = portal jump 
	 */
	public int keyPressed(int position, int tmpPos) {
		
		// Get the direction of movement.
		int dr=0,dc=0;
		dr = returnRow(position) - returnCol(tmpPos); //-1up, 1down
		dc = returnCol(position) - returnCol(tmpPos); //-1left, 1right
		
		selected = getSelected(); 
		// Set Up to make the move.
		ch = characters.get(selected); 	//get the selected character
		Cave c = ch.getLocation();		//get the character location
		Cave newC = gameBoard.getCave(returnRow(position), returnCol(position) ); //get the cave obj at new pos
		
		//get down to business
		// Make sure only the adventurer moves to the treasure.
		if (newC.getRow()==DEFAULT_ROWS-1 && newC.getCol()==DEFAULT_COLS-1 &&
				!(ch instanceof Adventurer)) {
			Toast.makeText(getApplicationContext(), "Only the adventurer can claim the treasure!"
					, Toast.LENGTH_SHORT).show();	
			return 0; 
		}

		// Try and make the move.
		else if (ch.move(newC)) {
                        
                            if(ch.getName().equals("Adventurer")){  //ADDED, THIS INCREMENTS THE ADVENTURER'S MOVES
                                advNum++;
                            }
                            
                            if(ch.getName().equals("Miner")){       //ADDED, THIS INCREMENTS THE MINER'S MOVES
                                minerNum++;
                            }
                            
                            if(ch.getName().equals("Filler")){      //ADDED, THIS INCREMENTS THE FILLER'S MOVES
                                fillNum++;
                            }
                          
			
			// Try and modify this cave is possible.
			CaveWorker cw = ch;
			if (cw.modifyCave(newC)) {
				updateGameBoard();
				Toast.makeText(getApplicationContext(), ch.getName()+" successfully moved and "+
						cw.describeModification()+"!", Toast.LENGTH_SHORT).show();
			}
                            
                            
                            //ADDED, THIS CHECKS IF THE ADVENTURER HAS A ROPE AND IF HE IS TRYING TO CROSS A PIT
                            else if((ch.useRope()) && (ch.getName().equals("Adventurer")) && (newC.isPit())){
                                
                                Cave jumpRope = gameBoard.getCave(c.getRow()+dr+dr, c.getCol()+dc+dc);
                                
                                //ADDED, THIS CHECKS IF THE PLAYER IS WITHIN THE BOARD AND THAT THERE ARE NO OBSTACLES IN THE WAY
                                //deleted gameBoard.ok(c.getRow()+dr+dr,c.getCol()+dc+dc)&& from parameters
                                if((!(jumpRope.isBlocked()||jumpRope.isOccupied()||jumpRope.isPit()))){
                                
                                        ch.move(jumpRope);
                                    
                                        ch.setRope(0);
                                        
                                        /*c = ch.getLocation();
                                        
                                        grid[c.getRow()][c.getCol()].setIcon("icons64/hat.png");*/
                                        
                                        updateGameBoard();
                                }
                                
                                else { // This character falls in the pit and dies.
                    					Toast.makeText(getApplicationContext(), ch.getName()+" fell in the pit and died!"
                    						, Toast.LENGTH_SHORT).show();
                                        newC.setOccupied(false);
                                        characters.remove(selected);
                                        selected%=characters.size();
                                        updateGameBoard();
                                    
				if (ch instanceof Adventurer) {
					if (!treasureClaimed) {
    					Toast.makeText(getApplicationContext(),  ch.getName()+" is now dead :( " +
								"No way to get the treausre now. Better luck next time!"
        						, Toast.LENGTH_SHORT).show();
					}
					else {
    					Toast.makeText(getApplicationContext(), ch.getName()+" is now dead :( " +
								"She fell in the pit and took all the treasure with her!"
        						, Toast.LENGTH_SHORT).show();
                                            }
                                    }
								return 0; 
                                }//end of "This character falls in the pit and dies."
                            }
                            
                            
			else if (newC.isPit()) { // This character falls in the pit and dies.
				Toast.makeText(getApplicationContext(), ch.getName()+" fell in the pit and died!"
						, Toast.LENGTH_SHORT).show();
				newC.setOccupied(false);
				characters.remove(selected);
				selected%=characters.size();
				updateGameBoard();
                                    
				if (ch instanceof Adventurer) {
					if (!treasureClaimed) {
						Toast.makeText(getApplicationContext(), ch.getName()+" is now dead :( " +
								"No way to get the treausre now. Better luck next time!"
								, Toast.LENGTH_SHORT).show();
					}
					else {
						Toast.makeText(getApplicationContext(), ch.getName()+" is now dead :( " +
								"She fell in the pit and took all the treasure with her!"
								, Toast.LENGTH_SHORT).show();
					}
				}
				return 0; 
			}//end of "This character falls in the pit and dies."
			else if (newC.isTeleport()) { // Transport this character to a random location.
				Cave randomLoc = gameBoard.getUnoccupiedOpenLocation();
				ch.move(randomLoc); // Guaranteed to return true.
				updateGameBoard();
				//return single array pos 
				jumpPos = singleArrayPosition( randomLoc.getCol(), randomLoc.getRow() ); 
				Toast.makeText(getApplicationContext(), ch.getName()+" was teleported to a mystery cave!"
						, Toast.LENGTH_SHORT).show();
				return 2; 
				
			}
			else {
				Toast.makeText(getApplicationContext(), ch.getName()+" successfully moved!"
						, Toast.LENGTH_SHORT).show();
				updateGameBoard();
			}
			
			if (!treasureClaimed && newC.getRow()==DEFAULT_ROWS-1 && 
					newC.getCol()==DEFAULT_COLS-1 && ch instanceof Adventurer) {
				treasureClaimed = true;
				updateGameBoard();
				//altered much here 
				Toast.makeText(getApplicationContext(), ch.getName()+" has claimed the treasure!" +
						" Fame and fortune are now yours!"
						, Toast.LENGTH_SHORT).show();
                                    
                newGame();
                updateGameBoard();
                return 1;
			}
		}


		return 1; 
	}
	/* returns 	0 = hat
	 * 			1 = pickaxe
	 * 			2 = wheelbarrow 
	 * 			3 = other
	 */
	public int getSelected(){
		if(MOBILE_tmp[0] == sCharacters[0])
			return 0; 	
		else if(MOBILE_tmp[0] == sCharacters[1])
			return 1; 
		else if(MOBILE_tmp[0] == sCharacters[2])
			return 2; 
		return 3; 
	}
	public int checkIfValid(int position){
		if(MOBILE_OS[position] == sCharacters[0])
			return 0; 	
		else if(MOBILE_OS[position] == sCharacters[1])
			return 1; 
		else if(MOBILE_OS[position] == sCharacters[2])
			return 2; 
		return 3; 
	}

}//end of class 