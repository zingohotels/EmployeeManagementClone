package app.zingo.employeemanagements.model;

public class Navigation_Model {

    String tv1;
    Integer imagehistory,image1;

    public String getTv1() {return tv1;}

    public void setTv1(String tv1) {this.tv1 = tv1;}

    public Integer getImagehistory() {return imagehistory;}

    public void setImagehistory(Integer imagehistory) {this.imagehistory = imagehistory;}

    public Integer getImage1() {return image1;}

    public void setImage1(Integer image1) {this.image1 = image1;}

    public Navigation_Model(String tv1, Integer imagehistory, Integer image1) {
        this.tv1 = tv1;
        this.imagehistory = imagehistory;
        this.image1 = image1;
    }
}
