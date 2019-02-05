package mjgv.shooter.game.display.components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;

public class Shoot extends DrawableItem {

	private long identifier;

	private int timeToLive;

	private int nextAlpha = 255;

	private String shootMsg;

	private float letterSize = 0f;

	public Shoot(Drawable drawableItem, int width, int height, int shootX, int shootY, int timeToLive, String shootMsg) {
		super(drawableItem, shootX, shootY);
		this.setItemWidth(width);
		this.setItemHeight(height);
		this.identifier = -System.currentTimeMillis();
		this.timeToLive = timeToLive;
		this.setLastTimeRendered(System.currentTimeMillis());
		this.shootMsg = shootMsg;
		this.letterSize = height / 2;
	}

	@Override
	public Long getIdentifier() {
		return identifier;
	}

	@Override
	public void applyTime() {

		this.setAlpha(nextAlpha);
		long currentTime = System.currentTimeMillis();
		int timeElapsed = Long.valueOf((currentTime - getLastTimeRendered())).intValue();
		nextAlpha -= timeElapsed * 255 / (timeToLive * 1000);
		if (nextAlpha < 0) {
			nextAlpha = 0;
		}
		this.setLastTimeRendered(currentTime);
	}

	public boolean isFinished() {
		return getAlpha() <= 0;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (!"".equals(shootMsg)) {
			Paint paint = new Paint();
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(50);
			paint.setColor(Color.DKGRAY);
			paint.setTextSize(this.letterSize);
			canvas.drawText(shootMsg, getPositionX(), getPositionY(), paint);
		}
	}

}
