package name.mazgalov.p2.flavours.operations

import org.eclipse.equinox.internal.p2.metadata.InstallableUnit

/**
 * Created on 24-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
interface ProvisioiningListsCache {
    static final String CACHE_FILE = 'provisioning.lists.cache'

    List<SimplifiedProvisioningList> getSimplifiedProvisioningLists()
    SimplifiedProvisioningList getSimplifiedProvisioningList(provisioningListId)
    SimplifiedProvisioningList getSimplifiedProvisioningList(name, profileId)

    SimplifiedProvisioningList createSimplifiedProvisioningList(
            String name, long profileId, List<SimplifiedInstallableUnit> provisioningList)

    SimplifiedProvisioningList addSimplifiedInstallableUnits(
            long provisioningListId, List<SimplifiedInstallableUnit> installableUnits)
    SimplifiedProvisioningList removeSimplifiedInstallableUnits(
            long provisioningListId, List<SimplifiedInstallableUnit> installableUnits)

    def removeSimplifiedProvisioningList(provisioningListId)

    List<InstallableUnit> resolveProvisioningListInstallableUnits(
            List<SimplifiedInstallableUnit> simplifiedInstallableUnitList)
}
