package com.pdsa.touristappbackend.model;

import java.io.Serializable;
import java.util.Objects;


/**
 * Composite key class for UserInterest entity
 * Consists of userId and interestId
 * Implements Serializable and overrides equals and hashCode methods
 * Used for mapping many-to-many relationship between User and Interest entities
 * Fields:
 * - userId: ID of the user
 * - interestId: ID of the interest
 */
public class UserInterestId implements Serializable {
    private Long userId;
    private Long interestId;

    public UserInterestId() {}

    public UserInterestId(Long userId, Long interestId) {
        this.userId = userId;
        this.interestId = interestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInterestId)) return false;
        UserInterestId that = (UserInterestId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(interestId, that.interestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, interestId);
    }
}
