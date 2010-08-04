package animation;
import java.awt.Graphics2D;

public class AnimationFramework {
	private int fps;
	private long clock = 0, frames;
	
	public enum AnimationSpeed { 
		SLOW(3), NORMAL(2), FAST(1);
		
		private int seconds;
		
		private AnimationSpeed(int seconds) {
			this.seconds = seconds;
		}
		
		public int getSeconds() {
			return seconds;
		}
	};
	
	private Graphics2D g;
	
	public AnimationFramework() {
		
	}
	
	public AnimationFramework(Graphics2D g) {
		this.g = g;
	}
	
	public void startClock() {
		if(clock == 0) clock = System.currentTimeMillis();
	}
		
	private int clockSeconds() {
		try {
			return (int)(System.currentTimeMillis() - clock) / 1000;
		} catch(ArithmeticException e) {
			return 1;
		}
	}
	
	public void tick() {
		frames++;
		
		fps = (int)frames / clockSeconds();
	}
	
	public int getFPS() {
		return fps;
	}
	
	public long getFrames() {
		return frames;
	}
	
	public Graphics2D getGraphics() {
		return g;
	}
	
	public void setGraphics(Graphics2D g) {
		this.g = g;
	}
}
