package mjgv.shooter.game.display.components;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public abstract class DrawableItem {

	private Drawable drawableItem;

	private int positionX;

	private int positionY;

	private int itemWidth;

	private int itemHeight;

	private int halfWidth;

	private int halfHeight;

	private long lastTimeRendered;

	private int alpha = 255;

	public DrawableItem(Drawable drawableItem, int positionX, int positionY) {
		super();
		this.drawableItem = drawableItem;
		this.positionX = positionX;
		this.positionY = positionY;
		this.itemWidth = drawableItem != null ? drawableItem.getIntrinsicWidth() : 0;
		this.halfWidth = itemWidth / 2;
		this.itemHeight = drawableItem != null ? drawableItem.getIntrinsicHeight() : 0;
		this.halfHeight = itemHeight / 2;
	}

	public void draw(Canvas canvas) {
		this.drawableItem.setBounds(positionX - itemWidth / 2, positionY - itemHeight / 2, positionX + itemWidth / 2,
				positionY + itemHeight / 2);
		this.drawableItem.setAlpha(getAlpha());
		this.drawableItem.draw(canvas);
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public long getLastTimeRendered() {
		return lastTimeRendered;
	}

	public void setLastTimeRendered(long lastTimeRendered) {
		this.lastTimeRendered = lastTimeRendered;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	protected int getHalfWidth() {
		return halfWidth;
	}

	protected int getHalfHeight() {
		return halfHeight;
	}

	protected Drawable getDrawableItem() {
		return drawableItem;
	}

	protected synchronized int getAlpha() {
		return alpha;
	}

	protected void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public abstract Long getIdentifier();

	public abstract void applyTime();

}
