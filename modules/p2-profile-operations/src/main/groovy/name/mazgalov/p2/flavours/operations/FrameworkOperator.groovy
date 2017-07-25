package name.mazgalov.p2.flavours.operations

import org.eclipse.core.runtime.IStatus
import org.eclipse.equinox.p2.core.IProvisioningAgent

/**
 * Created on 25-Apr-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
interface FrameworkOperator {
    IProvisioningAgent createProvisioningAgent(URI profileLocation, String profileId) throws IllegalStateException
    IStatus installProfile(ProvisioningOptions provisioningOptions)
}