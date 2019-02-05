package mjgv.shooter.game.display.components;

import android.graphics.drawable.Drawable;

public class GoalType {

	public static final int LEFT_TO_RIGHT = 1;

	public static final int RIGHT_TO_LEFT = -1;

	public static final int NORMAL = 1;

	public static final int LIFE = 2;

	public static final int ERROR = 3;

	private Drawable icon;

	private int positionX;

	private int orientation;

	private int type;

	private int coef;

	public GoalType(Drawable icon, int positionX, int orientation, int type, int coef) {
		super();
		this.icon = icon;
		this.positionX = positionX;
		this.orientation = orientation;
		this.type = type;
		this.coef = coef;
	}

	public Drawable getIcon() {
		return icon;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getOrientation() {
		return orientation;
	}

	public int getType() {
		return type;
	}

	public int getCoef() {
		return coef;
	}

}
