package mjgv.shooter.game.display.android.layout;

import mjgv.shooter.android.R;
import mjgv.shooter.game.data.TopScoreDataService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class PresentationActivity extends GameBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.presentation);
		loadingProgress(25);
		LoaderThread thread = new LoaderThread();
		thread.start();
		TopScoreDataService.createService(this);
		loadingProgress(50);
	}

	class LoaderThread extends Thread {

		@Override
		public void run() {

			TopScoreDataService.loadTopTenScoreList();
			loadingProgress(75);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			transitionToMenu();
		}

	}

	private void transitionToMenu() {
		Intent intent = new Intent(PresentationActivity.this, MenuActivity.class);
		startActivity(intent);
	}

	private void loadingProgress(int progress) {
		ProgressBar pg = (ProgressBar) this.findViewById(R.id.pbLoadingPresentation);
		pg.setProgress(progress);
	}

}
