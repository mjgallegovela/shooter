package mjgv.shooter.game.display.components;

import android.graphics.drawable.Drawable;

public class Pointer extends DrawableItem {

	private float speedX = 0;

	private float speedY = 0;

	private int frameWidth;

	private int frameHeight;

	public Pointer(Drawable drawableItem, int frameWidth, int frameHeight) {
		super(drawableItem, frameWidth / 2, frameHeight / 2);
		this.frameHeight = frameHeight;
		this.frameWidth = frameWidth;
		this.setLastTimeRendered(System.currentTimeMillis());
	}

	@Override
	public Long getIdentifier() {
		// TODO Auto-generated method stub
		return 0L;
	}

	/* Speed in pixel / 100 ms */
	@Override
	public void applyTime() {
		long currentTime = System.currentTimeMillis();
		int timeElapsed = Long.valueOf((currentTime - getLastTimeRendered())).intValue();

		Float incrementX = (speedX * timeElapsed);
		Float incrementY = (speedY * timeElapsed);

		if (incrementX != 0 && incrementY != 0) {

			int newPositionX = getPositionX() + incrementX.intValue();
			int newPositionY = getPositionY() + incrementY.intValue();

			if ((newPositionX - getHalfWidth()) < 0) {
				newPositionX = getHalfWidth();
			} else if ((newPositionX + getHalfWidth()) > frameWidth) {
				newPositionX = frameWidth - getHalfWidth();
			}

			if ((newPositionY - getHalfHeight()) < 0) {
				newPositionY = getHalfHeight();
			} else if ((newPositionY + getHalfHeight()) > frameHeight) {
				newPositionY = frameHeight - getHalfHeight();
			}

			this.setPositionX(newPositionX);
			this.setPositionY(newPositionY);

			this.setLastTimeRendered(currentTime);
		}
	}

	public void setNewSpeed(float speedX, float speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}

	// @Override
	// public void draw(Canvas canvas) {
	// // TODO Auto-generated method stub
	// super.draw(canvas);
	// Paint paint = new Paint();
	// paint.setStyle(Style.FILL_AND_STROKE);
	// paint.setColor(Color.MAGENTA);
	// canvas.drawCircle(getPositionX(), getPositionY(), 5, paint);
	// }

}
