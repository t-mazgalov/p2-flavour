package name.mazgalov.p2.flavours.operations

import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.eclipse.equinox.p2.metadata.IInstallableUnit

/**
 * Created on 04-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class ProvisioningOptions {
    List<URI> metadataRepositories = []
    List<URI> artifactRepositories = []
    String profileId
    IProvisioningAgent agent
    Collection<IInstallableUnit> installableUnits = []
}
