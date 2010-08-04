package animation;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class AnimationFade extends AnimationFramework {
	private boolean finishedAnimation = false;
	private int frame = 0;
	
	private ArrayList<AnimationFrame> slowFadeInFrames;
	private boolean slowFadeInFramesBuilt = false;
	
	private ArrayList<AnimationFrame> slowFadeOutFrames;
	private boolean slowFadeOutFramesBuilt = false;
	
	public AnimationFade() {
		slowFadeInFrames = new ArrayList<AnimationFrame>();
		slowFadeOutFrames = new ArrayList<AnimationFrame>();
	}
	
	public AnimationFade(Graphics2D g) {
		super(g);
		
		slowFadeInFrames = new ArrayList<AnimationFrame>();
		slowFadeOutFrames = new ArrayList<AnimationFrame>();
	}
	
	public void createSlowFadeInFrameIndex() {
		int maximumFrames = super.getFPS() * AnimationSpeed.SLOW.getSeconds();
		int alphaChannel = super.getGraphics().getColor().getAlpha();
		int deltaAlpha = alphaChannel / maximumFrames;
		
		Color tmpColor;
		
		for(int ixIndex = 0; ixIndex <= maximumFrames; ixIndex++) {
			tmpColor = new Color(
				super.getGraphics().getColor().getRed(),
				super.getGraphics().getColor().getGreen(),
				super.getGraphics().getColor().getBlue(),
				alphaChannel);
			
			slowFadeInFrames.add(new AnimationFrame(tmpColor));
			
			alphaChannel += deltaAlpha;
			if(alphaChannel + deltaAlpha > 255) break;
		}
		
		slowFadeInFramesBuilt = true;
	}
	
	public void createSlowFadeOutFrameIndex() {
		int maximumFrames = super.getFPS() * AnimationSpeed.SLOW.getSeconds();
		int alphaChannel = super.getGraphics().getColor().getAlpha();
		int deltaAlpha = alphaChannel / maximumFrames;
		
		Color tmpColor;
		
		for(int ixIndex = 0; ixIndex <= maximumFrames; ixIndex++) {
			tmpColor = new Color(
				super.getGraphics().getColor().getRed(),
				super.getGraphics().getColor().getGreen(),
				super.getGraphics().getColor().getBlue(),
				alphaChannel);
			
			slowFadeOutFrames.add(new AnimationFrame(tmpColor));
			
			alphaChannel -= deltaAlpha;
			if(alphaChannel - deltaAlpha < 0) break;
		}
		
		slowFadeOutFramesBuilt = true;
	}
	
	public void gotoBeginningAndReset() {
		frame = 0;
		finishedAnimation = false;
	}
	
	public boolean fadeIn() {
		if(finishedAnimation) return true;
		
		super.getGraphics().setColor(slowFadeInFrames.get(frame).getColor());
		
		if(frame + 1 >= slowFadeInFrames.size()) {
			finishedAnimation = true;
			return true;
		} else {
			frame++;
		}
		
		return false;
	}
	
	public boolean fadeOut() {
		if(finishedAnimation) return true;
		
		super.getGraphics().setColor(slowFadeOutFrames.get(frame).getColor());
		
		if(frame + 1 >= slowFadeOutFrames.size()) {
			finishedAnimation = true;
			return true;
		} else {
			frame++;
		}
		
		return false;
	}
}
