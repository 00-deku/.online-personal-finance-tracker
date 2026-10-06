package com.finance.util;

import java.util.UUID;

/**
 * Utility helper to generate unique primary key IDs for database entities.
 * Ensures IDs fit comfortably within the VARCHAR(30) database column constraint.
 */
public class IDGenerator {

    /**
     * Generates a unique string ID with a given prefix.
     * Example output: "USR_171234567890" or "EXP_a1b2c3d4"
     * 
     * @param prefix Entity prefix (e.g., "USR", "EXP", "BGT", "ADV", "FBK")
     * @return Unique ID string under 30 characters
     */
    public static String generateId(String prefix) {
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String id = prefix + "_" + randomPart;
        if (id.length() > 30) {
            return id.substring(0, 30);
        }
        return id;
    }
}
