package fr.xebia.java8.refactoring.step2;


import fr.xebia.java8.refactoring.data.Role;
import fr.xebia.java8.refactoring.data.User;
import fr.xebia.java8.refactoring.data.UsersAgeStatistic;
import fr.xebia.java8.refactoring.other.UserParser;

import java.util.*;

public class UserService {

    private static final String DEFAULT_FORMATED_ADDRESS = "1 rue de Rivoli\n75001 Paris";
    private List<User> users;

    public UserService() {
        users = UserParser.fromCsv("users.csv");
    }

    //TODO: convert users list to stream and use filter and count
    public long countUserWithRole(Role role) {
        long count = 0;
        for (User user : users) {
            if (user.getRole() == role) {
                count++;
            }
        }

        return count;
    }

    //TODO: use anyMatch
    public boolean isLoginAlreadyExist(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return true;
            }
        }

        return false;
    }

    //TODO: use user.getOptionalAddress() instead of user.getAddress(), use filter and findFirst
    //TODO: to finish this refactoring you need Optional.flatMap, Optional.Map, and Optional.orElse
    public String retrieveFormattedUserAddressByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                if (user.getAddress() != null) {
                    return user.getAddress().formatForEnveloppe();
                }
            }
        }

        return DEFAULT_FORMATED_ADDRESS;
    }

    /**
     * @return first 50 users ordered by first name and last name
     */
    //TODO: use sorted and replace specific comparator with Comparator.comparing static methods
    //TODO: use limit method and collect with Collectors to generate a new List
    public List<User> firstFiftyUsers() {
        List<User> usersOrdered = new ArrayList<>(users.size());
        usersOrdered.addAll(users);

        Collections.sort(usersOrdered, new UserComparator());

        return usersOrdered.subList(0, 50);
    }

    private static class UserComparator implements Comparator<User> {

        public int compare(User userLeft, User userRight) {
            int lastNameComparison = userLeft.getLastname().compareTo(userRight.getLastname());
            if (lastNameComparison == 0) {
                return userLeft.getFirstname().compareTo(userRight.getFirstname());
            } else {

                return lastNameComparison;
            }
        }
    }

    //TODO: Use filter on expired method and collect with Collectors.groupingBy
    public Map<Role, List<User>> retrieveActiveUserByRole() {
        Map<Role, List<User>> result = new HashMap<>();

        for (User user : users) {
            if (!user.isExpired()) {

                List<User> currentRoleUsers = result.get(user.getRole());
                if (currentRoleUsers == null) {
                    currentRoleUsers = new ArrayList<>();
                    result.put(user.getRole(), currentRoleUsers);
                }
                currentRoleUsers.add(user);
            }
        }

        return result;
    }

    //TODO: Use filter on role and collect with Collectors.toMap and Function.identity() as value mapper
    public Map<String, User> retrieveUserWithRoleByLogin(Role role) {
        Map<String, User> result = new HashMap<>();

        for (User user : users) {
            if (user.getRole() == role) {
                result.put(user.getLogin(), user);
            }
        }

        return result;
    }

    //TODO: use collect with Collectors.summarizingInt
    //TODO: use returned IntSummaryStatistics object to create UsersAgeStatistic object
    public UsersAgeStatistic generateAgeStatistic() {
        long count = 0;
        int min = Integer.MAX_VALUE;
        int max = 0;
        int sum = 0;

        for (User user : users) {
            int age = user.age();
            if (age > max) {
                max = age;
            }
            if (age < min) {
                min = age;
            }
            count++;
            sum += age;
        }
        double average = (double) sum / count;

        return new UsersAgeStatistic(count, min, max, average);
    }
}
