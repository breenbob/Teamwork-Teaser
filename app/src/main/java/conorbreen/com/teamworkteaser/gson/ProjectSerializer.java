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
    private SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.UK);
    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK);

    @Override
    public JsonElement serialize(final Project value, final Type type, final JsonSerializationContext context) {
        // Use default serialization as the basis - treat is as non-Project class to avoid StackOverflowException!
        final JsonObject jsonObj = new JsonObject();

        jsonObj.addProperty("id", value.getId());
        jsonObj.addProperty("companyId", value.getCompanyId());
        jsonObj.addProperty("starred", value.isStarred());
        jsonObj.addProperty("name", value.getName());
        jsonObj.addProperty("show-announcement", value.isShowAnnouncement());
        jsonObj.addProperty("announcement", value.getAnnouncement());
        jsonObj.addProperty("description", value.getDescription());
        jsonObj.addProperty("status", value.getStatus());
        jsonObj.addProperty("subStatus", value.getSubStatus());
        jsonObj.addProperty("isProjectAdmin", value.isProjectAdmin());
        jsonObj.addProperty("startPage", value.getStartPage());
        jsonObj.addProperty("logo", value.getLogo());
        jsonObj.addProperty("notifyeveryone", value.isNotifyEveryone());
        jsonObj.addProperty("harvest-timers-enabled", value.isHarvestTimersEnabled());
        jsonObj.addProperty("categoryId", value.getCategoryId());
        jsonObj.addProperty("replyByEmailEnabled", value.isReplyByEmailEnabled());
        jsonObj.addProperty("privacyEnabled", value.isPrivacyEnabled());

        if (value.getCompany() != null) {
            jsonObj.add("company", context.serialize(value.getCompany()));
        }

        if (value.getTags() != null) {
            jsonObj.add("tags", context.serialize(value.getTags()));
        }

        if (value.getCategory() != null) {
            jsonObj.add("category", context.serialize(value.getCategory()));
        }

        // Reason for custom json serializer - to handle the 2 (optional) date formats used:
        if (value.getStartDate() != null) {
            jsonObj.addProperty("startDate", shortDateFormat.format(value.getStartDate()));
        } else {
            jsonObj.addProperty("startDate", "");
        }
        if (value.getEndDate() != null) {
            jsonObj.addProperty("endDate", shortDateFormat.format(value.getEndDate()));
        } else {
            jsonObj.addProperty("endDate", "");
        }
        if (value.getCreatedOn() != null) {
            jsonObj.addProperty("created-on", isoDateFormat.format(value.getCreatedOn()));
        } else {
            jsonObj.addProperty("created-on", "");
        }
        if (value.getLastChangedOn() != null) {
            jsonObj.addProperty("last-changed-on", isoDateFormat.format(value.getLastChangedOn()));
        } else {
            jsonObj.addProperty("last-changed-on", "");
        }

        return jsonObj;
    }
}
