package name.mazgalov.p2.flavours.jersey.app.config

import name.mazgalov.p2.flavours.jersey.app.config.internal.Activator
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import org.osgi.framework.Filter
import org.osgi.framework.ServiceReference
import org.osgi.util.tracker.ServiceTracker
import org.osgi.util.tracker.ServiceTrackerCustomizer

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet

/**
 * Created on 18-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
@WebServlet(urlPatterns = '/*')
class JerseyServlet extends ServletContainer implements ServiceTrackerCustomizer {

    protected static JerseyApplication jerseyApplication = new JerseyApplication()

    private storedJerseyServices = []

    JerseyServlet() {
        super(ResourceConfig.forApplication(jerseyApplication))
    }

    @Override
    void init(ServletConfig config) throws ServletException {
        super.init(config)

        String filterString = "(service.type=jersey)"
        Filter filter = Activator.bundleContext.createFilter(filterString)
        ServiceTracker tracker = new ServiceTracker(Activator.bundleContext, filter, this)
        tracker.open()
    }

    @Override
    Object addingService(ServiceReference reference) {
        def jerseyService = Activator.bundleContext.getService(reference)
        if (jerseyService == null) {
            throw new IllegalStateException("Cannot obtain jersey service for refernce $jerseyService")
        }

        println "Found jersey service $jerseyService"
        storedJerseyServices << jerseyService
        jerseyApplication = new JerseyApplication()
        storedJerseyServices.each {
            jerseyApplication.register(it)
        }
        reload(ResourceConfig.forApplication(jerseyApplication))
    }

    @Override
    void modifiedService(ServiceReference reference, Object service) {
        // !!! Jersey config doesn't have unregister
    }

    @Override
    void removedService(ServiceReference reference, Object service) {
        // !!! Jersey config doesn't have unregister
    }
}
