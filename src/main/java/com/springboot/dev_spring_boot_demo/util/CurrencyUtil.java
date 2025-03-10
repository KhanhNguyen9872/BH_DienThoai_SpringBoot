package com.springboot.dev_spring_boot_demo.util;

import org.springframework.stereotype.Component;
import java.text.NumberFormat;
import java.util.Locale;

@Component("currencyUtil")
public class CurrencyUtil {
    public String formatVND(Number amount) {
        NumberFormat vnFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        vnFormat.setGroupingUsed(true);
        vnFormat.setMinimumFractionDigits(0);
        return vnFormat.format(amount) + " VNĐ";
    }
}
