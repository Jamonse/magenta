package com.jsoft.magenta.util;

public class AppDefaults
{
    public static final String PAGE_SIZE = "5";
    public static final String PAGE_INDEX = "0";
    public static final String ASCENDING_SORT = "false";
    public static final String RESULTS_COUNT = "5";

    public static final String PROJECTS_DEFAULT_SORT = "name";
    public static final String USER_DEFAULT_FIRST_SORT = "firstName";
    public static final String USER_DEFAULT_SECOND_SORT = "lastName";
    public static final String USER_DEFAULT_SORT_NAME = "name";
    public static final String CONTACT_DEFAULT_SORT_NAME = "name";
    public static final String PRIVILEGES_GROUP_DEFAULT_SORT = "name";
    public static final String ACCOUNTS_DEFAULT_SORT = "name";
    public static final String DEFAULT_ORDER_SORT = "title";
    public static final String DEFAULT_POST_SORT = "title";
    public static final String WORK_PLANS_DEFAULT_SORT = "title";

    public static final String[] USER_DEFAULT_SORT = {
            AppDefaults.USER_DEFAULT_FIRST_SORT,
            AppDefaults.USER_DEFAULT_SECOND_SORT
        };
    public static final String[] CONTACT_DEFAULT_SORT = {
            AppDefaults.USER_DEFAULT_FIRST_SORT,
            AppDefaults.USER_DEFAULT_SECOND_SORT
    };

    public static final int MAX_CONTENT_SIZE = 256;

    public static final int MIN_NAME_SIZE = 2;
    public static final int MAX_NAME_SIZE = 50;
}
