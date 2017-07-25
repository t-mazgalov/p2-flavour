package name.mazgalov.p2.flavours.controller

import name.mazgalov.p2.flavours.operations.ExistingResourceException
import name.mazgalov.p2.flavours.operations.ProvisioiningListsCache
import name.mazgalov.p2.flavours.operations.SimplifiedInstallableUnit
import name.mazgalov.p2.flavours.operations.SimplifiedProvisioningList
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
                    provisioningList.name, provisioningList.profileId, provisioningList.installableUnits)
        } catch (ExistingResourceException e) {
            throw new WebApplicationException(e)
        }

        createdProvisioningList
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
}
