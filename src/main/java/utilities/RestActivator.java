package utilities;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;
import resource.AuthenticationEndPoint;
import resource.UERessources;
import resource.ModuleRessources;


import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestActivator extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Enregistrement des classes de ressources
        classes.add(UERessources.class);
        classes.add(ModuleRessources.class);
        classes.add(AuthenticationEndPoint.class);
        return classes;
    }
}