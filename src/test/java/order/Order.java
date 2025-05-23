package order;

import java.util.List;

public class Order {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private List<String> color;

    public void setColor(List<String> color) {
        this.color = color;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setRentTime(int rentTime) {
        this.rentTime = rentTime;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMetroStation(String metroStation) {
        this.metroStation = metroStation;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<String> getColor() {
        return color;
    }

    public String getComment() {
        return comment;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public int getRentTime() {
        return rentTime;
    }

    public String getPhone() {
        return phone;
    }

    public String getMetroStation() {
        return metroStation;
    }

    public String getAddress() {
        return address;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Order(String firstName, String lastName, String address, String metroStation,
                 String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }
}