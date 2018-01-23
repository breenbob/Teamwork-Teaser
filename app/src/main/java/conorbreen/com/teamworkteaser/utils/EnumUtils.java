package conorbreen.com.teamworkteaser.utils;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

/**
 * Created by Conor Breen on 22/01/2018.
 */

public class EnumUtils {
    @Nullable
    static public <T extends Enum<T>> String GetSerializedName(T t) {
        String value = null;

        try {
            value = t.getClass().getField(t.name()).getAnnotation(SerializedName.class).value();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }

        return value;
    }
}
