package conorbreen.com.teamworkteaser.retrofit.converterFactories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import conorbreen.com.teamworkteaser.utils.EnumUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * A converter to help Retrofit 2.0 translate any enum that utilises the SerializedName attribute into String parameters
 */
public class EnumConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter<?, String> converter = null;
        if (type instanceof Class && ((Class<?>)type).isEnum()) {
            converter = value -> EnumUtils.getSerializedName((Enum) value);
        }
        return converter;
    }
}
