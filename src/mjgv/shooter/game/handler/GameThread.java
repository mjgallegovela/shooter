package mjgv.shooter.game.handler;


public class GameThread extends Thread {

	private boolean run;

	public GameThread() {

		this.run = false;
	}

	// Lo utilizaremos para establecer cuando el hilo corra o no.
	public void setRunning(boolean run) {
		this.run = run;
	}

	public void run() {
		GameHandler gameHandler = GameHandler.getGameHandler();
		while (run) {
			gameHandler.doExecGameCycle();
		}
	}

}