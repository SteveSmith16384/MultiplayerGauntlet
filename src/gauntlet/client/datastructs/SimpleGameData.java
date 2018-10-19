package gauntlet.client.datastructs;

import java.util.ArrayList;

import mgs2.shared.GameStage;

public class SimpleGameData {

	public int id;
	public GameStage game_stage;
	public String game_name;
	public ArrayList<SimplePlayerData> players = new ArrayList<SimplePlayerData>(); 

	
	public SimpleGameData() {
	}


	public void setSimplePlayerData(SimplePlayerData data) {
		this.players.add(data);
	}


}
