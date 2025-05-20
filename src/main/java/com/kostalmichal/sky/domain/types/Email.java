package com.kostalmichal.sky.domain.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kostalmichal.sky.exception.InvalidEmailValueException;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonSerialize(using = EmailSerializer.class)
@JsonDeserialize(using = EmailDeserializer.class)
@Embeddable
public class Email {
    private static final String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private String value;

    protected Email() {

    }

    public Email(String value) {
        if (!isValid(value)) {
            throw new InvalidEmailValueException(value);
        }
        this.value = value;
    }

    private boolean isValid(String email) {
        return email != null && Pattern.compile(regexPattern)
            .matcher(email)
            .matches();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return  Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
