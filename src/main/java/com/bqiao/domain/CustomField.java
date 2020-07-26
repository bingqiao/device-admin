package com.bqiao.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CustomFieldIdentity.class)
@EntityListeners(AuditingEntityListener.class)
public class CustomField {

    public static final int START_INDEX_OF_CUSTOM_FIELD = 12;
    public static final String CUSTOM_FIELD_NULL_VALUE = "null";
    public static int NUMBER_OF_CUSTOM_FIELDS = 5;

    public CustomField(String value) {
        this.value = value;
    }

    @JsonIgnore
    @Id
    private String deviceId;
    @JsonIgnore
    @Id
    private int fieldOrder;
    private String value;

    @CreatedDate
    private long created;

    @LastModifiedDate
    private long modified;

    public static Map<Integer, String> parse(String[] fields) {
        Map<Integer, String> fieldValues = new HashMap<>();
        if (fields == null || fields.length <= START_INDEX_OF_CUSTOM_FIELD) {
            return fieldValues;
        }
        for (int i = START_INDEX_OF_CUSTOM_FIELD; i < fields.length && i < START_INDEX_OF_CUSTOM_FIELD + NUMBER_OF_CUSTOM_FIELDS; i++) {
            if (!CUSTOM_FIELD_NULL_VALUE.equals(fields[i])) {
                fieldValues.put(i - START_INDEX_OF_CUSTOM_FIELD, fields[i]);
            }
        }
        return fieldValues;
    }
}
