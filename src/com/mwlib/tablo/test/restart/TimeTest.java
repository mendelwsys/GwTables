package com.mwlib.tablo.test.restart;

import java.util.Date;

/**
 * Created by Anton.Pozdnev on 21.07.2015.
 */
public class TimeTest {


    static int startHour = 1;
    static int periodHours = 2;

    public static void main(String[] args) {
        //Tests
        System.out.println(shouldRestart(new Date(2015 - 1970, 1, 1, 12, 25))); // false;
        System.out.println(shouldRestart(new Date(2015 - 1970, 1, 1, 1, 25))); // true;
        System.out.println(shouldRestart(new Date(2015 - 1970, 1, 1, 5, 25))); // true;
        System.out.println(shouldRestart(new Date(2015 - 1970, 1, 1, 23, 25))); // true;
        System.out.println(shouldRestart(new Date(2015 - 1970, 1, 1, 0, 25))); // false;
        System.out.println(shouldRestart(new Date(2015 - 1970, 1, 1, 4, 25))); // false;

    }


    static boolean shouldRestart(Date startClientDate) {


        int currentHour = startClientDate.getHours();


        int hoursCounter = startHour;


        for (int i = 0; i < 24; i++) {
            if (currentHour == hoursCounter) return true;
            else {
                hoursCounter += periodHours;
                if (hoursCounter >= 24) hoursCounter -= 24;

            }

        }


        return false;
    }
}
