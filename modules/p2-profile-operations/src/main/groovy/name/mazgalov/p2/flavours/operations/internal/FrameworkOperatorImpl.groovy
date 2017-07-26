package name.mazgalov.p2.flavours.operations.internal

import name.mazgalov.p2.flavours.operations.ProvisioningOptions
import name.mazgalov.p2.flavours.operations.FrameworkOperator
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.jobs.IJobChangeEvent
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.jobs.JobChangeAdapter
import org.eclipse.equinox.p2.core.IProvisioningAgent
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider
import org.eclipse.equinox.p2.core.spi.IAgentServiceFactory
import org.eclipse.equinox.p2.engine.IProfile
import org.eclipse.equinox.p2.engine.IProfileRegistry
import org.eclipse.equinox.p2.operations.InstallOperation
import org.eclipse.equinox.p2.operations.ProvisioningSession
import org.osgi.framework.BundleContext
import org.osgi.service.component.annotations.*

import java.nio.file.Paths

/**
 * Created on 26-Mar-17.
 *
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a>
 */
@Component(immediate = true, service = FrameworkOperator.class)
class FrameworkOperatorImpl implements FrameworkOperator{
    private IProvisioningAgentProvider provisioningAgentProvider

    private BundleContext bundleContext

    @Activate
    def activate(BundleContext bundleContext,
                 Map<String,Object> config) {
        this.bundleContext = bundleContext
    }

    // TODO CHANGE THE DS PLUGIN WHICH SUPPORTS FIELDS
    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            target = "(p2.agent.servicename=org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager)",
            unbind = 'destroyMetadataAgentServiceFactory')
    def createMetadataAgentServiceFactory(IAgentServiceFactory agentServiceFactory, Map<String, ?> properties) {
    }

    def destroyMetadataAgentServiceFactory(IAgentServiceFactory agentServiceFactory, Map<String, ?> properties) {
    }

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            target = "(p2.agent.servicename=org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager)",
            unbind = 'destroyArtifactsAgentServiceFactory')
    def createArtifactsAgentServiceFactory(IAgentServiceFactory agentServiceFactory, Map<String, ?> properties) {
    }

    def destroyArtifactsAgentServiceFactory(IAgentServiceFactory agentServiceFactory, Map<String, ?> properties) {
    }

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC,
            unbind = 'destroyProvisioningAgentProvider')
    def createProvisioningAgentProvider(IProvisioningAgentProvider provisioningAgentProvider, Map<String, ?> properties) {
        this.provisioningAgentProvider = provisioningAgentProvider
    }

    def destroyProvisioningAgentProvider(IProvisioningAgentProvider provisioningAgentProvider, Map<String, ?> properties) {
        this.provisioningAgentProvider = null
    }

    @Override
    IProvisioningAgent createProvisioningAgent(URI profileLocation, String profileId) {
        IProvisioningAgent provisioningAgent = provisioningAgentProvider.createAgent(profileLocation)
        if (provisioningAgent == null) {
            throw new IllegalStateException('No provisioning agent is available')
        }

        def profileRegistry = (IProfileRegistry) provisioningAgent.getService(IProfileRegistry.SERVICE_NAME)
        if (profileRegistry == null) {
            throw new IllegalStateException('Cannot find profile registry service')
        }

        def profileProperties = [:]
        profileProperties[IProfile.PROP_INSTALL_FOLDER] = profileLocation.path
        profileProperties[IProfile.PROP_CONFIGURATION_FOLDER] =
                Paths.get(profileLocation).resolve('configuration').toUri().path
        profileProperties[IProfile.PROP_LAUNCHER_CONFIGURATION] =
                Paths.get(profileLocation).resolve('eclipse.ini.ignored').toUri().path
        profileProperties[IProfile.PROP_INSTALL_FEATURES] = 'true'

        profileRegistry.getProfile(profileId)?:profileRegistry.addProfile(profileId,profileProperties)

        provisioningAgent
    }

    @Override
    IStatus installProfile(ProvisioningOptions provisioningOptions) {
        InstallOperation installOperation = new InstallOperation(
                new ProvisioningSession(provisioningOptions.agent), provisioningOptions.installableUnits)
        installOperation.provisioningContext.metadataRepositories = provisioningOptions.metadataRepositories as URI[]
        installOperation.provisioningContext.artifactRepositories = provisioningOptions.artifactRepositories as URI[]
        installOperation.setProfileId(provisioningOptions.profileId)

        IStatus resolveStatus = installOperation.resolveModal(new NullProgressMonitor())
        if (resolveStatus.isOK()) {
            Job job = installOperation.getProvisioningJob(new NullProgressMonitor())
            job.addJobChangeListener(
                    new JobChangeAdapter() {
                        void done(IJobChangeEvent event) {
                            provisioningOptions.agent.stop()
                        }
                    }
            )
            IStatus jobStatus = job.run(new NullProgressMonitor())
            jobStatus
        } else {
            resolveStatus
        }
    }
}
