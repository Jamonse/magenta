package com.jsoft.magenta.users;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {

  @Override
  public int compare(User user1, User user2) {
    int result = user1.getFirstName().compareToIgnoreCase(user2.getFirstName());
    return result != 0 ?
        result :
        user1.getLastName().compareToIgnoreCase(user2.getLastName());
  }
}
