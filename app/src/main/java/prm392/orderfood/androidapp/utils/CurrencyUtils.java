package prm392.orderfood.androidapp.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    /**
     * Chuyển từ chuỗi số (VD: "20000.0") sang định dạng tiền Việt (VD: "20.000 VND")
     */
    public static String formatToVND(String price) {
        try {
            double amount = Double.parseDouble(price);
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            return formatter.format((long) amount) + " VND";
        } catch (NumberFormatException e) {
            return "0 VND";
        }
    }

    /**
     * Chuyển từ số double (VD: 20000.0) sang định dạng tiền Việt (VD: "20.000 VND")
     */
    public static String formatToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format((long) price) + " VND";
    }

    /**
     * Chuyển từ định dạng tiền Việt (VD: "20.000 VND") sang chuỗi số (VD: "20000")
     */
    public static String parseVNDToRaw(String priceStr) {
        try {
            // Loại bỏ chữ "VND" và khoảng trắng
            String numericPart = priceStr.replace("VND", "").trim();
            // Loại bỏ dấu chấm ngăn cách hàng nghìn
            return numericPart.replace(".", "");
        } catch (Exception e) {
            return "0";
        }
    }
}
