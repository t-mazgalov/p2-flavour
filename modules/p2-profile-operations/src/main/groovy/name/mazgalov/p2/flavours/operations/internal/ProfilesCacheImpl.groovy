package name.mazgalov.p2.flavours.operations.internal

import com.google.gson.Gson
import groovy.json.JsonSlurper
import name.mazgalov.p2.flavours.operations.ExistingResourceException
import name.mazgalov.p2.flavours.operations.FrameworkOperator
import name.mazgalov.p2.flavours.operations.NonExistingResourceException
import name.mazgalov.p2.flavours.operations.Profile
import name.mazgalov.p2.flavours.operations.ProfilesCache
import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created on 19-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Component(immediate = true, service = ProfilesCache.class)
class ProfilesCacheImpl implements ProfilesCache {
    private List<Profile> profiles = []
    private Profile runningProfile

    private Path profilesCachePath = null
    private FrameworkOperator frameworkOperator = null

    protected storeCache() {
        // Using Gson instead of Groovy's JsonOutput because JsonOutput depends on sun.misc.Unsafe
        profilesCachePath.toFile().text = new Gson().toJson(profiles)
    }

    protected recreateAgents() {
        profiles.each {
            IProvisioningAgent agent = frameworkOperator.createProvisioningAgent(
                    Paths.get(it.location).toUri(),
                    it.name)
            it.agent = agent
        }
    }

    protected invalidateRunningProfile() {
        profiles.each {it.running = false}
    }

    protected restoreRunningProfile() {
        runningProfile = profiles.find { it.running }
    }

    protected createSystemProfile(String name, String location) {
        Profile systemProfile = createProfile(name, location)
        systemProfile.running = true

        runningProfile = systemProfile
    }

    @Activate
    def activate(BundleContext bundleContext,
                 Map<String,Object> config) {
        def configurationArea = Paths.get(new URI(System.getProperty('osgi.configuration.area')))
        profilesCachePath = configurationArea.resolve(CACHE_FILE)

        def profilesCacheFile = profilesCachePath.toFile()
        if(profilesCacheFile.createNewFile() || profilesCacheFile.text == '') {
            createSystemProfile('system', configurationArea.resolve('systemProfile').toString())
            return
        }

        def slurper = new JsonSlurper()
        slurper.parse(profilesCacheFile).each {
            profiles << new Profile(id: it.id, name: it.name, location: it.location, running: it.running)
        }

        // The ProvisioningAgent is not serializable, all agents must be recreated on activation
        recreateAgents()
        // The running profile is stored in the cache file, it must be restored on activation
        restoreRunningProfile()
    }

    @Override
    List<Profile> getProfiles() {
        profiles
    }

    @Override
    Profile getProfile(long id) {
        profiles.find {
            it.id == id
        }
    }

    @Override
    Profile getRunningProfile() {
        runningProfile
    }

    @Override
    void setRunningProfile(long id) {
        invalidateRunningProfile()
        runningProfile = getProfile(id)
        runningProfile.running = true
        storeCache()
    }

    @Override
    Profile createProfile(String profileName, String profileLocation) throws ExistingResourceException{
        def profileId = System.currentTimeMillis()
        Profile profile = new Profile(id: profileId, name: profileName, location: profileLocation)

        if(profiles.find {it == profile} != null)
            throw new ExistingResourceException(
                    "Profile with name $profileName and location $profileLocation already exists")

        IProvisioningAgent agent = frameworkOperator.createProvisioningAgent(
                Paths.get(profileLocation).toUri(),
                profileName)
        profile.agent = agent
        profiles << profile

        storeCache()
        profile
    }

    @Override
    def removeProfile(String profileName, String profileLocation)throws NonExistingResourceException {
        // FIXME not working. Must be removed from the P2 registry
        Profile profile = new Profile(name: profileName, location: profileLocation)
        def foundProfile = profiles.find {it == profile}
        if(foundProfile == null)
            throw new NonExistingResourceException(
                    "Profile with name $profileName and location $profileLocation cannot be found")

        profiles -= foundProfile
        storeCache()
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
