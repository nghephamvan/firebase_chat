package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.content.Context;

import com.example.tranquoctrungcntt.uchat.Objects.Message;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.DAY_MILLIS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.HOUR_MILLIS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.MINUTE_MILLIS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;


public class StringUtils {


    public static void copyContent(Context context, String content) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", content);

        clipboard.setPrimaryClip(clip);

        showLongToast(context, "Đã sao chép nội dung !");

    }

    public static String formatName(String string) {
        StringBuilder sb = new StringBuilder(string);
        for (int i = 0; i < sb.length(); i++)
            if (i == 0 || sb.charAt(i - 1) == ' ')//first letter to uppercase by default
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
        return sb.toString();
    }

    public static String formatSecondsToHours(int inputSeconds) {

        int hours = inputSeconds / 3600;
        int secondsLeft = inputSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime;

        String hour_string, minutes_string, second_string;

        if (hours < 10) {
            hour_string = "0" + hours;
        } else hour_string = "" + hours;

        if (minutes < 10) {
            minutes_string = "0" + minutes;
        } else minutes_string = "" + minutes;

        if (seconds < 10)
            second_string = "0" + seconds;
        else second_string = seconds + "";

        if (hours == 0)
            formattedTime = minutes_string + ":" + second_string;
        else formattedTime = hour_string + ":" + minutes_string + ":" + second_string;

        return formattedTime;
    }

    public static String covertStringToURL(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d");
        } catch (Exception e) {
        }
        return str;
    }

    public static String formatSmallNumber(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    public static String formatTimeAgoShortly(long inputTime) {

        if (inputTime < 1000000000000L) inputTime *= 1000;

        long now = System.currentTimeMillis();

        if (inputTime > now || inputTime <= 0) return null;

        final long diff = now - inputTime;

        if (diff >= 48 * HOUR_MILLIS) return null;

        return formatTimeAgoDetail(inputTime)
                .replaceAll(" trước", "")
                .replaceAll(" đây", "").trim();

    }

    public static String formatTimeAgoDetail(long inputTime) {

        if (inputTime < 1000000000000L) inputTime *= 1000;

        long now = System.currentTimeMillis();

        if (inputTime > now || inputTime <= 0) return null;

        final long diff = now - inputTime;

        if (diff < MINUTE_MILLIS) {
            return "vừa mới đây";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 phút trước";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút trước";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 giờ trước";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ trước";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "hôm qua";
        } else if (diff / DAY_MILLIS <= 7) {
            return diff / DAY_MILLIS + " ngày trước";
        } else return "nhiều ngày trước";
    }

    public static String formatTimeForDetail(long input) {

        Calendar message_calendar = Calendar.getInstance();

        message_calendar.setTimeInMillis(input);

        int seen_date = message_calendar.get(Calendar.DATE);
        int seen_month = message_calendar.get(Calendar.MONTH) + 1;
        int seen_year = message_calendar.get(Calendar.YEAR);
        int seen_hour = message_calendar.get(Calendar.HOUR_OF_DAY);
        int seen_minute = message_calendar.get(Calendar.MINUTE);
        int seen_week = message_calendar.get(Calendar.WEEK_OF_YEAR);

        Calendar now_calendar = Calendar.getInstance();

        int date_now = now_calendar.get(Calendar.DATE);
        int month_now = now_calendar.get(Calendar.MONTH) + 1;
        int year_now = now_calendar.get(Calendar.YEAR);
        int week_now = now_calendar.get(Calendar.WEEK_OF_YEAR);

        String date_string = formatSmallNumber(seen_date);
        String month_string = formatSmallNumber(seen_month);
        String year_string = formatSmallNumber(seen_year);
        String hour_string = formatSmallNumber(seen_hour);
        String minute_string = formatSmallNumber(seen_minute);
        String dayofweek_string = message_calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        if (seen_year == year_now) {
            if (seen_month == month_now) {
                if (seen_date == date_now) {
                    return "Lúc " + hour_string + "h" + minute_string + ", hôm nay";
                } else {
                    if (seen_week == week_now) {
                        return hour_string + "h" + minute_string + ", " + dayofweek_string;
                    } else {
                        return date_string + "/" + month_string + " Lúc " + hour_string + ":" + minute_string;
                    }
                }
            } else return date_string + "/" + month_string + "/" + year_string;
        } else return date_string + "/" + month_string + "/" + year_string;

    }

    public static String formatConversationTime(long input) {

        Calendar message_calendar = Calendar.getInstance();

        message_calendar.setTimeInMillis(input);

        int send_date = message_calendar.get(Calendar.DATE);
        int send_month = message_calendar.get(Calendar.MONTH) + 1;
        int send_year = message_calendar.get(Calendar.YEAR);
        int send_hour = message_calendar.get(Calendar.HOUR_OF_DAY);
        int send_minute = message_calendar.get(Calendar.MINUTE);
        int send_week = message_calendar.get(Calendar.WEEK_OF_YEAR);

        Calendar now_calendar = Calendar.getInstance();

        int date_now = now_calendar.get(Calendar.DATE);
        int month_now = now_calendar.get(Calendar.MONTH) + 1;
        int year_now = now_calendar.get(Calendar.YEAR);
        int week_now = now_calendar.get(Calendar.WEEK_OF_YEAR);

        String date_string = formatSmallNumber(send_date);
        String month_string = formatSmallNumber(send_month);
        String year_string = formatSmallNumber(send_year);
        String hour_string = formatSmallNumber(send_hour);
        String minute_string = formatSmallNumber(send_minute);
        String dayofweek_string = message_calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());

        if (send_year == year_now) {
            if (send_month == month_now) {
                if (send_date == date_now) {
                    return hour_string + ":" + minute_string;
                } else {
                    if (send_week == week_now) {
                        return dayofweek_string;
                    } else {
                        return date_string + "/" + month_string;
                    }
                }
            } else return date_string + " " + month_string;
        } else return date_string + "/" + month_string + "/" + year_string;

    }

    public static String formatSendTime(long input) {

        Calendar message_calendar = Calendar.getInstance();

        message_calendar.setTimeInMillis(input);

        int send_date = message_calendar.get(Calendar.DATE);
        int send_month = message_calendar.get(Calendar.MONTH) + 1;
        int send_year = message_calendar.get(Calendar.YEAR);
        int send_hour = message_calendar.get(Calendar.HOUR_OF_DAY);
        int send_minute = message_calendar.get(Calendar.MINUTE);
        int send_week = message_calendar.get(Calendar.WEEK_OF_YEAR);

        Calendar now_calendar = Calendar.getInstance();

        int date_now = now_calendar.get(Calendar.DATE);
        int month_now = now_calendar.get(Calendar.MONTH) + 1;
        int year_now = now_calendar.get(Calendar.YEAR);
        int week_now = now_calendar.get(Calendar.WEEK_OF_YEAR);

        String date_string = formatSmallNumber(send_date);
        String month_string = formatSmallNumber(send_month);
        String year_string = formatSmallNumber(send_year);
        String hour_string = formatSmallNumber(send_hour);
        String minute_string = formatSmallNumber(send_minute);
        String dayofweek_string = message_calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        if (send_year == year_now) {
            if (send_month == month_now) {
                if (send_date == date_now) {
                    return hour_string + ":" + minute_string;
                } else {
                    if (send_week == week_now) {
                        return hour_string + ":" + minute_string + ", " + dayofweek_string;
                    } else {
                        return hour_string + ":" + minute_string + ", " + date_string + "/" + month_string;
                    }
                }
            } else return date_string + "/" + month_string + "/" + year_string;
        } else return date_string + "/" + month_string + "/" + year_string;


    }

    public static String formatTimeDivider(Message preMessage, Message currentMessage) {

        Calendar pre_calendar = Calendar.getInstance();

        pre_calendar.setTimeInMillis(preMessage.getSendTime());

        int pre_send_date = pre_calendar.get(Calendar.DATE);
        int pre_send_hour = pre_calendar.get(Calendar.HOUR_OF_DAY);

        Calendar current_calendar = Calendar.getInstance();

        current_calendar.setTimeInMillis(currentMessage.getSendTime());

        int current_send_date = current_calendar.get(Calendar.DATE);
        int current_send_month = current_calendar.get(Calendar.MONTH) + 1;
        int current_send_year = current_calendar.get(Calendar.YEAR);
        int current_send_hour = current_calendar.get(Calendar.HOUR_OF_DAY);
        int current_send_minute = current_calendar.get(Calendar.MINUTE);

        String date_string = formatSmallNumber(current_send_date);
        String month_string = formatSmallNumber(current_send_month);
        String year_string = formatSmallNumber(current_send_year);
        String hour_string = formatSmallNumber(current_send_hour);
        String minute_string = formatSmallNumber(current_send_minute);

        if (pre_send_date != current_send_date) {
            return ("Ngày " + date_string + " Tháng " + month_string + " Năm " + year_string).toUpperCase();
        } else if (pre_send_hour != current_send_hour) {
            return hour_string + ":" + minute_string;
        } else return null;

    }


}
