package shoppingcart;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    final static double TAX_RATE = 0.0825;

    public static void main(String[] args) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/resources/message.json");
        Object parse = jsonParser.parse(reader);
        JSONArray cartArray = (JSONArray) parse;

        FileReader couponReader = new FileReader("src/test/resources/coupon.json");
        Object couponParse = jsonParser.parse(couponReader);
        JSONArray couponArray = (JSONArray) couponParse;

        double taxRate = 0.0825;

        System.out.println("Feature 1 - Sum all item price");
        double totalPrice = getSubTotal(cartArray);
        System.out.printf("SubTotal:\t\t$ %.2f\n", totalPrice);

        System.out.println("Feature 2 - Grand total");
        printGrandTotal(totalPrice);

        System.out.println("Feature 3 - Taxable item grand total");
        double totalTaxableItemPrice = getTaxableSubTotal(cartArray);
        printGrandTotal(totalTaxableItemPrice);

        System.out.println("Feature 4 - apply coupon grand total");
        printGrandTotal(applyCoupon(cartArray, couponArray));
    }

    public static double getSubTotal(JSONArray arr) {
        double sum = 0;
        for (Object obj : arr) {
            JSONObject item = (JSONObject) obj;
            sum += (Double) item.get("price");
        }
        return sum;
    }

    public static void printGrandTotal(double totalPrice) {
        System.out.printf("SubTotal:\t\t$ %.2f\n", totalPrice);
        double totalTax = totalPrice * TAX_RATE;
        double grandTotal = totalPrice + totalTax;
        System.out.printf("Taxes:\t\t\t$ %.2f\n", totalTax);
        System.out.printf("Grand Total:\t$ %.2f\n", grandTotal);
    }

    public static double getTaxableSubTotal(JSONArray arr) {
        double sum = 0;
        for (Object obj : arr) {
            JSONObject item = (JSONObject) obj;
            boolean isTaxable = (Boolean) item.get("isTaxable");
            if (isTaxable) sum += (Double) item.get("price");
        }
        return sum;
    }

    public static double applyCoupon(JSONArray itemArr, JSONArray couponArr) {
        double sum = 0;
        double totalDiscount = 0;
        for (Object obj : itemArr) {
            boolean discountFlag = false;
            JSONObject item = (JSONObject) obj;
            String itemName = (String) item.get("itemName");
            Double itemPrice = (Double) item.get("price");
            for (Object couponObj : couponArr) {
                JSONObject coupon = (JSONObject) couponObj;
                String couponName = String.valueOf(coupon.get("couponName")).split(" ")[0];
                Double discountPrice = (Double) coupon.get("discountPrice");
                if (itemName.contains(couponName)) {
                    System.out.println("Discount applied to " + itemName);
                    sum += (itemPrice - discountPrice);
                    totalDiscount += discountPrice;
                    discountFlag = true;
                    break;
                }
            }
            if (!discountFlag) sum += itemPrice;
        }
        System.out.printf("Total Discount:\t$ %.2f\n",totalDiscount);
        return sum;
    }
}
