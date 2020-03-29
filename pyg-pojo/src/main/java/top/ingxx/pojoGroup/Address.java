package top.ingxx.pojoGroup;

import top.ingxx.pojo.TbAddress;

import java.io.Serializable;

public class Address implements Serializable {

    private String province;

    private String city;

    private String town;

    private TbAddress address;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public TbAddress getAddress() {
        return address;
    }

    public void setAddress(TbAddress address) {
        this.address = address;
    }
}
