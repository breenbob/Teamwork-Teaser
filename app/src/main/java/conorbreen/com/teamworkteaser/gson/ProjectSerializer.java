package conorbreen.com.teamworkteaser.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

import conorbreen.com.teamworkteaser.models.Project;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class ProjectSerializer implements JsonSerializer<Project> {
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.UK);

    @Override
    public JsonElement serialize(final Project value, final Type type, final JsonSerializationContext context) {
        // Use default serialization as the basis
        final JsonObject jsonObj = context.serialize(value).getAsJsonObject();

        // Only way to cater for serialization of multiple date formats
        jsonObj.remove("startDate");
        jsonObj.remove("endDate");
        jsonObj.remove("created-on");
        jsonObj.remove("last-changed-on");

        if (value.getStartDate() != null) {
            jsonObj.addProperty("startDate", df.format(value.getStartDate()));
        } else {
            jsonObj.addProperty("startDate", "");
        }
        if (value.getEndDate() != null) {
            jsonObj.addProperty("endDate", df.format(value.getEndDate()));
        } else {
            jsonObj.addProperty("endDate", "");
        }
        if (value.getCreatedOn() != null) {
            jsonObj.addProperty("created-on", df.format(value.getCreatedOn()));
        } else {
            jsonObj.addProperty("created-on", "");
        }
        if (value.getLastChangedOn() != null) {
            jsonObj.addProperty("last-changed-on", df.format(value.getLastChangedOn()));
        } else {
            jsonObj.addProperty("last-changed-on", "");
        }

        return jsonObj;
    }
}
