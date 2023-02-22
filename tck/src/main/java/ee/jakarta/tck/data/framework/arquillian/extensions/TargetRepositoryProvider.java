package ee.jakarta.tck.data.framework.arquillian.extensions;

import java.util.logging.Logger;

public enum TargetRepositoryProvider {
    PERSISTANCE("ee.jakarta.tck.data.common.entity.persistance"),
    NOSQL("ee.jakarta.tck.data.common.entity.nosql");
    
    public static final String repositoryPlatform = "jakarta.data.repsoitory.platform";
    
    private static final String property = System.getProperty(repositoryPlatform, "NONE");
    private static final Logger log = Logger.getLogger(TargetRepositoryProvider.class.getCanonicalName());
    
    private String repositoryPackage;
    
    private TargetRepositoryProvider(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
    }
    
    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    public static TargetRepositoryProvider get() {
        if(PERSISTANCE.name().equals(property.trim().toUpperCase())) {
            return PERSISTANCE;
        }
        
        if(NOSQL.name().equals(property.trim().toUpperCase())) {
            return NOSQL;
        }
        
        //TODO could throw a runtime exception here instead
        log.warning("No matching repository provider for '" + property + "' found.  Failing back to default");
        return PERSISTANCE;
    }
}
