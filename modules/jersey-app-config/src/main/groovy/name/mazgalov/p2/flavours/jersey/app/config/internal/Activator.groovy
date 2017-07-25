package name.mazgalov.p2.flavours.jersey.app.config.internal


import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

/**
 * Created on 18-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class Activator implements BundleActivator {
    static BundleContext bundleContext

    @Override
    void start(BundleContext context) throws Exception {
        bundleContext = context
    }

    @Override
    void stop(BundleContext context) throws Exception {
        bundleContext = null
    }
}
