package cd.go.authentication.ldap.annotation;


import java.lang.reflect.Field;
import java.util.*;

public class MetadataHelper {

    public static List<Configuration> getMetadata(Class<?> clazz) {
        return buildMetadata(clazz);
    }

    private static List<Configuration> buildMetadata(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Configuration> metadata = new ArrayList<>();
        for (Field field : fields) {
            ProfileField profileField = field.getAnnotation(ProfileField.class);
            if (profileField != null) {
                final ProfileMetadata profileMetadata = new ProfileMetadata(profileField.required(), profileField.secure(), profileField.type());
                final Configuration<ProfileMetadata> configuration = new Configuration<>(profileField.key(), profileMetadata);
                metadata.add(configuration);
            }
        }
        return metadata;
    }

    public static List<Map<String, String>> validate(Class<?> clazz, Map<String, String> configuration) {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> knownFields = new ArrayList<>();

        for (Configuration field : getMetadata(clazz)) {
            knownFields.add(field.getKey());

            Map<String, String> validationError = field.validate(configuration.get(field.getKey()));

            if (!validationError.isEmpty()) {
                result.add(validationError);
            }
        }


        Set<String> set = new HashSet<>(configuration.keySet());
        set.removeAll(knownFields);

        if (!set.isEmpty()) {
            for (String key : set) {
                LinkedHashMap<String, String> validationError = new LinkedHashMap<>();
                validationError.put("key", key);
                validationError.put("message", "Is an unknown property");
                result.add(validationError);
            }
        }
        return result;
    }
}
