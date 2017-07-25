package name.mazgalov.p2.flavours.operations.internal

import name.mazgalov.p2.flavours.operations.MetadataRepositoryOperator
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.eclipse.equinox.p2.metadata.IInstallableUnit
import org.eclipse.equinox.p2.query.QueryUtil
import org.eclipse.equinox.p2.repository.IRepositoryManager
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager
import org.osgi.service.component.annotations.Component

/**
 * Created on 04-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Component(immediate = true, service = MetadataRepositoryOperator.class)
class MetadataRepositoryOperatorImpl implements MetadataRepositoryOperator {
    @Override
    IMetadataRepository loadRepository(URI repositoryLocation, IProvisioningAgent agent) {
        IMetadataRepositoryManager metadataRepositoryManager = getMetadataRepositoryManager agent

        metadataRepositoryManager.loadRepository(repositoryLocation, new NullProgressMonitor())
    }

    @Override
    IMetadataRepository refreshRepository(URI repositoryLocation, IProvisioningAgent agent) {
        IMetadataRepositoryManager metadataRepositoryManager = getMetadataRepositoryManager agent

        metadataRepositoryManager.refreshRepository(repositoryLocation, new NullProgressMonitor())
    }

    @Override
    List<URI> getKnownRepositories(IProvisioningAgent agent) {
        IMetadataRepositoryManager metadataRepositoryManager = getMetadataRepositoryManager agent

        metadataRepositoryManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL).toList()
    }

    @Override
    boolean removeRepository(URI repositoryLocation, IProvisioningAgent agent) {
        IMetadataRepositoryManager metadataRepositoryManager = getMetadataRepositoryManager agent

        metadataRepositoryManager.removeRepository(repositoryLocation)
    }

    @Override
    Collection<IInstallableUnit> getInstallableUnits(IMetadataRepository repository) {
        repository.query(QueryUtil.createIUAnyQuery(), new NullProgressMonitor()).toUnmodifiableSet()
    }

    @Override
    Collection<IInstallableUnit> getInstallableUnits(IMetadataRepository repository, Collection<String> installableUnitIds) {
        repository.query(
                QueryUtil.createMatchQuery(
                        createSelectIuP2Query(addSystemIus(installableUnitIds))),
                        new NullProgressMonitor())
                .toUnmodifiableSet()
    }

    protected static getMetadataRepositoryManager(IProvisioningAgent agent) {
        IMetadataRepositoryManager metadataRepositoryManager =
                (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME)
        if(metadataRepositoryManager == null) {
            throw new IllegalStateException('Cannot obtain metadata repository manager from the agent')
        }
        metadataRepositoryManager
    }

    protected static createSelectIuP2Query(Collection<String> installableUnitIds) {
        installableUnitIds.collect{
            "this.id ~= '$it'"
        }.join(' || ')
    }

    protected static Collection<String> addSystemIus(Collection<String> ius) {
        ius << 'org.eclipse.equinox.simpleconfigurator'
        ius << 'tooling*'
        ius
    }
}
