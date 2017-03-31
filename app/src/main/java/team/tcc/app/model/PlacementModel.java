package team.tcc.app.model;

import java.io.Serializable;

/**
 * Created by office on 19-Mar-17.
 */
public class PlacementModel implements Serializable{

    private int slno;
    private String placement_code;
    private String comp_code;
    private String news;
    private String active;
    private String created_on;
    private String created_by;
    private String updated_on;
    private String updated_by;

    @Override
    public String toString() {
        return "PlacementModel{" +
                "slno='" + slno + '\'' +
                ", placement_code='" + placement_code + '\'' +
                ", news='" + news + '\'' +
                ", created_by='" + created_by + '\'' +
                '}';
    }

    public int getSlno() {
        return slno;
    }

    public void setSlno(int slno) {
        this.slno = slno;
    }

    public String getPlacement_code() {
        return placement_code;
    }

    public void setPlacement_code(String placement_code) {
        this.placement_code = placement_code;
    }

    public String getComp_code() {
        return comp_code;
    }

    public void setComp_code(String comp_code) {
        this.comp_code = comp_code;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }
}
