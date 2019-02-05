package mjgv.shooter.game.display.components;

public class Goal extends DrawableItem {

	/** The identifier */
	private long identifier;

	private GoalLevel level;

	private boolean hit = false;

	private GoalType goalType;

	public Goal(GoalLevel goalLevel, GoalType goalType) {
		super(goalType.getIcon(), goalType.getPositionX(), goalLevel.getPositionY());
		this.level = goalLevel;
		this.identifier = System.currentTimeMillis();
		this.goalType = goalType;
		this.setLastTimeRendered(identifier);

		// Scale
		setItemWidth(level.getLevelHeight() * getItemWidth() / getItemHeight());
		setItemHeight(level.getLevelHeight());

		setPositionX(getPositionX() - (goalType.getOrientation() * getItemWidth()));
	}

	@Override
	public Long getIdentifier() {
		return identifier;
	}

	@Override
	public void applyTime() {
		long now = System.currentTimeMillis();
		Long timeElapsed = now - this.getLastTimeRendered();

		Float increment = (goalType.getOrientation() * timeElapsed.intValue() * level.getSpeed());

		if (Math.abs(increment) > 0) {
			this.setPositionX(this.getPositionX() + increment.intValue());
			this.setLastTimeRendered(now);
		}
	}

	public GoalLevel getLevel() {
		return level;
	}

	public GoalType getGoalType() {
		return goalType;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	// @Override
	// public void draw(Canvas canvas) {
	// // TODO Auto-generated method stub
	// super.draw(canvas);
	// Paint paint = new Paint();
	// paint.setStyle(Style.STROKE);
	// paint.setColor(Color.RED);
	// canvas.drawRect(this.getDrawableItem().getBounds(), paint);
	// paint.setColor(Color.GREEN);
	// Rect rect = new Rect(getPositionX() - getItemWidth() / 2, getPositionY()
	// - getItemHeight() / 2, getPositionX()
	// + getItemWidth() / 2, getPositionY() + getItemHeight() / 2);
	// canvas.drawRect(rect, paint);
	// }

}
