package team.tcc.app.model;

public class ExamModel {
	private String exam_code;
	private String exam_title;
	private String exam_desc;
	private String exam_welcometext;
	private String exam_endtext;
	private String active;
	private String startdate;
	private String expires;
	private Double marks_exam_total;
	private Double marks_interview_total;
	private Double marks_total_cutoff;
	private Double time_total;


	/**
	 * @return the exam_code
	 */
	public String getExam_code() {
		return exam_code;
	}


	/**
	 * @param exam_code the exam_code to set
	 */
	public void setExam_code(String exam_code) {
		this.exam_code = exam_code;
	}




	/**
	 * @return the exam_title
	 */
	public String getExam_title() {
		return exam_title;
	}


	/**
	 * @param exam_title the exam_title to set
	 */
	public void setExam_title(String exam_title) {
		this.exam_title = exam_title;
	}


	/**
	 * @return the exam_desc
	 */
	public String getExam_desc() {
		return exam_desc;
	}


	/**
	 * @param exam_desc the exam_desc to set
	 */
	public void setExam_desc(String exam_desc) {
		this.exam_desc = exam_desc;
	}


	/**
	 * @return the exam_welcometext
	 */
	public String getExam_welcometext() {
		return exam_welcometext;
	}


	/**
	 * @param exam_welcometext the exam_welcometext to set
	 */
	public void setExam_welcometext(String exam_welcometext) {
		this.exam_welcometext = exam_welcometext;
	}


	/**
	 * @return the exam_endtext
	 */
	public String getExam_endtext() {
		return exam_endtext;
	}


	/**
	 * @param exam_endtext the exam_endtext to set
	 */
	public void setExam_endtext(String exam_endtext) {
		this.exam_endtext = exam_endtext;
	}


	/**
	 * @return the active
	 */
	public String getActive() {
		return active;
	}


	/**
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
	}


	/**
	 * @return the startdate
	 */
	public String getStartdate() {
		return startdate;
	}


	/**
	 * @param startdate the startdate to set
	 */
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}


	/**
	 * @return the expires
	 */
	public String getExpires() {
		return expires;
	}


	/**
	 * @param expires the expires to set
	 */
	public void setExpires(String expires) {
		this.expires = expires;
	}


	/**
	 * @return the marks_exam_total
	 */
	public Double getMarks_exam_total() {
		return marks_exam_total;
	}


	/**
	 * @param marks_exam_total the marks_exam_total to set
	 */
	public void setMarks_exam_total(Double marks_exam_total) {
		this.marks_exam_total = marks_exam_total;
	}


	/**
	 * @return the marks_interview_total
	 */
	public Double getMarks_interview_total() {
		return marks_interview_total;
	}


	/**
	 * @param marks_interview_total the marks_interview_total to set
	 */
	public void setMarks_interview_total(Double marks_interview_total) {
		this.marks_interview_total = marks_interview_total;
	}


	/**
	 * @return the marks_total_cutoff
	 */
	public Double getMarks_total_cutoff() {
		return marks_total_cutoff;
	}


	/**
	 * @param marks_total_cutoff the marks_total_cutoff to set
	 */
	public void setMarks_total_cutoff(Double marks_total_cutoff) {
		this.marks_total_cutoff = marks_total_cutoff;
	}


	/**
	 * @return the time_total
	 */
	public Double getTime_total() {
		return time_total;
	}


	/**
	 * @param time_total the time_total to set
	 */
	public void setTime_total(Double time_total) {
		this.time_total = time_total;
	}


	@Override
	public String toString() {
		return exam_title+"["+exam_code+"]";
	}
}
