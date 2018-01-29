package conorbreen.com.teamworkteaser.gson;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Conor Breen on 29/01/2018.
 */

/**
 * Some dates returned by API had ISO 8601 format, others had yyyyMMdd format, and some were completely empty.
 * This allows for all of the above!
 */
public class MultiFormatDateDeserializer implements JsonDeserializer<Date> {
    private static final String[] SUPPORTED_DATE_FORMATS = new String[] {
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyyMMdd"
    };

    @Override
    public Date deserialize(JsonElement jsonElement, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        String jsonString = jsonElement.getAsString();
        if (!TextUtils.isEmpty(jsonString)) {
            for (String format : SUPPORTED_DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format, Locale.UK).parse(jsonString);
                } catch (ParseException e) {
                    Timber.v("Parsing date '%s' in format '%s' failed.", jsonString, format);
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: " + Arrays.toString(SUPPORTED_DATE_FORMATS));
        }

        // allows for empty dates also
        return null;
    }
}
