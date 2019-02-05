package mjgv.shooter.game.display.components;

public class GoalLevel {
	private int positionY;

	private float speed;

	private int score;

	private int timeInc;

	private int levelHeight;

	private int levelIndex;

	public GoalLevel(int levelIndex, int positionY, int levelHeight, float speed, int score, int timeInc) {
		super();
		this.levelIndex = levelIndex;
		this.positionY = positionY;
		this.levelHeight = levelHeight;
		this.speed = speed;
		this.score = score;
		this.timeInc = timeInc;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTimeInc() {
		return timeInc;
	}

	public void setTimeInc(int timeInc) {
		this.timeInc = timeInc;
	}

	public int getLevelHeight() {
		return levelHeight;
	}

	public void setLevelHeight(int levelHeight) {
		this.levelHeight = levelHeight;
	}

	public int getLevelIndex() {
		return levelIndex;
	}

}
