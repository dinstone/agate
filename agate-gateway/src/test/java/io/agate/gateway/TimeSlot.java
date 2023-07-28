package io.agate.gateway;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSlot {

    LocalDateTime startTime;
    LocalDateTime finishTime;
    SlotType slotType;

    long charge;

    long length;

    public TimeSlot(LocalDateTime st, LocalDateTime ft, SlotType type) {
        this.startTime = st;
        this.finishTime = ft;
        this.slotType = type;

        this.length = Duration.between(startTime, finishTime).toMinutes();
    }

    public enum SlotType {
        day, night
    }

    @Override
    public String toString() {
        return "TimeSlot [startTime=" + startTime + ", finishTime=" + finishTime + ", slotType=" + slotType
                + ", charge=" + charge + ", length=" + length + "]";
    }

}
