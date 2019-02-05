package mjgv.shooter.game.data;

import java.util.List;

public interface TopScoreDAO {

	void deleteLowerScores(TopScoreDTO lastScoreDTO);

	void addNewSocore(TopScoreDTO newScoreDTO);

	List<TopScoreDTO> getTopScores(int size);
}
