package name.mazgalov.p2.flavours.operations.internal

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import groovy.json.JsonSlurper
import name.mazgalov.p2.flavours.operations.ExistingResourceException
import name.mazgalov.p2.flavours.operations.ProvisioiningListsCache
import name.mazgalov.p2.flavours.operations.SimplifiedInstallableUnit
import name.mazgalov.p2.flavours.operations.SimplifiedProvisioningList
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created on 24-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Component(immediate = true, service = ProvisioiningListsCache.class)
class ProvisioningListCacheImpl implements ProvisioiningListsCache{
    private List<SimplifiedProvisioningList> simplifiedProvisioningLists = []

    private Path provisioningListsCachePath = null

    protected storeCache() {
        // Using Gson instead of Groovy's JsonOutput because JsonOutput depends on sun.misc.Unsafe
        provisioningListsCachePath.toFile().text = new Gson().toJson(simplifiedProvisioningLists)
    }

    @Activate
    def activate(BundleContext bundleContext,
                 Map<String,Object> config) {
        def configurationArea = Paths.get(new URI(System.getProperty('osgi.configuration.area')))
        provisioningListsCachePath = configurationArea.resolve(CACHE_FILE)

        def provisioningListsCacheFile = provisioningListsCachePath.toFile()
        if(provisioningListsCacheFile.createNewFile() || provisioningListsCacheFile.text == '')
            return // Nothing to parse if the file is new (empty)

        JsonSlurper slurper = new JsonSlurper()
        slurper.parse(provisioningListsCacheFile).each {
            simplifiedProvisioningLists << new SimplifiedProvisioningList(
                    id: it.id,
                    name: it.name,
                    installableUnits: it.installableUnits)
        }
    }

    @Override
    List<SimplifiedProvisioningList> getSimplifiedProvisioningLists() {
        simplifiedProvisioningLists
    }

    @Override
    SimplifiedProvisioningList getSimplifiedProvisioningList(long provisioningListId) {
        simplifiedProvisioningLists.find { it.id == provisioningListId }
    }

    @Override
    SimplifiedProvisioningList getSimplifiedProvisioningList(String name) {
        simplifiedProvisioningLists.find { it.name == name }
    }

    @Override
    SimplifiedProvisioningList createSimplifiedProvisioningList(
            String name, List<SimplifiedInstallableUnit> provisioningList) {
        if(getSimplifiedProvisioningList(name) != null)
            throw new ExistingResourceException(
                    "Provisioning list with name $name for profile $profileId already exists")

        def newProvisioningList = new SimplifiedProvisioningList(
                id: System.currentTimeMillis(), name: name, installableUnits: provisioningList)
        simplifiedProvisioningLists << newProvisioningList
        storeCache()
        newProvisioningList
    }

    @Override
    SimplifiedProvisioningList addSimplifiedInstallableUnits(
            long provisioningListId, List<SimplifiedInstallableUnit> installableUnits) {
        def simplifiedProvisioningList = getSimplifiedProvisioningList(provisioningListId)
        simplifiedProvisioningList.installableUnits += installableUnits
        storeCache()
        simplifiedProvisioningList
    }

    @Override
    SimplifiedProvisioningList removeSimplifiedInstallableUnits(
            long provisioningListId, List<SimplifiedInstallableUnit> installableUnits) {
        def simplifiedProvisioningList = getSimplifiedProvisioningList(provisioningListId)
        simplifiedProvisioningList.installableUnits -= installableUnits
        storeCache()
        simplifiedProvisioningList
    }

    @Override
    def removeSimplifiedProvisioningList(long provisioningListId) {
        simplifiedProvisioningLists -= getSimplifiedProvisioningList(provisioningListId)
        storeCache()
    }

    @Override
    List<InstallableUnit> resolveProvisioningListInstallableUnits(
            List<SimplifiedInstallableUnit> simplifiedInstallableUnitList) {
        return null
    }
}
