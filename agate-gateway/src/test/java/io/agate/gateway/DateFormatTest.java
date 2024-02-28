/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatTest {
    public static void main(String[] args) {
        long timestamp = new Date().getTime();

        tdate(timestamp);
        ndate(timestamp);
    }

    private static void tdate(long timestamp) {
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

        long s = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String ts = dateTimeFormat.format(new Date(timestamp));
        }
        long e = System.currentTimeMillis();

        System.out.println("tdate is " + (e - s));
    }

    private static void ndate(long timestamp) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        ZonedDateTime atZone = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("GMT"));
        long s = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String ts = f.format(atZone);
            // String ts = Utils.formatRFC1123DateTime(timestamp);
        }
        long e = System.currentTimeMillis();

        System.out.println("ndate is " + (e - s));
    }
}
