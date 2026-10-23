package util;

import java.util.Locale;

/** Formats numbers as Indian rupees with lakh/crore grouping, e.g. ₹2,45,500.00 */
public class Money {

    public static String format(double amount) {
        boolean negative = amount < 0;
        String text = String.format(Locale.US, "%.2f", Math.abs(amount));   // e.g. "245500.00"

        int dot = text.indexOf('.');
        String whole = text.substring(0, dot);       // "245500"
        String decimals = text.substring(dot);       // ".00"

        String grouped = whole;
        if (whole.length() > 3) {
            String lastThree = whole.substring(whole.length() - 3);     // "500"
            String rest = whole.substring(0, whole.length() - 3);       // "245"
            StringBuilder sb = new StringBuilder();
            while (rest.length() > 2) {                                 // groups of two
                sb.insert(0, "," + rest.substring(rest.length() - 2));
                rest = rest.substring(0, rest.length() - 2);
            }
            sb.insert(0, rest);
            grouped = sb + "," + lastThree;                             // "2,45,500"
        }
        return (negative ? "-" : "") + "\u20B9" + grouped + decimals;
    }
}
