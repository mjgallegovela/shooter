package mjgv.shooter.game.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import mjgv.shooter.android.R;
import mjgv.shooter.game.display.android.layout.PlayActivity;
import mjgv.shooter.game.display.android.playview.ShootSurfaceView;
import mjgv.shooter.game.display.components.BackgroundImage;
import mjgv.shooter.game.display.components.Goal;
import mjgv.shooter.game.display.components.GoalLevel;
import mjgv.shooter.game.display.components.GoalType;
import mjgv.shooter.game.display.components.Pointer;
import mjgv.shooter.game.display.components.Shoot;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.TextView;

public class GameHandler {

	private static final int LAST_GOALS_SIZE = 3;

	private static final int NUMBER_OF_LEVELS = 5;

	private GameHandler() {
		super();
	}

	// Singleton instance
	private static GameHandler singletonGH = null;

	public static GameHandler getGameHandler() {
		if (singletonGH == null) {
			singletonGH = new GameHandler();
		}
		return singletonGH;
	}

	/*
	 * --------------------------------------------------------------------------
	 */
	/* Time */
	private int countDown = 3;
	private int timeRemaining = 0;

	/* Score */
	private int score = 0;

	/* Acelerometer */
	private Float initialPositionX = null;
	private Float initialPositionY = null;

	/* Screen Size */
	private int frameWidth = 0;
	private int frameHeight = 0;

	/* Game */
	private boolean gameOn = false;

	/*
	 * --------------------------------------------------------------------------
	 */

	private Map<Integer, GoalType> goalTypes = new HashMap<Integer, GoalType>();

	/** The drawable to use as the background of the animation canvas */
	private BackgroundImage backgroundImage;

	private Pointer pointer;

	private List<GoalLevel> levels = new ArrayList<GoalLevel>();

	private List<List<Goal>> ducksOnGame = new ArrayList<List<Goal>>();

	private List<Shoot> shoots = new LinkedList<Shoot>();

	private Queue<Goal> lastGoals = new LinkedList<Goal>();

	private float imagesScaleX;

	private float imagesScaleY;

	/* Configurable parameters */

	private int shootTime = 0;

	private int levelIncScore;

	private int levelIncTime;

	private int duckTime;

	private int initialTime;

	private float initialVariationTh;

	private float variationThreshold;

	private float goalLifeProbability;

	private float goalErrorProbability;

	private int goalLifeCoef;

	private int goalErrorCoef;

	private void doLoadConfiguration() {
		shootTime = activity.getResources().getInteger(R.integer.shootTime);
		initialTime = activity.getResources().getInteger(R.integer.initialTime);
		levelIncTime = activity.getResources().getInteger(R.integer.levelIncTime);
		levelIncScore = activity.getResources().getInteger(R.integer.levelIncScore);
		duckTime = activity.getResources().getInteger(R.integer.duckTime);
		initialVariationTh = activity.getResources().getFraction(R.fraction.pointerSensitiveThreashold, 1, 1);
		variationThreshold = initialVariationTh;
		goalLifeProbability = activity.getResources().getFraction(R.fraction.goalLifeProbability, 1, 1);
		goalLifeCoef = activity.getResources().getInteger(R.integer.goalLifeCoef);
		goalErrorProbability = 1f - activity.getResources().getFraction(R.fraction.goalErrorProbability, 1, 1);
		goalErrorCoef = activity.getResources().getInteger(R.integer.goalErrorCoef);
	}

	/*
	 * --------------------------------------------------------------------------
	 */
	private ShootSurfaceView view;

	private GameThread thread;

	private PlayActivity activity;

	/*
	 * --------------------------------------------------------------------------
	 */

	private int gameRealTimeElapsed = 0;

	private float pointerIncrementTimeLevel = 0.001f;

	public OnChronometerTickListener doCreateTimeListener() {
		return new OnChronometerTickListener() {

			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (countDown > 0) {
					countDown--;
					activity.showCountDown(countDown);

				} else if (countDown <= 1 && !gameOn) {
					doStartGame(activity);
				} else {
					// disminuye el tiempo
					timeRemaining--;

					int currentTenTimeElapsed = gameRealTimeElapsed / 10;
					gameRealTimeElapsed++;
					if (gameRealTimeElapsed / 10 > currentTenTimeElapsed) {
						variationThreshold += pointerIncrementTimeLevel;
					}

					// Creating new goal
					doCreateNewGoal();
					int color = R.color.lblTextColorNormal;

					// Last 10 secods advertisement
					if (timeRemaining < 10) {
						color = R.color.lblTextColorWarning;
						if (timeRemaining <= 0) {
							chronometer.stop();
							doGameOver();
						}
					}
					activity.showRemainingTime(String.valueOf(timeRemaining), color);
				}
			}

		};
	}

	private void doStartGame(PlayActivity activity) {
		activity.hideCountDown();
		this.timeRemaining = initialTime;
		this.gameOn = true;
		this.gameRealTimeElapsed = 0;
	}

	public void doCreateNewGame() {
		this.countDown = 3;
		this.score = 0;
		this.activity.showScore(score);
		this.initialPositionX = null;
		this.initialPositionY = null;
		((TextView) activity.findViewById(R.id.lblOverShootArea)).setText(String.valueOf(countDown));
		((TextView) activity.findViewById(R.id.lblOverShootArea)).setTextSize(30);

		ducksOnGame.clear();
		shoots.clear();
		for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
			ducksOnGame.add(new LinkedList<Goal>());
		}

		Chronometer chrLeftTime = (Chronometer) activity.findViewById(R.id.chrTimeElapsed);
		chrLeftTime.start();
	}

	public void doCreateBackground() {
		/* The drawable to use as the background of the animation canvas */
		Bitmap mBackgroundImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background);
		int width = mBackgroundImage.getWidth();
		int height = mBackgroundImage.getHeight();

		this.imagesScaleX = Float.valueOf(width).floatValue() / Float.valueOf(this.frameWidth).floatValue();
		this.imagesScaleY = Float.valueOf(height).floatValue() / Float.valueOf(this.frameHeight).floatValue();

		mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, this.frameWidth, this.frameHeight, true);

		this.backgroundImage = new BackgroundImage(mBackgroundImage);
	}

	public void doExecGameCycle() {
		if (gameOn) {
			pointer.applyTime();
			List<Goal> finishedGoals = new LinkedList<Goal>();
			synchronized (this) {
				for (List<Goal> level : ducksOnGame) {
					for (Goal goal : level) {
						goal.applyTime();
						if (isFinished(goal) || goal.isHit()) {
							finishedGoals.add(goal);
						}
					}
					if (!finishedGoals.isEmpty()) {
						level.removeAll(finishedGoals);
						finishedGoals.clear();
					}

					List<Shoot> finishedShoots = new LinkedList<Shoot>();
					for (Shoot shoot : shoots) {
						shoot.applyTime();
						if (shoot.isFinished()) {
							finishedShoots.add(shoot);
						}
					}
					if (!finishedShoots.isEmpty()) {
						shoots.removeAll(finishedShoots);
					}
				}
			}
		}
		doDraw();
	}

	private Goal doCheckShoot(int shootX, int shootY, int levelIndex) {
		for (Goal goal : ducksOnGame.get(levelIndex)) {

			int goalX1 = goal.getPositionX() - goal.getItemWidth() / 2;
			int goalX2 = goal.getPositionX() + goal.getItemWidth() / 2;

			int goalY1 = goal.getPositionY() - goal.getItemHeight() / 2;
			int goalY2 = goal.getPositionY() + goal.getItemHeight() / 2;

			if (!goal.isHit() && goalX1 <= shootX && goalX2 >= shootX && goalY1 <= shootY && goalY2 >= shootY) {
				goal.setHit(true);
				return goal;
			}
		}
		return null;
	}

	private boolean isFinished(Goal goal) {
		if (goal.getGoalType().getOrientation() > 0) {
			return (goal.getPositionX() - goal.getItemWidth() / 2) > frameWidth;
		}
		return goal.getPositionX() < (goal.getItemWidth() / -2);
	}

	private long lastCursorUpdate = System.currentTimeMillis();

	private long cursorUpdateInterval = 100;

	public void doUpdatePointerPosition(float lastPositionX, float lastPositionY) {
		if (System.currentTimeMillis() - lastCursorUpdate >= cursorUpdateInterval) {
			lastCursorUpdate = System.currentTimeMillis();
			Log.i("pointer", "new position: " + lastPositionX + " " + lastPositionY);
			if (pointer != null) {
				float speedY = 0;
				float speedX = 0;

				if (initialPositionX == null) {
					initialPositionX = lastPositionX;
				} else {
					speedX = (lastPositionX - initialPositionX) * variationThreshold;
				}

				if (initialPositionY == null) {
					initialPositionY = lastPositionY;
				} else {
					speedY = (lastPositionY - initialPositionY) * variationThreshold;
				}

				pointer.setNewSpeed(speedX, speedY);
			}
		}
	}

	private void doGameOver() {
		this.doClearCurrentGame();
		this.activity.doNotifyGameOver();
	}

	private void doClearCurrentGame() {
		gameOn = false;
		this.shoots.clear();
		this.ducksOnGame.clear();
		this.lastGoals.clear();
	}

	public void doDraw() {

		Canvas canvas = null;
		try {

			canvas = getSurfaceHolder().lockCanvas(null);
			if (canvas != null) {
				synchronized (getSurfaceHolder()) {
					// Imagen de fondo
					if (getBackgroundImage() != null) {
						getBackgroundImage().draw(canvas);
					}

					if (gameOn) {
						synchronized (this) {
							for (List<Goal> level : ducksOnGame) {
								for (Goal goal : level) {
									goal.draw(canvas);
								}
							}

							for (Shoot shoot : shoots) {
								shoot.draw(canvas);
							}
						}
						// Puntero
						getPointer().draw(canvas);
					}
				}
			} else {
				doKillPaintingThread();
			}
		} finally {
			// do this in a finally so that if an exception is thrown
			// during the above, we don't leave the Surface in an
			// inconsistent state
			if (canvas != null) {
				getSurfaceHolder().unlockCanvasAndPost(canvas);
			}
		}

	}

	public void doLinkSurfaceView(ShootSurfaceView view) {

		Log.i("GameHandlerConfiguration", "Linking the SurfaceView to the GameHandler");

		this.view = view;
		setCanvasSizeX(getSurfaceHolder().getSurfaceFrame().width());
		setCanvasSizeY(getSurfaceHolder().getSurfaceFrame().height());

		Log.i("GameHandlerConfiguration", "Creating the background and the pointer");
		doCreateBackground();
		doCreatePointer();
		doCreateGoalLevels();
		doCreateGoalTypes();
		doDraw();

		Log.i("GameHandlerConfiguration", "Creating and starting the thread");
		thread = new GameThread();
		thread.setRunning(true);
		thread.start();
	}

	private void doCreateGoalTypes() {
		this.goalTypes.clear();

		GoalType normalGoalLeft = new GoalType(getDrawable(R.drawable.goal_normal_left), 0, GoalType.LEFT_TO_RIGHT,
				GoalType.NORMAL, 1);
		GoalType normalGoalRight = new GoalType(getDrawable(R.drawable.goal_normal_right), this.frameWidth,
				GoalType.RIGHT_TO_LEFT, GoalType.NORMAL, 1);
		GoalType lifeGoalLeft = new GoalType(getDrawable(R.drawable.goal_life_left), 0, GoalType.LEFT_TO_RIGHT,
				GoalType.LIFE, goalLifeCoef);
		GoalType lifeGoalRight = new GoalType(getDrawable(R.drawable.goal_life_right), this.frameWidth,
				GoalType.RIGHT_TO_LEFT, GoalType.LIFE, goalLifeCoef);
		GoalType errorGoalLeft = new GoalType(getDrawable(R.drawable.goal_error_left), 0, GoalType.LEFT_TO_RIGHT,
				GoalType.ERROR, goalErrorCoef);
		GoalType errorGoalRight = new GoalType(getDrawable(R.drawable.goal_error_right), this.frameWidth,
				GoalType.RIGHT_TO_LEFT, GoalType.ERROR, goalErrorCoef);

		this.goalTypes.put(GoalType.LEFT_TO_RIGHT * GoalType.NORMAL, normalGoalLeft);
		this.goalTypes.put(GoalType.RIGHT_TO_LEFT * GoalType.NORMAL, normalGoalRight);
		this.goalTypes.put(GoalType.LEFT_TO_RIGHT * GoalType.LIFE, lifeGoalLeft);
		this.goalTypes.put(GoalType.RIGHT_TO_LEFT * GoalType.LIFE, lifeGoalRight);
		this.goalTypes.put(GoalType.LEFT_TO_RIGHT * GoalType.ERROR, errorGoalLeft);
		this.goalTypes.put(GoalType.RIGHT_TO_LEFT * GoalType.ERROR, errorGoalRight);

	}

	private GoalType getGoalType(int type, int orientation) {
		return this.goalTypes.get(type * orientation);
	}

	private Drawable getDrawable(int intResourceKey) {
		return activity.getResources().getDrawable(intResourceKey);
	}

	private void doCreatePointer() {
		Drawable drawablePointer = getDrawable(R.drawable.pointer);
		pointer = new Pointer(drawablePointer, frameWidth, frameHeight);
		pointer.setItemWidth(Float.valueOf(pointer.getItemWidth() / imagesScaleX).intValue());
		pointer.setItemHeight(Float.valueOf(pointer.getItemHeight() / imagesScaleY).intValue());
	}

	private void doCreateGoalLevels() {
		levels.clear();

		Drawable drawableIcon = getDrawable(R.drawable.goal_normal_left);
		int py = drawableIcon.getIntrinsicHeight();

		float speed = Float.valueOf(frameWidth) / Float.valueOf(duckTime * 1000);

		int halfFrameWidth = frameWidth / 2;
		int x = 0 - halfFrameWidth;
		int y = frameHeight - py;

		// First level
		GoalLevel level0 = new GoalLevel(0, y + py / 2, py, speed, levelIncScore, levelIncTime);
		levels.add(level0);
		x += py;

		int levelHeight = 0;
		for (int i = 1; i < NUMBER_OF_LEVELS; i++) {
			levelHeight = (2 * py * x) / -frameWidth;
			x += levelHeight;
			y -= levelHeight;
			GoalLevel leveln = new GoalLevel(i, y + (levelHeight / 2), levelHeight, speed, levelIncScore * (i + 1),
					levelIncTime * (i + 1));
			levels.add(leveln);
		}

	}

	public void doUnlinkSurfaceView() {
		doKillPaintingThread();
	}

	private void doKillPaintingThread() {
		// boolean retry = true;
		thread.setRunning(false);
		// while (retry) {
		// try {
		// thread.join();
		// retry = false;
		// } catch (InterruptedException e) {
		// }
		// }
	}

	public void doLinkActivity(PlayActivity playActivity) {
		this.activity = playActivity;
		doLoadConfiguration();
	}

	public OnClickListener getShootBtnListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.doNotifyShoot();
				doCreateNewShoot();
			}

		};
	}

	private void doCreateNewGoal() {

		int levelCandidate = 0;

		boolean found = false;
		while (!found) {
			levelCandidate = Long.valueOf(Math.round(Double.valueOf(Math.random() * (NUMBER_OF_LEVELS - 1))))
					.intValue();
			found = true;
			for (Goal duck : lastGoals) {
				if (duck.getLevel().getLevelIndex() == levelCandidate) {
					found = false;
					break;
				}
			}
		}

		// Direction: if more ducks are running in this level, then the new duck
		// has to run in the same direction.
		List<Goal> ducksOnLevel = ducksOnGame.get(levelCandidate);
		int orientation;
		if (ducksOnLevel.isEmpty()) {
			orientation = doCalculateOrientation();
		} else {
			orientation = ducksOnLevel.get(0).getGoalType().getOrientation();
		}
		GoalType goalType = getGoalType(doCalculateGoalType(), orientation);
		Goal newDuck = new Goal(this.levels.get(levelCandidate), goalType);

		synchronized (this) {
			ducksOnLevel.add(newDuck);
		}

		if (lastGoals.size() >= LAST_GOALS_SIZE) {
			lastGoals.poll();
		}
		lastGoals.add(newDuck);
	}

	private int doCalculateGoalType() {
		float randomValue = Double.valueOf(Math.random()).floatValue();

		if (randomValue < goalLifeProbability) {
			return GoalType.LIFE;
		}

		if (randomValue > goalErrorProbability) {
			return GoalType.ERROR;
		}

		return GoalType.NORMAL;
	}

	private int doCalculateOrientation() {
		return (Math.random() > 0.5) ? GoalType.LEFT_TO_RIGHT : GoalType.RIGHT_TO_LEFT;
	}

	private void doCreateNewShoot() {
		int shootX = this.pointer.getPositionX();
		int shootY = this.pointer.getPositionY();

		GoalLevel shootLevel = null;
		for (GoalLevel level : levels) {
			if (shootY >= (level.getPositionY() - level.getLevelHeight() / 2)) {
				shootLevel = level;
				break;
			}
		}

		int shootWidth;
		int shootHeight;
		Drawable shootIcon;

		String shootMsg = "";
		if (shootLevel != null) {

			Goal hittedGoal = null;
			if ((hittedGoal = doCheckShoot(shootX, shootY, shootLevel.getLevelIndex())) != null) {
				shootIcon = getShootHitIcon(hittedGoal.getGoalType());
				shootMsg = doSuccesfulShoot(hittedGoal);
			} else {
				shootIcon = getDrawable(R.drawable.miss);
			}

			shootWidth = shootLevel.getLevelHeight() * shootIcon.getIntrinsicWidth() / shootIcon.getIntrinsicHeight();
			shootHeight = shootLevel.getLevelHeight();

		} else {

			shootIcon = getDrawable(R.drawable.miss_air);
			shootWidth = pointer.getItemWidth();
			shootHeight = pointer.getItemHeight();

		}

		Shoot newShoot = new Shoot(shootIcon, shootWidth, shootHeight, shootX, shootY, shootTime, shootMsg);
		synchronized (this) {
			this.shoots.add(newShoot);
		}
	}

	private Drawable getShootHitIcon(GoalType goalType) {

		switch (goalType.getType()) {
		case GoalType.LIFE:
			return getDrawable(R.drawable.hit_life);
		case GoalType.ERROR:
			return getDrawable(R.drawable.hit_error);
		}
		return getDrawable(R.drawable.hit_normal);
	}

	private synchronized String doSuccesfulShoot(Goal hittedGoal) {
		int preCoef = this.score < 10 ? 1 : Double.valueOf(Math.log10(this.score)).intValue();
		String message = "";
		GoalType goalType = hittedGoal.getGoalType();
		GoalLevel goalLevel = hittedGoal.getLevel();

		int hitScoreInc = goalLevel.getScore() * goalType.getCoef();
		int hitTimeInc = goalLevel.getTimeInc() * goalType.getCoef();

		message = (hitScoreInc > 0 ? "+" : "") + hitScoreInc + "pt " + (hitTimeInc > 0 ? "+" : "") + hitTimeInc + "s";

		this.score += hitScoreInc;
		this.activity.showScore(score);
		this.timeRemaining += hitTimeInc;

		int postCoef = Double.valueOf(Math.log10(this.score)).intValue();

		if (this.score < 10) {
			this.variationThreshold = initialVariationTh;
		} else if (preCoef != postCoef) {
			// Threasholds: 10, 100, 1000...
			this.pointerIncrementTimeLevel = this.pointerIncrementTimeLevel * 10;

			// this.variationThreshold = Math.abs(initialVariationTh
			// / (2 * (Double.valueOf(Math.log10(this.score)).intValue() - 1)));
			for (GoalLevel level : levels) {
				level.setSpeed(level.getSpeed() * Float.valueOf((100 + (postCoef * 10)) / 100));

			}
		}

		return message;
	}

	public void doUnlinkActivity() {
		doKillPaintingThread();
		doClearCurrentGame();
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public float getVariationThreshold() {
		return variationThreshold;
	}

	public void setVariationThreshold(float variationThreshold) {
		this.variationThreshold = variationThreshold;
	}

	public int getCanvasSizeX() {
		return frameWidth;
	}

	public void setCanvasSizeX(int canvasSizeX) {
		this.frameWidth = canvasSizeX;
	}

	public int getCanvasSizeY() {
		return frameHeight;
	}

	public void setCanvasSizeY(int canvasSizeY) {
		this.frameHeight = canvasSizeY;
	}

	public boolean isGameOn() {
		return gameOn;
	}

	public Pointer getPointer() {
		return pointer;
	}

	public BackgroundImage getBackgroundImage() {
		return backgroundImage;
	}

	public Context getContext() {
		return this.view.getContext();
	}

	public SurfaceHolder getSurfaceHolder() {
		return this.view.getHolder();
	}

}
