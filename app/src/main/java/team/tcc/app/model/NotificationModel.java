package team.tcc.app.model;

import java.io.Serializable;

/**
 * Created by office on 19-Mar-17.
 */
public class NotificationModel implements Serializable{
    /*notification_id text NOT NULL,
    comp_code text NOT NULL,
    notification text,
    hsc_percentage numeric,
    intrm_percentage numeric,
    grad_percentage numeric,
    pg_percentage numeric,
    created_on timestamp without time zone,
    created_by text,
    updated_on timestamp without time zone,
    updated_by text,*/
    private String notification_id;
    private String comp_code;
    private String notification;
    private Double hsc_percentage;
    private Double intrm_percentage;
    private Double grad_percentage;
    private Double pg_percentage;
    private String status;
    private String exam_code;
    private String training_code;
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return notification+"["+notification_id+"]";
    }

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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getComp_code() {
        return comp_code;
    }

    public void setComp_code(String comp_code) {
        this.comp_code = comp_code;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Double getHsc_percentage() {
        return hsc_percentage;
    }

    public void setHsc_percentage(Double hsc_percentage) {
        this.hsc_percentage = hsc_percentage;
    }

    public Double getIntrm_percentage() {
        return intrm_percentage;
    }

    public void setIntrm_percentage(Double intrm_percentage) {
        this.intrm_percentage = intrm_percentage;
    }

    public Double getGrad_percentage() {
        return grad_percentage;
    }

    public void setGrad_percentage(Double grad_percentage) {
        this.grad_percentage = grad_percentage;
    }

    public Double getPg_percentage() {
        return pg_percentage;
    }

    public void setPg_percentage(Double pg_percentage) {
        this.pg_percentage = pg_percentage;
    }
}
