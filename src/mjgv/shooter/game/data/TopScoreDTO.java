package mjgv.shooter.game.data;

public class TopScoreDTO implements Comparable {

	private String name;

	private Integer score;

	private Long dateInMillis;

	@Override
	public int compareTo(Object another) {
		TopScoreDTO anotherScoreDTO = (TopScoreDTO) another;
		if (getScore().equals(anotherScoreDTO.getScore())) {
			return getDateInMillis().compareTo(anotherScoreDTO.getDateInMillis());
		}
		return getScore().compareTo(anotherScoreDTO.getScore());
	}

	public TopScoreDTO(String name, Integer score, Long dateInMillis) {
		super();
		this.name = name;
		this.score = score;
		this.dateInMillis = dateInMillis;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Long getDateInMillis() {
		return dateInMillis;
	}

	public void setDateInMillis(Long dateInMillis) {
		this.dateInMillis = dateInMillis;
	}

}
