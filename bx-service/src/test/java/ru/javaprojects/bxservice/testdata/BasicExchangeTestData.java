package ru.javaprojects.bxservice.testdata;

import ru.javaprojects.bxservice.service.client.UserBxDetails;

import java.time.LocalDate;

import static java.time.LocalDate.of;
import static java.time.Month.FEBRUARY;
import static ru.javaprojects.bxservice.service.client.UserBxDetails.Sex.MAN;
import static ru.javaprojects.bxservice.service.client.UserBxDetails.Sex.WOMAN;

public class BasicExchangeTestData {
    public static final long USER1_ID = 200000;
    public static final long USER2_ID = 200001;
    public static final String USER1_ID_STRING = "200000";

    public static final LocalDate FEBRUARY_5_2022 = of(2022, FEBRUARY, 5);
    public static final LocalDate FEBRUARY_6_2022 = of(2022, FEBRUARY, 6);
    public static final LocalDate FEBRUARY_7_2022 = of(2022, FEBRUARY, 7);

    public static final int USER1_BX_CALORIES = 1891;
    public static final int UPDATED_USER1_BX_CALORIES = 1866;
    public static final int USER2_BX_CALORIES = 1308;

    public static final String DATE = "2022-02-06";

    public static final UserBxDetails user1BxDetails = new UserBxDetails(MAN, 90, 185, 34);
    public static final UserBxDetails updatedUser1BxDetails = new UserBxDetails(MAN, 88, 185, 35);
    public static final UserBxDetails user2BxDetails = new UserBxDetails(WOMAN, 60, 163, 30);
}