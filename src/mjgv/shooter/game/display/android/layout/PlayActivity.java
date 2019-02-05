package mjgv.shooter.game.display.android.layout;

import java.util.List;

import mjgv.shooter.android.R;
import mjgv.shooter.game.handler.GameHandler;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class PlayActivity extends GameBaseActivity implements SensorEventListener {

	private static final long SHOOT_TIME_VIBRATION = 300;
	// Singleton access
	private GameHandler gameHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i("CreatingPlayActivity", "PlayActivity::onCreate Starting");
		super.onCreate(savedInstanceState);

		gameHandler = GameHandler.getGameHandler();
		gameHandler.doLinkActivity(PlayActivity.this);

		Log.i("CreatingPlayActivity", "Creating content view");
		setContentView(R.layout.play);

		Log.i("CreatingPlayActivity", "Configuration of the time and the acelerometer");
		timeConfiguration();
		configAcelerometer();

		Button shootBtn = (Button) this.findViewById(R.id.btnShoot);
		shootBtn.setOnClickListener(gameHandler.getShootBtnListener());

		gameHandler.doCreateNewGame();

	}

	private void timeConfiguration() {
		Chronometer chrLeftTime = (Chronometer) this.findViewById(R.id.chrTimeElapsed);
		chrLeftTime.setBase(0);
		chrLeftTime.setOnChronometerTickListener(gameHandler.doCreateTimeListener());

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		this.gameHandler.doUpdatePointerPosition(event.values[1], event.values[0]);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing to implement
	}

	@Override
	protected void onStop() {
		doDisableAcelerometer();
		super.onStop();
	}

	private void doDisableAcelerometer() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this);
	}

	protected void configAcelerometer() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void doNotifyGameOver() {
		Intent intent = new Intent(PlayActivity.this, GameSummaryActivity.class);
		// Creamos la información a pasar entre actividades
		Bundle b = new Bundle();
		b.putString(GameSummaryActivity.SCORE_BUNDLE_KEY, String.valueOf(gameHandler.getScore()));

		// Quitamos el sensor de movimientos
		doDisableAcelerometer();

		// Añadimos la información al intent
		intent.putExtras(b);
		startActivity(intent);
	}

	public void showMessage(String msg, int colorResourceKey, int textSizeResourceKey) {
		TextView txtMessage = ((TextView) this.findViewById(R.id.lblOverShootArea));
		txtMessage.setText(msg);
	}

	@Override
	protected void onDestroy() {
		gameHandler.doUnlinkActivity();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		gameHandler.doUnlinkActivity();
		Intent intent = new Intent(PlayActivity.this, MenuActivity.class);
		startActivity(intent);
	}

	public void showScore(int score) {
		((TextView) this.findViewById(R.id.txtScore)).setText(String.valueOf(score));
	}

	public void showRemainingTime(String timeRemaining, int color) {
		// Actualizamos el marcador de tiempo
		TextView txtTimeRemaining = (TextView) this.findViewById(R.id.txtTime);
		txtTimeRemaining.setText(String.valueOf(timeRemaining));
		txtTimeRemaining.setTextColor(this.getResources().getColor(color));
	}

	public void showCountDown(int countDown) {
		showMessage(String.valueOf(countDown), R.color.lblTextColorNormal, R.dimen.backCountSize);
	}

	public void hideCountDown() {
		showMessage("", R.color.lblTextColorNormal, R.dimen.backCountSize);
	}

	public void doNotifyShoot() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(SHOOT_TIME_VIBRATION);
	}

}
