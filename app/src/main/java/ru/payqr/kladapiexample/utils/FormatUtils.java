package ru.payqr.kladapiexample.utils;

import ru.payqr.kladapiexample.realm.models.kladr.KladrResult;

/**
 * @author Savelii Zagurskii
 */
public class FormatUtils {
    /**
     * Get the format to represent on UI.
     *
     * @param result the instance that needs to be formatted.
     * @return formatted short address.
     */
    public static String getAutoCompleteFormat(KladrResult result) {
        return String.format("%1$s %2$s", result.getTypeShort(), result.getName());
    }
}