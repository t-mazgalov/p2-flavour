package name.mazgalov.p2.flavours.operations.internal

import name.mazgalov.p2.flavours.operations.ArtifactsRepositoryOperator
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.eclipse.equinox.p2.metadata.IArtifactKey
import org.eclipse.equinox.p2.repository.IRepositoryManager
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager
import org.osgi.service.component.annotations.Component

/**
 * Created on 04-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@Component(immediate = true, service = ArtifactsRepositoryOperator.class)
class ArtifactsRepositoryOperatorImpl implements ArtifactsRepositoryOperator {
    @Override
    IArtifactRepository loadRepository(URI repositoryLocation, IProvisioningAgent agent) {
        IArtifactRepositoryManager artifactRepositoryManager = getArtifactsRepositoryManager agent

        artifactRepositoryManager.loadRepository(repositoryLocation, new NullProgressMonitor())
    }

    @Override
    IArtifactRepository refreshRepository(URI repositoryLocation, IProvisioningAgent agent) {
        IArtifactRepositoryManager artifactRepositoryManager = getArtifactsRepositoryManager agent

        artifactRepositoryManager.refreshRepository(repositoryLocation, new NullProgressMonitor())
    }

    @Override
    List<URI> getKnownRepositories(IProvisioningAgent agent) {
        IArtifactRepositoryManager artifactRepositoryManager = getArtifactsRepositoryManager agent

        artifactRepositoryManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL).toList()
    }

    @Override
    boolean removeRepository(URI repositoryLocation, IProvisioningAgent agent) {
        IArtifactRepositoryManager artifactRepositoryManager = getArtifactsRepositoryManager agent

        artifactRepositoryManager.removeRepository(repositoryLocation)
    }

    @Override
    Collection<IArtifactKey> getArtifacts(IArtifactRepository repository) {
        repository.query(ArtifactKeyQuery.ALL_KEYS, new NullProgressMonitor()).toUnmodifiableSet()
    }

    protected static getArtifactsRepositoryManager(IProvisioningAgent agent) {
        IArtifactRepositoryManager artifactRepositoryManager =
                (IArtifactRepositoryManager) agent.getService(IArtifactRepositoryManager.SERVICE_NAME)
        if(artifactRepositoryManager == null) {
            throw new IllegalStateException('Cannot obtain artifacts repository manager from the agent')
        }
        artifactRepositoryManager
    }
}
