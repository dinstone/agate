/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agate.gateway;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.agate.gateway.TimeSlot.SlotType;

public class ParkCharge {

    LocalDateTime startTime;
    LocalDateTime finishTime;
    private CarType carType;

    BigDecimal charge;

    long length;

    List<TimeSlot> details = new ArrayList<>();

    public ParkCharge(String startTime, String finishTime, CarType carType) {
        LocalDateTime st = LocalDateTime.parse(startTime);
        LocalDateTime ft = LocalDateTime.parse(finishTime);
        if (st.isAfter(ft)) {
            throw new IllegalStateException(startTime + " is after " + finishTime);
        }
        this.startTime = st;
        this.finishTime = ft;
        this.carType = carType;
        this.length = Duration.between(st, ft).toMinutes();
    }

    @Override
    public String toString() {
        return "ParkCharge [startTime=" + startTime + ", finishTime=" + finishTime + ", carType=" + carType
                + ", length=" + length + ", charge=" + charge + "]";
    }

    public static void main(String[] args) {

        try {
            System.out.println(calculate("2022-10-18T23:00:17", "2022-10-19T08:46:47", CarType.small));

            System.out.println(calculate("2022-10-17T21:04:36", "2022-10-18T08:39:29", CarType.small));

            System.out.println(calculate("2022-10-15T10:23:23", "2022-10-17T08:46:48", CarType.small));

            ParkCharge pc = new ParkCharge("2022-10-15T10:23:23", "2022-10-17T08:46:48", CarType.small);
            System.out.println(pc.charge());
            
            LocalDateTime st = LocalDateTime.parse("2023-03-31T10:23:23");
            LocalDateTime et = st.plusDays(-75);
            System.out.println(et);
            
            st = LocalDateTime.parse("2022-09-30T10:23:23");
            et = st.plusDays(-75);
            System.out.println(et);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public ParkCharge charge() {

        // split the start-finish time to day and night slot
        split(startTime, finishTime, this.details);

        int fhUnitTimes = 0;
        for (TimeSlot timeSlot : details) {
            if (timeSlot.slotType == SlotType.day) {
                if (timeSlot.length == 15) {
                    if (fhUnitTimes < 4) {
                        fhUnitTimes++;
                        timeSlot.charge = 50;
                    } else {
                        timeSlot.charge = 75;
                    }
                }
            } else {
                if (timeSlot.length == 120) {
                    timeSlot.charge = 100;
                }
            }

            // not one day
            if (timeSlot.finishTime.getDayOfMonth() - timeSlot.startTime.getDayOfMonth() != 0) {
                fhUnitTimes = 0;
            }
        }

        long s = this.details.stream().mapToLong(ts -> ts.charge).sum();

        // count slot charge to total charge
        this.charge = new BigDecimal(s).divide(new BigDecimal(100));

        return this;
    }

    private static BigDecimal calculate(String startTime, String finishTime, CarType type) throws ParseException {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime st = LocalDateTime.parse(startTime);
        LocalDateTime ft = LocalDateTime.parse(finishTime);
        if (st.isAfter(ft)) {
            throw new IllegalStateException(startTime + " is after " + finishTime);
        }

        // split the start-finish time to day and night slot
        List<TimeSlot> tss = new ArrayList<>();
        split(st, ft, tss);

        int fhUnitTimes = 0;
        for (TimeSlot timeSlot : tss) {
            if (timeSlot.slotType == SlotType.day) {
                if (timeSlot.length == 15) {
                    if (fhUnitTimes < 4) {
                        fhUnitTimes++;
                        timeSlot.charge = 50;
                    } else {
                        timeSlot.charge = 75;
                    }
                }
            } else {
                if (timeSlot.length == 120) {
                    timeSlot.charge = 100;
                }
            }

            // not one day
            if (timeSlot.finishTime.getDayOfMonth() - timeSlot.startTime.getDayOfMonth() != 0) {
                fhUnitTimes = 0;
            }
        }

        long s = tss.stream().mapToLong(ts -> ts.charge).sum();

        // count slot charge to total charge
        return new BigDecimal(s).divide(new BigDecimal(100));
    }

    private static void split(LocalDateTime st, LocalDateTime ft, List<TimeSlot> tss) {
        // 07:00:00 and 19:00:00
        LocalDateTime sh07 = LocalDateTime.of(st.getYear(), st.getMonth(), st.getDayOfMonth(), 7, 0, 0);
        LocalDateTime sh19 = LocalDateTime.of(st.getYear(), st.getMonth(), st.getDayOfMonth(), 19, 0, 0);
        if (greateEqual(st, sh07) && st.isBefore(sh19)) {
            // day: 07:00:00 -> st -> 19:00:00
            LocalDateTime sft = st.plusMinutes(15);
            if (sft.isBefore(ft)) {
                tss.add(new TimeSlot(st, sft, TimeSlot.SlotType.day));

                split(sft, ft, tss);
            } else {
                tss.add(new TimeSlot(st, ft, TimeSlot.SlotType.day));
            }
        } else {
            // night: 00:00:00 -> st -> 07:00:00 or 19:00:00 -> st -> 00:00:00
            LocalDateTime sft = st.plusHours(2);
            if (sft.isBefore(ft)) {
                tss.add(new TimeSlot(st, sft, TimeSlot.SlotType.night));

                split(sft, ft, tss);
            } else {
                tss.add(new TimeSlot(st, ft, TimeSlot.SlotType.night));
            }
        }
    }

    private static boolean greateEqual(LocalDateTime st, LocalDateTime tt) {
        return st.compareTo(tt) >= 0;
    }

}
