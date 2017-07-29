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

    protected boolean checkCyclicDependencies(SimplifiedProvisioningList fromList, SimplifiedProvisioningList toList) {
        def foundIds = []
        if(findDependencyIds(foundIds, toList))
            return true
        if(foundIds.contains(fromList.id)) {
            return true
        }
        return false
    }

    protected boolean findDependencyIds(List<Long> collector, SimplifiedProvisioningList toList) {
        if(toList.extendedListIds.contains(toList.id))
            return true
        toList.extendedListIds.each {
            if(it == -1)
                return
            if(collector.contains(it))
                return
            collector << it
            findDependencyIds(collector, getSimplifiedProvisioningList(it))
        }
        return false
    }

    protected crateSystemProvisioningList() {
        def newProvisioningList = new SimplifiedProvisioningList(
                id: 0L,
                name: 'system',
                installableUnits: [],
                extendedListIds: [-1L])
        simplifiedProvisioningLists << newProvisioningList
        storeCache()
    }

    @Activate
    def activate(BundleContext bundleContext,
                 Map<String,Object> config) {
        def configurationArea = Paths.get(new URI(System.getProperty('osgi.configuration.area')))
        provisioningListsCachePath = configurationArea.resolve(CACHE_FILE)

        def provisioningListsCacheFile = provisioningListsCachePath.toFile()
        if(provisioningListsCacheFile.createNewFile() || provisioningListsCacheFile.text == '') {
            crateSystemProvisioningList()
            return // Nothing to parse if the file is new (empty)
        }

        JsonSlurper slurper = new JsonSlurper()
        slurper.parse(provisioningListsCacheFile).each {
            simplifiedProvisioningLists << new SimplifiedProvisioningList(
                    id: it.id,
                    name: it.name,
                    installableUnits: it.installableUnits,
                    extendedListIds: it.extendedListIds.collect{new Long(it)})
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
        createSimplifiedProvisioningList(name, provisioningList, 0L)
    }

    @Override
    SimplifiedProvisioningList createSimplifiedProvisioningList(
            String name, List<SimplifiedInstallableUnit> installableUnits, long extendedListId) {
        if(getSimplifiedProvisioningList(name) != null)
            throw new ExistingResourceException(
                    "Provisioning list with name $name already exists")

        def newProvisioningList = new SimplifiedProvisioningList(
                id: System.currentTimeMillis(),
                name: name,
                installableUnits: installableUnits,
                extendedListIds: [extendedListId])
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
    def extendList(long fromListId, long toListId) {
        def fromList = getSimplifiedProvisioningList(fromListId)
        def toList = getSimplifiedProvisioningList(toListId)
        if(fromList == null || toList == null)
            return
        if(checkCyclicDependencies(fromList, toList))
            return
        fromList.extendedListIds << toList.id
        storeCache()
    }

    @Override
    def shrinkList(long fromListId, long toListId) {
        def fromList = getSimplifiedProvisioningList(fromListId)
        def toList = getSimplifiedProvisioningList(toListId)
        if(fromList == null || toList == null)
            return
        fromList.extendedListIds -= toList.id
        storeCache()
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
