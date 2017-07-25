package name.mazgalov.p2.flavours.operations

import com.fasterxml.jackson.annotation.JsonIgnore
import org.eclipse.equinox.p2.core.IProvisioningAgent

/**
 * Created on 19-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class Profile {
    long id
    boolean running = false // Needed to store the state with the current running profile
    String name
    String location
    @JsonIgnore
    transient IProvisioningAgent agent

    @Override
    boolean equals(Object obj) {
        def profile = obj as Profile
        id == profile.id
    }
}
