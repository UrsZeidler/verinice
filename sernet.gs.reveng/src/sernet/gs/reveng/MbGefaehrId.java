package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:30 PM by Hibernate Tools 3.4.0.CR1

/**
 * MbGefaehrId generated by hbm2java
 */
public class MbGefaehrId implements java.io.Serializable {

	private int gefImpId;
	private int gefId;

	public MbGefaehrId() {
	}

	public MbGefaehrId(int gefImpId, int gefId) {
		this.gefImpId = gefImpId;
		this.gefId = gefId;
	}

	public int getGefImpId() {
		return this.gefImpId;
	}

	public void setGefImpId(int gefImpId) {
		this.gefImpId = gefImpId;
	}

	public int getGefId() {
		return this.gefId;
	}

	public void setGefId(int gefId) {
		this.gefId = gefId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof MbGefaehrId))
			return false;
		MbGefaehrId castOther = (MbGefaehrId) other;

		return (this.getGefImpId() == castOther.getGefImpId())
				&& (this.getGefId() == castOther.getGefId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getGefImpId();
		result = 37 * result + this.getGefId();
		return result;
	}

}
