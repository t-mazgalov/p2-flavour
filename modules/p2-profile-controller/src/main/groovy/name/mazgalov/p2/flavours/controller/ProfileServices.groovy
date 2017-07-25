package name.mazgalov.p2.flavours.controller

import name.mazgalov.p2.flavours.operations.ExistingResourceException
import name.mazgalov.p2.flavours.operations.Profile
import name.mazgalov.p2.flavours.operations.ProfilesCache
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType

/**
 * Created on 19-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Path("profiles")
@Component(immediate = true, service = ProfileServices.class, property = 'service.type=jersey')
class ProfileServices {
    private ProfilesCache profilesCache = null

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    def listProfiles() {
        profilesCache.profiles
    }

    @GET
    @Path("current")
    @Produces(MediaType.APPLICATION_JSON)
    def currentProfile() {
        profilesCache.runningProfile
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    def createProfile(Profile profile) {
        try {
            profilesCache.createProfile(profile.name, profile.location)
        } catch (ExistingResourceException e) {
            throw new WebApplicationException(e)
        }
    }

    @PUT
    @Path("change/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    def changeProfile(@PathParam("id") long id) {
        try {
            profilesCache.setRunningProfile(id)
        } catch (ExistingResourceException e) {
            throw new WebApplicationException(e)
        }
    }

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            unbind = 'unbindProfilesCache')
    def bindProfilesCache(ProfilesCache profilesCache, Map<String, ?> properties) {
        this.profilesCache = profilesCache
    }

    def unbindProfilesCache(ProfilesCache profilesCache, Map<String, ?> properties) {
        this.profilesCache = null
    }
}
