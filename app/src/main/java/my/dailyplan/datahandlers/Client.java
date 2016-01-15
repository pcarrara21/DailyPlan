package my.dailyplan.datahandlers;

import java.io.Serializable;

public final class Client implements Serializable{ //Classe per la rappresentazione dei clienti

    private long id;
    private String name;
    private String city;
    private String tel;
    private String mail;

    public Client(long id, String name, String city, String tel, String mail) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.tel = tel;
        this.mail = mail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTel() {
        return tel;
    }

    public String getMail() {
        return mail;
    }
}
