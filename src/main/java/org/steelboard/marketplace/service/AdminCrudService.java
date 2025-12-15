package org.steelboard.marketplace.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.steelboard.marketplace.meta.AdminEntityMeta;
import org.steelboard.marketplace.meta.AdminMetaRegistry;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminCrudService {

    @PersistenceContext
    private EntityManager em;

    private final AdminMetaRegistry registry;

    public AdminCrudService(AdminMetaRegistry registry) {
        this.registry = registry;
    }

    public Iterable<?> findAll(String table) {
        AdminEntityMeta meta = registry.get(table);
        return em.createQuery("from " + meta.getEntityClass().getSimpleName())
                .getResultList();
    }

    public Iterable<Map<String, Object>> findAllAsMaps(String table) {
        AdminEntityMeta meta = registry.get(table);

        List<?> entities = em.createQuery(
                "from " + meta.getEntityClass().getSimpleName()
        ).getResultList();

        return entities.stream()
                .map(this::extractFields)
                .toList();
    }


    public Object findById(String table, Long id) {
        return em.find(registry.get(table).getEntityClass(), id);
    }

    public Map<String, Object> extractFields(Object entity) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(entity));
            } catch (Exception ignored) {}
        }
        return map;
    }

    public void update(String table, Long id, Map<String, String> values) {
        AdminEntityMeta meta = registry.get(table);
        Object entity = em.find(meta.getEntityClass(), id);

        values.forEach((field, value) -> {
            if (!meta.isEditable(field)) return;

            Field f = ReflectionUtils.findField(meta.getEntityClass(), field);
            f.setAccessible(true);
            ReflectionUtils.setField(f, entity, convert(value, f.getType()));
        });
    }

    private Object convert(String v, Class<?> t) {
        if (t.equals(String.class)) return v;
        if (t.equals(Integer.class)) return Integer.valueOf(v);
        if (t.equals(Long.class)) return Long.valueOf(v);
        if (t.equals(Double.class)) return Double.valueOf(v);
        if (t.equals(Boolean.class)) return Boolean.valueOf(v);
        if (t.isEnum()) return Enum.valueOf((Class<Enum>) t, v);
        return null;
    }
}
