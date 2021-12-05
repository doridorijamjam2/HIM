package com.example.him;

public class FitnessEquipmentItem {

    public int id;
    public int partKind;        // 부위종류
    public String name;         // 운동기구명
    public String url;          // 동영상 url

    public FitnessEquipmentItem(int id, int partKind, String name, String url) {
        this.id = id;
        this.partKind = partKind;
        this.name = name;
        this.url = url;
    }
}
