package com.company.model;

public class Client {
    private int id;
    private String fio;
    private String documentName;
    private String documentVal;
    private String phoneNumber;
    private int height;
    private int weight;

    public Client(int id, String fio, String documentName, String documentVal, String phoneNumber, int height, int weight){
        this.id = id;
        this.fio = fio;
        this.documentName = documentName;
        this.documentVal = documentVal;
        this.phoneNumber = phoneNumber;
        this.height = height;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentVal() {
        return documentVal;
    }

    public void setDocumentVal(String documentVal) {
        this.documentVal = documentVal;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
