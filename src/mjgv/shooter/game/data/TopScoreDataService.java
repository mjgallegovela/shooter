package mjgv.shooter.game.data;

import java.util.Collections;
import java.util.List;

import mjgv.shooter.game.data.android.impl.TopScoreDAOSQLiteImpl;
import android.content.Context;

public class TopScoreDataService {

	private static TopScoreDAO dao;

	private static List<TopScoreDTO> topScoreList;

	public static void createService(Context context) {
		dao = new TopScoreDAOSQLiteImpl(context);
	}

	public static void loadTopTenScoreList() {
		if (dao != null) {
			topScoreList = dao.getTopScores(10);
		}
	}

	public static List<TopScoreDTO> getTopScoreList() {
		return topScoreList;
	}

	public static void addNewScore(String name, int score) {
		TopScoreDTO newScoreDTO = new TopScoreDTO(name, score, System.currentTimeMillis());
		dao.addNewSocore(newScoreDTO);
		topScoreList.add(newScoreDTO);
		Collections.sort(topScoreList);
		if (topScoreList.size() > 10) {
			int i = 0;
			TopScoreDTO lastScoreToSave = null;
			for (TopScoreDTO scoreToSave : topScoreList) {
				lastScoreToSave = scoreToSave;
				if (i++ >= 10) {
					break;
				}
			}
			if (lastScoreToSave != null) {
				dao.deleteLowerScores(lastScoreToSave);
			}
		}
	}

	public static boolean checkIfSaveAllowed(int score) {
		if (topScoreList.isEmpty() || topScoreList.size() < 10) {
			return true;
		}
		TopScoreDTO lastScore = topScoreList.get(9);
		return lastScore.getScore() < score;
	}
}
