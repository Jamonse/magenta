package com.jsoft.magenta.util;

import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.ColorTheme;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public class AppConstants
{
    // Validation messages

    public static final String TITLE_BLANK_MESSAGE = "Title must not be null or empty";
    public static final String TITLE_LENGTH_MESSAGE = "Title must contain at least 2 characters and maximum of 50";
    public static final String TITLE_DEFAULT_MESSAGE = "Title must not be null, empty or contain less than 2 or more than 50 characters";

    public static final String NAME_BLANK_MESSAGE = "Name must not be null or empty";
    public static final String NAME_LENGTH_MESSAGE = "Name must contain at least 2 characters and maximum of 50";
    public static final String NAME_DEFAULT_MESSAGE = "Name must not be null, empty or contain less than 2 or more than 50 characters";
    public static final String EMAIL_INVALID_MESSAGE = "Email pattern is invalid";
    public static final String PASSWORD_BLANK_MESSAGE = "Password must not be null or empty";

    public static final String CONTENT_LENGTH_MESSAGE = "Content must not exceed 255 characters";
    public static final String CONTENT_NULL_MESSAGE = "Content must not be null";

    public static final String PHONE_NUMBER_MESSAGE = "Phone number pattern is invalid";

    public static final String POSITIVE_NUMBER_MESSAGE = "Number must be greater than or equal to 0";

    public static final String PERMISSION_BLANK_MESSAGE = "Permission name must not be null or empty";

    public static final String AMOUNT_OF_HOURS_MESSAGE = "Amount of hours in single work time cannot exceed 24 hours";

    public static final String PERMISSION_NAME_MESSAGE = "Permission name does not exist";
    public static final String THEME_NAME_MESSAGE = "Theme name does not exist";

    public static final String SUPERVISOR_OR_OWNER_MESSAGE = "User is not supervisor or owner of requested resource";

    public static final String YEAR_MESSAGE = "Year value is not valid";
    public static final String MONTH_MESSAGE = "Month value is not valid";

    public static final String SECURITY_MESSAGE = "Error during keystore loading";

    public static final String ALIAS = "magenta";

    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";

    // Permission names

    public static final String ACCOUNT_PERMISSION = "account";
    public static final String PROJECT_PERMISSION = "project";
    public static final String POST_PERMISSION = "post";
    public static final String USER_PERMISSION = "user";

    public static final Set<String> PERMISSION_LEVEL_NAMES = Set.of(
            AccessPermission.READ.name(),
            AccessPermission.MANAGE.name(),
            AccessPermission.WRITE.name(),
            AccessPermission.ADMIN.name()
    );

    public static final Set<String> THEME_NAMES = Set.of(
            ColorTheme.LIGHT.name(),
            ColorTheme.DARK.name()
    );

    public static final double HOURS_IN_DAY = 24.0;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int HOURS_IN_WEEK = 168;
    public static final int HOURS_IN_BUSINESS_WEEK = 44;
    public static final int MAX_HOURS_IN_MONTH = 744;
    public static final int BUSINESS_DAY_HOURS = 9;
    public static final int SHORT_BUSINESS_DAY_HOURS = BUSINESS_DAY_HOURS - 1;

    public static final DayOfWeek SHORT_DAY = DayOfWeek.THURSDAY;
    public static final DayOfWeek FIRST_WD_DAY = DayOfWeek.FRIDAY;
    public static final DayOfWeek SECOND_WD_DAY = DayOfWeek.SATURDAY;

    public static final String WEEKLY_MAIL_MESSAGE = "Hello %s, we have a weekly mail for you from magenta!";

    // URL

    public static final String LOGIN_URL = "login";
    public static final String LOGOUT_URL = "auth/logout";
}
