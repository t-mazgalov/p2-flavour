package name.mazgalov.p2.flavours.ui

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

/**
 * Created on 25-Apr-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class Activator implements BundleActivator{
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
