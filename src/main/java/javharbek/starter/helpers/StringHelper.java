package javharbek.starter.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringHelper {

    public static String clearOnlyOfficeString(String str) {
        if (str == null) return "";
        return str.replaceAll(String.valueOf('\"'), "")
                .replaceAll("\\\\", "")
                .replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll("/", "");
    }

    public static String cyrilToLatin(String message) {
        char[] abcCyr = {' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String[] abcLat = {" ", "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }

    public static String toDisplayCase(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }

    public static String toDisplayCase2(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        int index = 0;
        for (char c : s.toCharArray()) {
            index = index + 1;
            if (index == 2) {
                capNext = false;
            }
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getMonthNameLatin(Integer month) {
        HashMap<Integer, String> data = new HashMap<>();
        data.put(1, "yanvar");
        data.put(2, "fevral");
        data.put(3, "mart");
        data.put(4, "aprel");
        data.put(5, "may");
        data.put(6, "iyun");
        data.put(7, "iyul");
        data.put(8, "avgust");
        data.put(9, "sentabr");
        data.put(10, "oktyabr");
        data.put(11, "noyabr");
        data.put(12, "dekabr");
        return data.get(month);
    }

    public static String getDateShortVariant(LocalDate date) {
        String day = String.valueOf(Long.valueOf(date.format(DateTimeFormatter.ofPattern("dd"))));
        return date.getYear() + "-yil " + day + "-" + StringHelper.getMonthNameLatin(date.getMonthValue());
    }

    public static String toCapitalizedCase(String message) {
        if (message == null) return "";

        message = message.toLowerCase();
        char[] charArray = message.toCharArray();
        boolean foundSpace = true;
        boolean specialCharacter = false;

        for (int i = 0; i < charArray.length; i++) {
            if (Character.isLetter(charArray[i])) {
                if (foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
                if (specialCharacter) {
                    charArray[i] = Character.toLowerCase(charArray[i]);
                    specialCharacter = false;
                }
            } else if (!Character.isWhitespace(charArray[i]) && charArray[i] != '.') {
                foundSpace = false;
                specialCharacter = true;
            } else {
                foundSpace = true;
            }
        }
        message = String.valueOf(charArray);
        message = message.replace("O‘g‘li", "o‘g‘li").replace("Qizi", "qizi");
        return message;
    }
    public static String toCapitalizedCase2(String message) {
        if (message == null) return "";

        message = message.toLowerCase();
        char[] charArray = message.toCharArray();
        boolean foundSpace = true;
        boolean specialCharacter = false;

        for (int i = 0; i < charArray.length; i++) {
            if (Character.isLetter(charArray[i])) {
                if (foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
                if (specialCharacter) {
                    charArray[i] = Character.toLowerCase(charArray[i]);
                    specialCharacter = false;
                }
            } else if (!Character.isWhitespace(charArray[i]) && charArray[i] != '.') {
                foundSpace = false;
                specialCharacter = true;
            } else {
                foundSpace = true;
            }
        }
        message = String.valueOf(charArray);
        return message;
    }

    public static String longListToCommaString(List<Long> ids) {
        if (ids == null) {
            return "'";
        }
        if (ids.isEmpty()) {
            return "";
        }
        return ids.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(",", "(", ")"));
    }

    public static String longListToSearchArraySqlString(List<Long> ids) {
        if (ids == null) {
            return "'";
        }
        if (ids.isEmpty()) {
            return "";
        }
        return ids.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(" = ANY (t.categories) OR ", " ", " = ANY (t.categories) "));
    }


    public static String stringListToSearchAggMongoDb(List<String> ids) {
        if (ids == null) {
            return "'";
        }
        if (ids.isEmpty()) {
            return "";
        }

        List<String> items = new ArrayList<>();
        for (String id : ids) {
            items.add("'" + id + "'");
        }

        return items.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }


    public static String listActionToActionStr(List<String> actions) {
        if (actions == null) {
            return "'";
        }
        if (actions.isEmpty()) {
            return "";
        }

        return actions.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(".", "", ""));
    }


    public static String argsToString(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }

    public static String argsToStringJoinDot(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append(".");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static ArrayList<String> getVarsNamesFromExpr(String string) {
        ArrayList<String> names = new ArrayList<>();
        final String regex = "(\\$([\\w]+))";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            names.add(matcher.group(2));
        }
        return names;
    }

    public static HashMap<String, Object> getVarsNamesFromExprAsHashMapNull(String string) {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        ArrayList<String> names = getVarsNamesFromExpr(string);
        if (!names.isEmpty()) {
            for (String name : names) {
                objectHashMap.put(name, null);
            }
        }
        return objectHashMap;
    }

    public static HashMap<String, Object> getVarsNamesFromExprAsHashMapNullReturnData(HashMap<String, Object> data, String expr) {
        HashMap<String, Object> exprVars = getVarsNamesFromExprAsHashMapNull(expr);
        exprVars.putAll(data);
        return exprVars;
    }

    public static String getTextSetVars(String text, HashMap<String, String> vars) {
        if (vars != null) {
            if (!vars.isEmpty()) {
                for (Map.Entry<String, String> var : vars.entrySet()) {
                    String key = var.getKey();
                    String value = var.getValue();
                    final String regex = "\\$" + key;
                    text = text.replaceAll(regex, value);
                }

            }
        }
        return text;
    }
}
