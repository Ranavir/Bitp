package team.tcc.app.model;

public class StudentTrainingProfileModel {
	//bitp_user
	private Integer user_id;
	private String name;
	private String phnno;
	private String email;
	  
	//bitp_training
	private String training_code;
	private String training_desc;
	private double mth1_stipen_amt;
	private double mth2_stipen_amt;
	private double mth3_stipen_amt;
	private double mth4_stipen_amt;
	private double mth5_stipen_amt;
	private double mth6_stipen_amt;
	private String comp_code;
	  
	  //bitp_trainee_details
	private String mth1_feedback;
	private String mth2_feedback;
	private String mth3_feedback;
	private String mth4_feedback;
	private String mth5_feedback;
	private String mth6_feedback;
	private String mth1_stipen_rcv;
	private String mth2_stipen_rcv;
	private String mth3_stipen_rcv;
	private String mth4_stipen_rcv;
	private String mth5_stipen_rcv;
	private String mth6_stipen_rcv;
	private String project_submission_status;
	private String issue_certificate_status;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return training_code+"["+training_desc+"]";
	}
	/**
	 * @return the user_id
	 */
	public Integer getUser_id() {
		return user_id;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the phnno
	 */
	public String getPhnno() {
		return phnno;
	}
	/**
	 * @param phnno the phnno to set
	 */
	public void setPhnno(String phnno) {
		this.phnno = phnno;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the training_code
	 */
	public String getTraining_code() {
		return training_code;
	}
	/**
	 * @param training_code the training_code to set
	 */
	public void setTraining_code(String training_code) {
		this.training_code = training_code;
	}
	/**
	 * @return the training_desc
	 */
	public String getTraining_desc() {
		return training_desc;
	}
	/**
	 * @param training_desc the training_desc to set
	 */
	public void setTraining_desc(String training_desc) {
		this.training_desc = training_desc;
	}
	/**
	 * @return the mth1_stipen_amt
	 */
	public double getMth1_stipen_amt() {
		return mth1_stipen_amt;
	}
	/**
	 * @param mth1_stipen_amt the mth1_stipen_amt to set
	 */
	public void setMth1_stipen_amt(double mth1_stipen_amt) {
		this.mth1_stipen_amt = mth1_stipen_amt;
	}
	/**
	 * @return the mth2_stipen_amt
	 */
	public double getMth2_stipen_amt() {
		return mth2_stipen_amt;
	}
	/**
	 * @param mth2_stipen_amt the mth2_stipen_amt to set
	 */
	public void setMth2_stipen_amt(double mth2_stipen_amt) {
		this.mth2_stipen_amt = mth2_stipen_amt;
	}
	/**
	 * @return the mth3_stipen_amt
	 */
	public double getMth3_stipen_amt() {
		return mth3_stipen_amt;
	}
	/**
	 * @param mth3_stipen_amt the mth3_stipen_amt to set
	 */
	public void setMth3_stipen_amt(double mth3_stipen_amt) {
		this.mth3_stipen_amt = mth3_stipen_amt;
	}
	/**
	 * @return the mth4_stipen_amt
	 */
	public double getMth4_stipen_amt() {
		return mth4_stipen_amt;
	}
	/**
	 * @param mth4_stipen_amt the mth4_stipen_amt to set
	 */
	public void setMth4_stipen_amt(double mth4_stipen_amt) {
		this.mth4_stipen_amt = mth4_stipen_amt;
	}
	/**
	 * @return the mth5_stipen_amt
	 */
	public double getMth5_stipen_amt() {
		return mth5_stipen_amt;
	}
	/**
	 * @param mth5_stipen_amt the mth5_stipen_amt to set
	 */
	public void setMth5_stipen_amt(double mth5_stipen_amt) {
		this.mth5_stipen_amt = mth5_stipen_amt;
	}
	/**
	 * @return the mth6_stipen_amt
	 */
	public double getMth6_stipen_amt() {
		return mth6_stipen_amt;
	}
	/**
	 * @param mth6_stipen_amt the mth6_stipen_amt to set
	 */
	public void setMth6_stipen_amt(double mth6_stipen_amt) {
		this.mth6_stipen_amt = mth6_stipen_amt;
	}
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
	 * @return the mth1_feedback
	 */
	public String getMth1_feedback() {
		return mth1_feedback;
	}
	/**
	 * @param mth1_feedback the mth1_feedback to set
	 */
	public void setMth1_feedback(String mth1_feedback) {
		this.mth1_feedback = mth1_feedback;
	}
	/**
	 * @return the mth2_feedback
	 */
	public String getMth2_feedback() {
		return mth2_feedback;
	}
	/**
	 * @param mth2_feedback the mth2_feedback to set
	 */
	public void setMth2_feedback(String mth2_feedback) {
		this.mth2_feedback = mth2_feedback;
	}
	/**
	 * @return the mth3_feedback
	 */
	public String getMth3_feedback() {
		return mth3_feedback;
	}
	/**
	 * @param mth3_feedback the mth3_feedback to set
	 */
	public void setMth3_feedback(String mth3_feedback) {
		this.mth3_feedback = mth3_feedback;
	}
	/**
	 * @return the mth4_feedback
	 */
	public String getMth4_feedback() {
		return mth4_feedback;
	}
	/**
	 * @param mth4_feedback the mth4_feedback to set
	 */
	public void setMth4_feedback(String mth4_feedback) {
		this.mth4_feedback = mth4_feedback;
	}
	/**
	 * @return the mth5_feedback
	 */
	public String getMth5_feedback() {
		return mth5_feedback;
	}
	/**
	 * @param mth5_feedback the mth5_feedback to set
	 */
	public void setMth5_feedback(String mth5_feedback) {
		this.mth5_feedback = mth5_feedback;
	}
	/**
	 * @return the mth6_feedback
	 */
	public String getMth6_feedback() {
		return mth6_feedback;
	}
	/**
	 * @param mth6_feedback the mth6_feedback to set
	 */
	public void setMth6_feedback(String mth6_feedback) {
		this.mth6_feedback = mth6_feedback;
	}
	/**
	 * @return the mth1_stipen_rcv
	 */
	public String getMth1_stipen_rcv() {
		return mth1_stipen_rcv;
	}
	/**
	 * @param mth1_stipen_rcv the mth1_stipen_rcv to set
	 */
	public void setMth1_stipen_rcv(String mth1_stipen_rcv) {
		this.mth1_stipen_rcv = mth1_stipen_rcv;
	}
	/**
	 * @return the mth2_stipen_rcv
	 */
	public String getMth2_stipen_rcv() {
		return mth2_stipen_rcv;
	}
	/**
	 * @param mth2_stipen_rcv the mth2_stipen_rcv to set
	 */
	public void setMth2_stipen_rcv(String mth2_stipen_rcv) {
		this.mth2_stipen_rcv = mth2_stipen_rcv;
	}
	/**
	 * @return the mth3_stipen_rcv
	 */
	public String getMth3_stipen_rcv() {
		return mth3_stipen_rcv;
	}
	/**
	 * @param mth3_stipen_rcv the mth3_stipen_rcv to set
	 */
	public void setMth3_stipen_rcv(String mth3_stipen_rcv) {
		this.mth3_stipen_rcv = mth3_stipen_rcv;
	}
	/**
	 * @return the mth4_stipen_rcv
	 */
	public String getMth4_stipen_rcv() {
		return mth4_stipen_rcv;
	}
	/**
	 * @param mth4_stipen_rcv the mth4_stipen_rcv to set
	 */
	public void setMth4_stipen_rcv(String mth4_stipen_rcv) {
		this.mth4_stipen_rcv = mth4_stipen_rcv;
	}
	/**
	 * @return the mth5_stipen_rcv
	 */
	public String getMth5_stipen_rcv() {
		return mth5_stipen_rcv;
	}
	/**
	 * @param mth5_stipen_rcv the mth5_stipen_rcv to set
	 */
	public void setMth5_stipen_rcv(String mth5_stipen_rcv) {
		this.mth5_stipen_rcv = mth5_stipen_rcv;
	}
	/**
	 * @return the mth6_stipen_rcv
	 */
	public String getMth6_stipen_rcv() {
		return mth6_stipen_rcv;
	}
	/**
	 * @param mth6_stipen_rcv the mth6_stipen_rcv to set
	 */
	public void setMth6_stipen_rcv(String mth6_stipen_rcv) {
		this.mth6_stipen_rcv = mth6_stipen_rcv;
	}
	/**
	 * @return the project_submission_status
	 */
	public String getProject_submission_status() {
		return project_submission_status;
	}
	/**
	 * @param project_submission_status the project_submission_status to set
	 */
	public void setProject_submission_status(String project_submission_status) {
		this.project_submission_status = project_submission_status;
	}
	/**
	 * @return the issue_certificate_status
	 */
	public String getIssue_certificate_status() {
		return issue_certificate_status;
	}
	/**
	 * @param issue_certificate_status the issue_certificate_status to set
	 */
	public void setIssue_certificate_status(String issue_certificate_status) {
		this.issue_certificate_status = issue_certificate_status;
	}
	  
	
	
	  
}//end class
