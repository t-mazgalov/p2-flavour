package name.mazgalov.p2.flavours.controller.internal

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.eclipse.equinox.internal.p2.metadata.BasicVersion
import org.eclipse.equinox.internal.p2.metadata.OSGiVersion

import java.lang.reflect.Type

/**
 * Created on 25-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class BasicVersionSerializer implements JsonSerializer {
    @Override
    JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        def basicVersion = src as BasicVersion
        JsonObject versionObject = new JsonObject()
        versionObject.addProperty('major', basicVersion.major)
        versionObject.addProperty('minor', basicVersion.minor)
        versionObject.addProperty('micro', basicVersion.micro)
        versionObject.addProperty('qualifier', basicVersion.qualifier)

        versionObject
    }
}
