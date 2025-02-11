package ru.sber.model;

import java.util.List;

public class ConfirmData {

    private Long key;
    List<String> ids;


    public ConfirmData() {
    }

    public ConfirmData(Long key, List<String> ids) {
        this.key = key;
        this.ids = ids;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "ConfirmData{" +
                "key=" + key +
                ", ids=" + ids +
                '}';
    }

}
