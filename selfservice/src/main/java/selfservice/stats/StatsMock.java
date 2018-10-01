package selfservice.stats;

import selfservice.api.dashboard.Constants;

import java.util.*;

public class StatsMock implements Stats, Constants {

    @Override
    public List<Map<String, Object>> loginTimeFrame(long from, long to, String scale, Optional<String> spEntityId, String state) {
        long step = step(scale);
        List<Map<String, Object>> result = new ArrayList<>();
        for (long i = from; i <= to; i += step) {
            Map<String, Object> point = new HashMap<>();
            point.put("count_user_id", countValue(scale));
            if (!"minute".equals(scale) && !"hour".equals(scale)) {
                point.put("distinct_count_user_id", countValue(scale));
            }
            spEntityId.ifPresent(id -> point.put("sp_entity_id", id));
            point.put("idp_entity_id", currentUserIdp());
            point.put("time", i * 1000);
            result.add(point);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> loginAggregated(String period, Optional<String> spEntityId, String state) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, Integer.valueOf(period.substring(0, 4)));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.DAY_OF_MONTH, 1);
        if (period.length() > 4) {
            switch (period.substring(4, 5).toUpperCase()) {
                case "Q": {
                    today.set(Calendar.MONTH, ((Integer.valueOf(period.substring(5)) - 1) * 3));
                }
                case "M": {
                    today.set(Calendar.MONTH, Integer.valueOf(period.substring(5)) - 1);
                }
                case "W": {
                    today.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(period.substring(5)));
                }
                case "D": {
                    today.set(Calendar.DAY_OF_YEAR, Integer.valueOf(period.substring(5)));
                }
            }
        } else {
            today.set(Calendar.MONTH, 0);
        }
        long from = today.getTimeInMillis() / 1000;
        today.add(calendarConstant(period), period.length() > 4 && period.substring(4, 5).toLowerCase().equals("q") ? 3 : 1);
        long to = today.getTimeInMillis() / 1000;
        return loginTimeFrame(from, to, periodToScale(period), spEntityId, state);
    }

    @Override
    public List<Map<String, Object>> uniqueLoginCount(long from, long to, String spEntityId, String state) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> point = new HashMap<>();
        point.put("count_user_id", countValue("year"));
        point.put("sp_entity_id", spEntityId);
        point.put("idp_entity_id", currentUserIdp());
        point.put("time", from);
        result.add(point);
        return result;
    }

    private String periodToScale(String period) {
        if (period.length() == 4) {
            return "year";
        }
        switch (period.substring(4, 5).toLowerCase()) {
            case "d":
                return "day";
            case "w":
                return "week";
            case "m":
                return "month";
            case "q":
                return "quarter";
            default:
                throw new IllegalArgumentException("Unknown period:" + period);
        }
    }

    private int calendarConstant(String period) {
        if (period.length() == 4) {
            return Calendar.YEAR;
        }
        switch (period.substring(4, 5).toLowerCase()) {
            case "d":
                return Calendar.DAY_OF_YEAR;
            case "w":
                return Calendar.WEEK_OF_YEAR;
            case "m":
                return Calendar.MONTH;
            case "q":
                return Calendar.MONTH;
            default:
                throw new IllegalArgumentException("Unknown period:" + period);
        }
    }

    private long countValue(String scale) {
        double base = Math.floor(10 * (Math.random() + 1));
        switch (scale) {
            case "minute":
                return (long) base * 60;
            case "hour":
                return (long) base * 60 * 60;
            case "day":
                return (long) base * 24 * 60 * 60;
            case "month":
                return (long) base * 30 * 24 * 60 * 60;
            case "quarter":
                return (long) base * 90 * 24 * 60 * 60;
            case "year":
                return (long) base * 365 * 24 * 60 * 60;
            default:
                throw new IllegalArgumentException("Unknown scale:" + scale);
        }
    }

    private long step(String scale) {
        switch (scale) {
            case "minute":
                return 60;
            case "hour":
                return 60 * 60;
            case "day":
                return 24 * 60 * 60;
            case "month":
                return 30 * 24 * 60 * 60;
            case "quarter":
                return 90 * 24 * 60 * 60;
            case "year":
                return 365 * 24 * 60 * 60;
            default:
                throw new IllegalArgumentException("Unknown scale:" + scale);
        }
    }
}
