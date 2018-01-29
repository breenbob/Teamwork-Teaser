package conorbreen.com.teamworkteaser.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import conorbreen.com.teamworkteaser.models.Project;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class ProjectDeserializationStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        if (f.getDeclaringClass() == Project.class) {
            if (f.getName().equals("companyId")) {
                return true;
            } else
            if (f.getName().equals("category-id")) {
                return true;
            }
        }

        return false;
    }
}
