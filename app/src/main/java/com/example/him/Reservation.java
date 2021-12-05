package com.example.him;

public class Reservation {

    private String masterId;            // 등록한 회원 Doc ID

    private int fitnessEquipmentId;     // 운동기구 ID
    private long startDateTime;         // 예약시작일시 (millisecond)
    private long endDateTime;           // 예약종료일시 (millisecond)

    public Reservation() {}

    public Reservation(String masterId, int fitnessEquipmentId, long startDateTime, long endDateTime) {
        this.masterId = masterId;
        this.fitnessEquipmentId = fitnessEquipmentId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getMasterId() {
        return masterId;
    }

    public int getFitnessEquipmentId() {
        return fitnessEquipmentId;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }
}
