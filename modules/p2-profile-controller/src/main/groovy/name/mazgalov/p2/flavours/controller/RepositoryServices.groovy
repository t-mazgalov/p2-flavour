package name.mazgalov.p2.flavours.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import name.mazgalov.p2.flavours.controller.internal.*
import name.mazgalov.p2.flavours.operations.ArtifactsRepositoryOperator
import name.mazgalov.p2.flavours.operations.MetadataRepositoryOperator
import name.mazgalov.p2.flavours.operations.Profile
import name.mazgalov.p2.flavours.operations.ProfilesCache
import org.eclipse.equinox.internal.p2.metadata.OSGiVersion
import org.eclipse.equinox.p2.metadata.IInstallableUnit
import org.eclipse.equinox.p2.metadata.IRequirement
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created on 05-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Path("repos")
@Component(immediate = true, service = RepositoryServices.class, property = 'service.type=jersey')
class RepositoryServices {
    private ProfilesCache profilesCache = null
    private MetadataRepositoryOperator metadataRepositoryOperator = null
    private ArtifactsRepositoryOperator artifactsRepositoryOperator = null

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    def listRepositories() {
        def profilesList = []
        profilesCache.profiles.each { Profile profile ->
            def profileMap = [:]
            profileMap['profile'] = profile
            profileMap['metadataRepositories'] = metadataRepositoryOperator.getKnownRepositories profile.agent
            profileMap['artifactsRepositories'] = artifactsRepositoryOperator.getKnownRepositories profile.agent

            profilesList << profileMap
        }
        profilesList
    }

    @GET
    @Path("list/{profileId}/metadata/{location: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    def listRepositoryIUs(@PathParam("profileId") long profileId, @PathParam("location") String location) {
        IMetadataRepository metadataRepository =
                metadataRepositoryOperator.refreshRepository(
                        new URI(location),
                        profilesCache.getProfile(profileId).agent)

        def ius = metadataRepositoryOperator.getInstallableUnits(metadataRepository)
        // Using Gson instead of the Jersey parser because
        // Jankson expect all used classes to be serializable.
        // org.eclipse.equinox.internal.p2.metadata.VersionFormat is not
        GsonBuilder gsonBuilder = new GsonBuilder()
        gsonBuilder.registerTypeAdapter(OSGiVersion.class, new BasicVersionSerializer())
        gsonBuilder.create().toJson(ius)
    }

    @GET
    @Path("list/{profileId}/artifacts/{location: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    def listRepositoryArtifacts(@PathParam("profileId") long profileId, @PathParam("location") String location) {
        IArtifactRepository artifactRepository =
                artifactsRepositoryOperator.refreshRepository(
                        new URI(location),
                        profilesCache.getProfile(profileId).agent)

        def artifacts = artifactsRepositoryOperator.getArtifacts(artifactRepository)
        // Using Gson instead of the Jersey parser because
        // Jankson expect all used classes to be serializable.
        // org.eclipse.equinox.internal.p2.metadata.VersionFormat is not
        new Gson().toJson(artifacts)
    }

    @GET
    @Path("graph/{profileId}/metadata/{location: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    def iusGraphData(@PathParam("profileId") long profileId, @PathParam("location") String location) {
        IMetadataRepository metadataRepository =
                metadataRepositoryOperator.refreshRepository(
                        new URI(location),
                        profilesCache.getProfile(profileId).agent)

        def ius = metadataRepositoryOperator.getInstallableUnits(metadataRepository)

        def graphIus = []
        def graphRequirements = []

        // TODO move to constants
        // TODO find scalable solution. Display of large repositories is too slow, e.g. eclipse p2 repo
        // TODO consider extraction of the colors and shapes in the UI bundle
        ius.each { IInstallableUnit iu ->
            def graphIuCapability = iu.providedCapabilities.find {
                it.namespace == 'osgi.bundle' || it.name.endsWith('feature.group')
            }
            if(graphIuCapability == null)
                return

            def iuShape = 'triangle' // Default shape
            def iuColor = '#555555' // Deafult color
            if(graphIuCapability.namespace == 'osgi.bundle') {
                iuShape = 'dot'
                iuColor = '#009688'
            } else if(graphIuCapability.name.endsWith('feature.group')) {
                iuShape = 'square'
                iuColor = '#448aff'
            }
            def iuIdVersion = "$iu.id:$iu.version.original"
            graphIus << new GraphInstallableUnit(id: iuIdVersion, label: iuIdVersion, shape: iuShape, color: iuColor)
            iu.requirements.each { IRequirement requirement ->
                ius.findAll {
                    it.satisfies(requirement)
                }.each { IInstallableUnit foundIu ->
                    graphRequirements << new GraphInstallableUnitRequirement(
                            from: iuIdVersion,
                            to: "$foundIu.id:$foundIu.version.original",
                            arrows: 'to')
                }
            }
        }

        new Gson().toJson(
                new GraphInstallableUnitData(
                        graphInstallableUnits: graphIus,
                        graphInstallableUnitRequirements: graphRequirements))
    }

    @POST
    @Path("load")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    def loadRepositories(RepositoryRegistration repositoryRegistration) {
        if(repositoryRegistration.profileLocation.isEmpty() || repositoryRegistration.profileName.isEmpty()) {
            throw new WebApplicationException("Invalid profile name and/or location")
        }

        if(repositoryRegistration.metadataLocation.isEmpty() || repositoryRegistration.artifactsLocation.isEmpty()) {
            throw new WebApplicationException(
                    "Repository pair must be specified - metadata and artifacts")
        }

        def profile = profilesCache.profiles.find {
            it.name == repositoryRegistration.profileName && it.location == repositoryRegistration.profileLocation
        }

        if(profile == null) {
            throw new WebApplicationException(
                    "Cannot find profile $repositoryRegistration.profileName ($repositoryRegistration.profileLocation)")
        }

        IMetadataRepository metadataRepository = metadataRepositoryOperator.loadRepository(
                new URI(repositoryRegistration.metadataLocation),
                profile.agent)
        artifactsRepositoryOperator.loadRepository(
                new URI(repositoryRegistration.artifactsLocation),
                profile.agent)

        def ius = metadataRepositoryOperator.getInstallableUnits(metadataRepository)

        // Using Gson instead of the Jersey parser because
        // Jankson expect all used classes to be serializable.
        // org.eclipse.equinox.internal.p2.metadata.VersionFormat is not
        new Gson().toJson(ius)
    }

    // Tomcat does NOT accept encoded slash as path parameter (%2F), so
    // the Jersey is enforced to accept all characters in the parameter (regex)
    @DELETE
    @Path("remove/{repoType}/{location: .*}")
    def removeRepository(
            @PathParam("repoType") String repoType,
            @PathParam("location") String location) {
        // FIXME
        /*if(repoType == 'metadata')
            repositoriesCache.removeMetadataRepository(location)
        else if(repoType == 'artifacts')
            repositoriesCache.removeArtifactsRepository(location)*/
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
            unbind = 'unbindArtifactsRepositoryOperator')
    def bindArtifactsRepositoryOperator(
            ArtifactsRepositoryOperator artifactsRepositoryOperator,
            Map<String, ?> properties) {
        this.artifactsRepositoryOperator = artifactsRepositoryOperator
    }

    def unbindArtifactsRepositoryOperator(
            ArtifactsRepositoryOperator artifactsRepositoryOperator,
            Map<String, ?> properties) {
        this.artifactsRepositoryOperator = null
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
