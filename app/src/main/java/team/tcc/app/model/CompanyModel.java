package team.tcc.app.model;

public class CompanyModel {
	private String comp_code;
	private String comp_name;
	/**
	 * @return the comp_code
	 */
	public String getComp_code() {
		return comp_code;
	}
	/**
	 * @param comp_code the comp_code to set
	 */
	public void setComp_code(String comp_code) {
		this.comp_code = comp_code;
	}
	/**
	 * @return the comp_name
	 */
	public String getComp_name() {
		return comp_name;
	}
	/**
	 * @param comp_name the comp_name to set
	 */
	public void setComp_name(String comp_name) {
		this.comp_name = comp_name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return comp_name;
	}
	
}
