package com.kms.katalon.util;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class VersionUtil {
    public static boolean isNewer(String version, String comparedVersion) {
        if (StringUtils.equals(version, comparedVersion)) {
            return false;
        }

        int[] thisVer = Arrays.stream(StringUtils.split(version, '.')).mapToInt(Integer::parseInt).toArray();

        int[] thatVer = Arrays.stream(StringUtils.split(comparedVersion, '.')).mapToInt(Integer::parseInt).toArray();
        
        int maxLength = Math.max(thisVer.length, thatVer.length);
        while (thisVer.length < maxLength) {
            thisVer = ArrayUtils.add(thisVer, 0);
        }
        
        while (thatVer.length < maxLength) {
            thatVer = ArrayUtils.add(thatVer, 0);
        }

        for (int i = 0; i < maxLength; i++) {
            if (thisVer[i] == thatVer[i]) {
                continue;
            }

            if (thisVer[i] > thatVer[i]) {
                return true;
            }
        }
        return false;
    }
}
