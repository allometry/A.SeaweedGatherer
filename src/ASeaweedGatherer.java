import java.util.Map;

import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSTile;

public class ASeaweedGatherer extends Script {
	private int seaweedItemID = 401, sodaAshItemID = 1781;
	private int peerTheSeerNPCID = 1288;
	private int groundFireObjectID = 14172;
	
	private RSTile groundFireTile = new RSTile(2724, 3728);
	
	@Override
	public boolean onStart(Map<String,String> args) {
		return true;
	}

	@Override
	public int loop() {
		if(!isInventoryFull() && getNearestGroundItemByID(seaweedItemID) != null) {
			
		}
	
		if(isInventoryFull()) {
			if(getInventoryCountExcept(sodaAshItemID) > 0) {
				//Setup expectation
				//While expectation hasn't been met
				//Use item on fire at tile (needs to be an object)
				//Wait 1.5 - 2.0 seconds
			} else {
				
			}
		}
		
		return 1;
	}
	
	@Override
	public void onFinish() {
		return ;
	}
}
