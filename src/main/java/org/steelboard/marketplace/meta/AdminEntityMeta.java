package org.steelboard.marketplace.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class AdminEntityMeta {

    private String table;
    private Class<?> entityClass;

    private Set<String> editableFields;
    private Set<String> readonlyFields;

    public boolean isEditable(String field) {
        return editableFields.contains(field);
    }
}
