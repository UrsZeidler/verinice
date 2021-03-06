package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:30 PM by Hibernate Tools 3.4.0.CR1

/**
 * NmbZusatzId generated by hbm2java
 */
public class NmbZusatzId implements java.io.Serializable {

	private int notizImpId;
	private int notizId;
	private int id;

	public NmbZusatzId() {
	}

	public NmbZusatzId(int notizImpId, int notizId, int id) {
		this.notizImpId = notizImpId;
		this.notizId = notizId;
		this.id = id;
	}

	public int getNotizImpId() {
		return this.notizImpId;
	}

	public void setNotizImpId(int notizImpId) {
		this.notizImpId = notizImpId;
	}

	public int getNotizId() {
		return this.notizId;
	}

	public void setNotizId(int notizId) {
		this.notizId = notizId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof NmbZusatzId))
			return false;
		NmbZusatzId castOther = (NmbZusatzId) other;

		return (this.getNotizImpId() == castOther.getNotizImpId())
				&& (this.getNotizId() == castOther.getNotizId())
				&& (this.getId() == castOther.getId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getNotizImpId();
		result = 37 * result + this.getNotizId();
		result = 37 * result + this.getId();
		return result;
	}

}
