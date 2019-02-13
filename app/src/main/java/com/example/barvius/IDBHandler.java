package com.example.barvius;

import java.util.List;

public interface IDBHandler {
    public DBItems getRandomItems();
    public List<DBItems> getRandomSet(int setSize, long masterId);
    public List<DBItems> getAll();
    public boolean moveToArchive(DBItems items);
    public boolean addItems(DBItems items);
    public void truncateArchive();
    public boolean dictionaryIsEmpty();
    public boolean testIsAvailable();
    public String info();
}
