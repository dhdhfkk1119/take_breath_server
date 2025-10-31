package com.take.take_breath._core._utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    /**
     * "yyyy년 MM월 dd일 HH시 mm분" 형식의 문자열로 반환
     * @param startDate
     * @return
     */
    public static String timestampFormat(Timestamp startDate) {
        if (startDate == null) return "----년 --월 --일"; // 또는 다른 기본값 반환

        Date currentDate = new Date(startDate.getTime());
        return DateFormatUtils.format(currentDate, "yyyy년 MM월 dd일 HH시 mm분");
    }

    /**
     * "yyyy년 MM월 dd일 HH시 mm분" 형식의 문자열로 반환
     * @param dateTime
     * @return
     */
    public static String localDateTimeFormat(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return dateTime.format(formatter);
    }

    /**
     * "a HH:mm" 형식의 문자열 (오전/오후 표시 포함)로 반환
     * @param startDate
     * @return
     */
    public static String chatFormat(Timestamp startDate) {
        Date currentDate = new Date(startDate.getTime());
        return DateFormatUtils.format(currentDate, "a HH:mm");
    }

    /**
     * "yyyy-MM-dd'T'HH:mm" 형식의 문자열로 반환
     * @param endDate
     * @return
     */
    public static String dateTimeFormat(Timestamp endDate) {
        if (endDate == null) return "";
        LocalDateTime localDateTime = endDate.toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }
}

