package name.mazgalov.p2.flavours.operations

/**
 * Created on 24-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
class SimplifiedInstallableUnit {
    String id
    String type
    String repository
    int major
    int minor
    int micro
    def qualifier

    @Override
    boolean equals(Object obj) {
        def simplifiedInstallableUnit = obj as SimplifiedInstallableUnit
        id == simplifiedInstallableUnit.id &&
                type == simplifiedInstallableUnit.type &&
                major == simplifiedInstallableUnit.major &&
                minor == simplifiedInstallableUnit.minor &&
                micro == simplifiedInstallableUnit.micro &&
                qualifier == simplifiedInstallableUnit.qualifier
    }
}
