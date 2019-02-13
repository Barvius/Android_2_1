package com.example.barvius;

public class DBItems {
    private long id;
    private String ru;
    private String en;

    public DBItems(long id, String ru, String en) {
        this.id = id;
        this.ru = ru;
        this.en = en;
    }

    public DBItems(String ru, String en) {
        this.ru = ru;
        this.en = en;
    }

    public DBItems() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

}
