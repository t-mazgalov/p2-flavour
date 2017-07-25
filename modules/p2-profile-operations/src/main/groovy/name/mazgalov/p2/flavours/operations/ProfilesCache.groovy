package name.mazgalov.p2.flavours.operations
/**
 * Created on 19-Jul-17.
 * @author <a href="mailto:todor@mazgalov.name">Todor Mazgalov</a> 
 */
interface ProfilesCache {
    static final String CACHE_FILE = 'profiles.cache'

    List<Profile> getProfiles()
    Profile getProfile(long id)
    Profile getRunningProfile()
    void setRunningProfile(long id)

    def createProfile(String profileName, String profileLocation) throws ExistingResourceException
    def removeProfile(String profileName, String profileLocation) throws NonExistingResourceException
}