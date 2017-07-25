package name.mazgalov.p2.flavours.operations

import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.eclipse.equinox.p2.metadata.IArtifactKey
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository

/**
 * Created on 04-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
interface ArtifactsRepositoryOperator {
    IArtifactRepository loadRepository(URI repositoryLocation, IProvisioningAgent agent)
    IArtifactRepository refreshRepository(URI repositoryLocation, IProvisioningAgent agent)
    List<URI> getKnownRepositories(IProvisioningAgent agent)
    boolean removeRepository(URI repositoryLocation, IProvisioningAgent agent)
    Collection<IArtifactKey> getArtifacts(IArtifactRepository repository)
}
