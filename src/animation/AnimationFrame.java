package animation;
import java.awt.Color;
import java.awt.Point;

public class AnimationFrame {
	private Color color;
	private Point position;
	
	public AnimationFrame(Color color) {
		this.color = color;
	}
	
	public AnimationFrame(Color color, Point position) {
		this.color = color;
		this.position = position;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Point getPosition() {
		return position;
	}
}
