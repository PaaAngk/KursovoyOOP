package com.company.model;

import com.company.model.Client;
import com.company.model.Ski;

import java.util.Date;

public class Rent {
    private int id;
    private Date time;
    private Ski ski;
    private Client client;
    private String add;
    private int hourRent;

    public Rent(int id, Date time, Ski ski, Client client, String add, int hourRent) {
        this.id = id;
        this.time = time;
        this.ski = ski;
        this.client = client;
        this.add = add;
        this.hourRent = hourRent;
    }

    public Ski getSki() {
        return ski;
    }

    public void setSki(Ski ski) {
        this.ski = ski;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getHourRent() {
        return hourRent;
    }

    public void setHourRent(int hourRent) {
        this.hourRent = hourRent;
    }
}
