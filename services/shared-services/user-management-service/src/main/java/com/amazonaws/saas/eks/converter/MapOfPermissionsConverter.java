package com.amazonaws.saas.eks.converter;

import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.util.Utils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.util.HashMap;
import java.util.Map;

public class MapOfPermissionsConverter implements DynamoDBTypeConverter<Map<String, Map<String, String>>, Map<String, Permission>> {
    @Override
    public Map<String, Map<String, String>> convert(Map<String, Permission> obj) {
        Map<String, Map<String, String>> result = new HashMap<>();

        Map<String, String> pMap;
        for (Permission p: obj.values()) {
            pMap = new HashMap<>(){{
                put(Permission.AttributeNames.DESCRIPTION, p.getDescription());
                put(Permission.AttributeNames.GROUP, p.getGroup());
                put(Permission.AttributeNames.LABEL, p.getLabel());
                put(Permission.AttributeNames.CREATED, Utils.toISO8601UTC(p.getCreated()));
                put(Permission.AttributeNames.MODIFIED, Utils.toISO8601UTC(p.getModified()));
            }};
            result.put(p.getName(), pMap);
        }

        return result;
    }

    @Override
    public Map<String, Permission> unconvert(Map<String, Map<String, String>> obj) {
        Map<String, Permission> permissions = new HashMap<>();

        Permission p;
        for (Map.Entry<String, Map<String, String>> e: obj.entrySet()) {
            p = new Permission();
            p.setName(e.getKey());
            p.setDescription(e.getValue().get(Permission.AttributeNames.DESCRIPTION));
            p.setGroup(e.getValue().get(Permission.AttributeNames.GROUP));
            p.setLabel(e.getValue().get(Permission.AttributeNames.LABEL));
            p.setCreated(Utils.fromISO8601UTC(e.getValue().get(Permission.AttributeNames.CREATED)));
            p.setModified(Utils.fromISO8601UTC(e.getValue().get(Permission.AttributeNames.MODIFIED)));

            permissions.put(p.getName(), p);
        }
        return permissions;
    }
}
