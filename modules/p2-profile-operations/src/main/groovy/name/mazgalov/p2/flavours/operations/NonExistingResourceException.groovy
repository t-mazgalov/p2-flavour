package name.mazgalov.p2.flavours.operations

/**
 * Created on 19-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class NonExistingResourceException extends Exception{
    NonExistingResourceException(String msg) {
        super(msg)
    }
}
