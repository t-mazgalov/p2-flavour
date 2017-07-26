package name.mazgalov.p2.flavours.operations

import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.eclipse.equinox.p2.metadata.IInstallableUnit
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository

/**
 * Created on 04-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
interface MetadataRepositoryOperator {
    IMetadataRepository loadRepository(URI repositoryLocation, IProvisioningAgent agent)
    IMetadataRepository refreshRepository(URI repositoryLocation, IProvisioningAgent agent)
    List<URI> getKnownRepositories(IProvisioningAgent agent)
    boolean removeRepository(URI repositoryLocation, IProvisioningAgent agent)
    Collection<IInstallableUnit> getInstallableUnits(IMetadataRepository repository)
//    Collection<IInstallableUnit> getInstallableUnits(
//            IMetadataRepository repository, Collection<String> installableUnitIds)
    Collection<IInstallableUnit> getInstallableUnits(
            IMetadataRepository repository, Collection<SimplifiedInstallableUnit> installableUnitIds)
}
