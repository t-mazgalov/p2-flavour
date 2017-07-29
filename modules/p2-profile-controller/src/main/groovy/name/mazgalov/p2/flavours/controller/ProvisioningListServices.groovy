package name.mazgalov.p2.flavours.controller

import com.google.gson.Gson
import name.mazgalov.p2.flavours.controller.internal.ProfileProvisioningContext
import name.mazgalov.p2.flavours.operations.ExistingResourceException
import name.mazgalov.p2.flavours.operations.FrameworkOperator
import name.mazgalov.p2.flavours.operations.MetadataRepositoryOperator
import name.mazgalov.p2.flavours.operations.ProfilesCache
import name.mazgalov.p2.flavours.operations.ProvisioiningListsCache
import name.mazgalov.p2.flavours.operations.ProvisioningOptions
import name.mazgalov.p2.flavours.operations.SimplifiedInstallableUnit
import name.mazgalov.p2.flavours.operations.SimplifiedProvisioningList
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created on 24-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Path("provisioning-lists")
@Component(immediate = true, service = ProvisioningListServices.class, property = 'service.type=jersey')
class ProvisioningListServices {
    private ProvisioiningListsCache provisioningListsCache = null
    private MetadataRepositoryOperator metadataRepositoryOperator = null
    private ProfilesCache profilesCache = null
    private FrameworkOperator frameworkOperator = null

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    def listProvisioningLists() {
        provisioningListsCache.simplifiedProvisioningLists
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    def getProvisioningList(@PathParam("id") long id) {
        provisioningListsCache.getSimplifiedProvisioningList(id)
    }

    @POST
    @Path("addIUs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    def addInstallableUnitsInProvisioningList(SimplifiedProvisioningList provisioningList) {
        provisioningListsCache.addSimplifiedInstallableUnits(provisioningList.id, provisioningList.installableUnits)
    }

    @POST
    @Path("removeIUs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    def removeInstallableUnitsFromProvisioningList(SimplifiedProvisioningList provisioningList) {
        provisioningListsCache.removeSimplifiedInstallableUnits(provisioningList.id, provisioningList.installableUnits)
    }

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    def createProvisioningList(SimplifiedProvisioningList provisioningList) {
        def createdProvisioningList
        try {
            createdProvisioningList = provisioningListsCache
                    .createSimplifiedProvisioningList(
                    provisioningList.name, provisioningList.installableUnits)
        } catch (ExistingResourceException e) {
            throw new WebApplicationException(e)
        }

        createdProvisioningList
    }

    @POST
    @Path("provision")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    def provisionProvisioningList(ProfileProvisioningContext profileProvisioningContext) {
        Map<String, List<SimplifiedInstallableUnit>> repositories = [:]
        profileProvisioningContext.simplifiedProvisioningList.installableUnits.each {
            if (repositories[it.repository] == null)
                repositories[it.repository] = []
            repositories[it.repository] << it
        }

        List<InstallableUnit> ius = []
        repositories.each { key, value ->
            IMetadataRepository metadataRepository =
                    metadataRepositoryOperator.refreshRepository(
                            new URI(key),
                            profilesCache.getProfile(profilesCache.runningProfile.id).agent)
            ius += metadataRepositoryOperator.getInstallableUnits(metadataRepository, value)
        }
        ProvisioningOptions provisioningOptions = new ProvisioningOptions(
                metadataRepositories: repositories.collect {new URI(it.key)},
                artifactRepositories: repositories.collect {new URI(it.key)},
                profileId: profileProvisioningContext.profile.name,
                agent: profilesCache.getProfile(profileProvisioningContext.profile.id).agent,
                installableUnits: ius)

        new Gson().toJson(frameworkOperator.installProfile(provisioningOptions))
    }

    @PUT
    @Path("extend/{fromListId}/{toListId}")
    @Consumes(MediaType.APPLICATION_JSON)
    def extendProvisioningList(@PathParam("fromListId") long fromListId, @PathParam("toListId") long toListId) {
        provisioningListsCache.extendList(fromListId, toListId)
    }

    @PUT
    @Path("shrink/{fromListId}/{toListId}")
    @Consumes(MediaType.APPLICATION_JSON)
    def shrinkProvisioningList(@PathParam("fromListId") long fromListId, @PathParam("toListId") long toListId) {
        provisioningListsCache.shrinkList(fromListId, toListId)
    }

    @DELETE
    @Path("remove/{id}")
    def removeRepository(@PathParam("id") long id) {
        provisioningListsCache.removeSimplifiedProvisioningList(id)
    }

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            unbind = 'unbindProvisioningListsCache')
    def bindProvisioningListsCache(ProvisioiningListsCache provisioningListsCache, Map<String, ?> properties) {
        this.provisioningListsCache = provisioningListsCache
    }

    def unbindProvisioningListsCache(ProvisioiningListsCache provisioningListsCache, Map<String, ?> properties) {
        this.provisioningListsCache = null
    }

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            unbind = 'unbindMetadataRepositoryOperator')
    def bindMetadataRepositoryOperator(
            MetadataRepositoryOperator metadataRepositoryOperator,
            Map<String, ?> properties) {
        this.metadataRepositoryOperator = metadataRepositoryOperator
    }

    def unbindMetadataRepositoryOperator(
            MetadataRepositoryOperator metadataRepositoryOperator,
            Map<String, ?> properties) {
        this.metadataRepositoryOperator = null
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

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            unbind = 'unbindFrameworkOperator')
    def bindFrameworkOperator(FrameworkOperator frameworkOperator, Map<String, ?> properties) {
        this.frameworkOperator = frameworkOperator
    }

    def unbindFrameworkOperator(FrameworkOperator frameworkOperator, Map<String, ?> properties) {
        this.frameworkOperator = null
    }
}
