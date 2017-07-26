package name.mazgalov.p2.flavours.controller.internal

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.eclipse.equinox.internal.p2.metadata.OSGiVersion

import java.lang.reflect.Type

/**
 * Created on 25-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class OsgiVersionSerializer implements JsonSerializer {
    @Override
    JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        def osgiVersion = src as OSGiVersion
        JsonObject versionObject = new JsonObject()
        versionObject.addProperty('major', osgiVersion.major)
        versionObject.addProperty('minor', osgiVersion.minor)
        versionObject.addProperty('micro', osgiVersion.micro)
        versionObject.addProperty('qualifier', osgiVersion.qualifier)

        versionObject
    }
}
