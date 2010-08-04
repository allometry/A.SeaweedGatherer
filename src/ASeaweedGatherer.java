import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Allometry" }, category = "1", name = "A. Seaweed Gatherer", version = 1.0,
		description = "" +
				"<html>" +
				"<head>" +
				"<style type=\"text/css\">" +
				"body {background: #000 url(http://scripts.allometry.com/app/webroot/img/gui/window.jpg);" +
				"font-family: Georgia, 'Times New Roman', Times, serif;" +
				"font-size: 12px;font-weight: normal;" +
				"padding: 50px 10px 45px 10px;}" +
				"</style>" +
				"</head>" +
				"<body>" +
				"<p>Allometry Humidifier</p>" +
				"<p>Supports Fire, Water and Steam staffs</p>" +
				"<p>Astrals in inventory. Empty vials visible in bank!</p>" +
				"<p>For more info, visit the" +
				"thread on the RuneDev forums!</p>" +
				"</body>" +
				"</html>")
public class ASeaweedGatherer extends Script implements PaintListener, ServerMessageListener {
	//messages
	//You burn the seaweed to soda ash.
	private boolean isThreadsRunning = true;
	
	private int seaweedItemID = 401, sodaAshID = 1781;
	private int peerTheSeerNPCID = 1288;
	
	private long timeout, startTime;
	
	private enum States { GATHERING_SEAWEED, COOKING_SEAWEED_INTERFACE, COOKING_SEAWEED, WALKING_TO_SEER, BANKING_SEER_INTERFACE, BANKING_SEER, WALKING_TO_SEAWEED };
	
	private ArrayList<String> statusLog = new ArrayList<String>();
	
	private RSArea seaweedArea = new RSArea(new RSTile(2692, 3721), new RSTile(2728, 3734));
	
	private RSInterfaceChild cookingSeaweedInterface, bankingSeerInterface;
	
	private RSTile groundFireTile = new RSTile(2724, 3728);
	private RSTile[] pathSeaweed = {new RSTile(2646, 3665), new RSTile(2659, 3656), new RSTile(2666, 3642), new RSTile(2681, 3635), new RSTile(2694, 3633), new RSTile(2703, 3643), new RSTile(2707, 3656), new RSTile(2718, 3666), new RSTile(2718, 3678), new RSTile(2716, 3691), new RSTile(2716, 3699), new RSTile(2712, 3713), new RSTile(2718, 3721), new RSTile(2723, 3730)};
	
	private RSArea zoneBank;
	private RSArea zoneSeaweed = new RSArea(new RSTile(2692, 3721), new RSTile(2728, 3734));
	
	private States state;
	private StateMonitor stateMonitor;
	private Thread stateThread;
	
	@Override
	public boolean onStart(Map<String,String> args) {
		stateMonitor = new StateMonitor();
		stateThread = new Thread(stateMonitor);
		//stateThread.start();
		
		startTime = System.currentTimeMillis();
		
		return true;
	}

	@Override
	public int loop() {
		/*
		switch(state) {
			case GATHERING_SEAWEED:
			break;
			
			case COOKING_SEAWEED_INTERFACE:
			break;
			
			case COOKING_SEAWEED:
			break;
			
			case WALKING_TO_SEER:
			break;
			
			case BANKING_SEER:
			break;
			
			case BANKING_SEER_INTERFACE:
			break;
			
			case WALKING_TO_SEAWEED:
			break;
		}
		
		if(!isInventoryFull()) {
			if(seaweedArea.contains(getMyPlayer().getLocation())) {
				RSItemTile seaweedItem = getNearestGroundItemByID(seaweedItemID);
				
				if(seaweedItem != null) {
					if(!Calculations.onScreen(Calculations.tileToScreen(seaweedItem))) {
						if(walkPathMM(generateFixedPath(seaweedItem)))
							wait(random(1000, 1500));
					} else {
						int expectedInventoryCount = getInventoryCount() + 1;
						
						timeout = System.currentTimeMillis() + 5000;
						while(getInventoryCount() != expectedInventoryCount && !timeout(timeout)) {
							atTile(seaweedItem, "Take Seaweed");
							while(getMyPlayer().isMoving()) timeout = System.currentTimeMillis() + 5000;
							
							if(getInventoryCount() != expectedInventoryCount)
								wait(random(1500, 2000));
						}
					}
				} else {
					walkPathMM(generateFixedPath(groundFireTile));
				}
			} else {
				timeout = System.currentTimeMillis() + 5000;
				while(!walkPathMM(pathSeaweed) && !timeout(timeout)) {
					while(getMyPlayer().isMoving()) timeout = System.currentTimeMillis() + 5000;
					wait(random(1500, 2000));
				}
			}
		} else {
			if(getInventoryCount(seaweedItemID) > 0) {
				RSInterfaceChild cookInterface = getInterface(513, 4);
				
				if(cookInterface.isValid()) {
					if(atInterface(cookInterface, "Cook All")) {
						timeout = System.currentTimeMillis() + 5000;
						int previousSeaweedCount = getInventoryCount(seaweedItemID);
						while(getInventoryCount(seaweedItemID) > 0 && !timeout(timeout)) {
							if(getInventoryCount(seaweedItemID) < previousSeaweedCount)
								timeout = System.currentTimeMillis() + 5000; 
						}
					}
				} else {
					if(Calculations.onScreen(Calculations.tileToScreen(groundFireTile))) {
						useItem(getInventoryItemByID(seaweedItemID), getObjectAt(groundFireTile));
						return random(1500, 2000);
					} else {
						walkPathMM(generateFixedPath(groundFireTile), 10);
						return random(2000, 3000);
					}
				}
			}
			
			if(getInventoryCount() == getInventoryCount(sodaAshID)) {
				RSNPC peerTheSeer = getNearestNPCByID(peerTheSeerNPCID);
				
				if(peerTheSeer != null && distanceTo(peerTheSeer) < 10) {
					timeout = System.currentTimeMillis() + 5000;
					RSInterfaceChild bankInterface = getInterface(11, 18);
					
					while(!bankInterface.isValid() && !timeout(timeout)) {
						atNPC(peerTheSeer, "Deposit Peer the Seer");
						bankInterface = getInterface(11, 18);
						
						wait(random(1500, 2000));
					}
					
					atInterface(bankInterface, "Deposit carried items");
					
					timeout = System.currentTimeMillis() + 5000;
					while(!isInventoryFull() && !timeout(timeout)) {
						wait(1);
					}
					
					RSInterfaceChild bankCloseInterface = getInterface(11, 15);
					
					if(bankCloseInterface.isValid())
						atInterface(bankCloseInterface, "Close");

					return random(2000, 3000);
				} else {
					timeout = System.currentTimeMillis() + 5000;
					while(!walkPathMM(reversePath(pathSeaweed), 10) && !timeout(timeout)) {
						while(getMyPlayer().isMoving()) timeout = System.currentTimeMillis() + 5000;
						wait(random(1500, 2000));
					}
				}
			}
		}
		*/
		return 1;
	}
	
	@Override
	public void serverMessageRecieved(ServerMessageEvent e) {
		return ;	
	}
	
	private long frames;
	private int fps;
	
	@Override
	public void onRepaint(Graphics g2) {
		//if(isPaused || isWelcomeScreen() || isLoginScreen()) return ;
		
		frames++;
		fps = (int) (frames / ((System.currentTimeMillis() - startTime) / 1000));
		
		Graphics2D g = (Graphics2D)g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		float x = (float) g.getDeviceConfiguration().getBounds().getCenterX() - 128, y = 46f;
				
		RoundRectangle2D scoreboard = new RoundRectangle2D.Float(
				x, 25, 256,
				10 + (16 * 5), 5, 5);

		g.setColor(new Color(0, 0, 0, 127));
		g.fill(scoreboard);
		
		Font console = new Font("Arial", Font.PLAIN, 13);
		g.setFont(console);
		
		g.setColor(Color.white);
		g.drawString("FPS: " + fps, 16, 16);
		
		drawTopGradientString(g, "Hello There!", x, y);
		
		return ;
	}
	
	private void drawTopGradientString(Graphics2D g, String aString, float x, float y) {
		GradientPaint testGradientFill = new GradientPaint(x + 5, y, new Color(255, 255, 255, 255), x + 5, y - 13f, new Color(255, 255, 255, 0), false);
		g.setPaint(testGradientFill);
		g.drawString(aString, x + 5, y);
	}
	
	@Override
	public void onFinish() {
		isThreadsRunning = false;
		while(stateThread.isAlive()) {
			wait(1);
		}
		
		stateThread = null;
		stateMonitor = null;
		
		return ;
	}
	
	private boolean timeout(long timeout) {
		return System.currentTimeMillis() > timeout;
	}
	
	private class StateMonitor implements Runnable {
		@Override
		public void run() {
			while(isThreadsRunning) {
				try {
					bankingSeerInterface = getInterface(11, 18);
					cookingSeaweedInterface = getInterface(513, 4);
					
					if(!isPaused && !isWelcomeScreen() && !isLoginScreen())
						if(bankingSeerInterface.isValid() || cookingSeaweedInterface.isValid())
							if(bankingSeerInterface.isValid())
								state = States.BANKING_SEER_INTERFACE;
							else if(cookingSeaweedInterface.isValid())
								state = States.COOKING_SEAWEED_INTERFACE;
							else
								if(zoneSeaweed.contains(getMyPlayer().getLocation()))
									if(!isInventoryFull())
										state = States.GATHERING_SEAWEED;
									else
										if(getInventoryCount() == getInventoryCount(seaweedItemID))
											state = States.COOKING_SEAWEED;
										else if(getInventoryCount() == getInventoryCount(sodaAshID))
											state = States.WALKING_TO_SEER;
										else
											state = States.COOKING_SEAWEED;
								else if(zoneBank.contains(getMyPlayer().getLocation()))
									if(isInventoryFull())
										state = States.BANKING_SEER;
									else
										state = States.WALKING_TO_SEAWEED;
								else
									if(isInventoryFull())
										state = States.WALKING_TO_SEER;
									else
										state = States.WALKING_TO_SEAWEED;
				} catch(Exception e) {}
			}
			
			return ;
		}
	}
}
