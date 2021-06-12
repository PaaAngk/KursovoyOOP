package com.company.model;

public class Ski {
    private final String name = "Лыжи";
    private int size;
    private int quant;

    public Ski(int size, int quant){
        this.size = size;
        this.quant = quant;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getQuant() {
        return quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
    }

    public String getName() {
        return name;
    }



}
