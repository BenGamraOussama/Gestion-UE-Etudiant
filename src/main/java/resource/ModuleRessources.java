package resource;

import entities.Module;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import metiers.ModuleBusiness;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/modules")
public class ModuleRessources {
    private static final Logger LOGGER = Logger.getLogger(ModuleRessources.class.getName());
    private final ModuleBusiness moduleBusiness = new ModuleBusiness();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllModules() {
        try {
            List<Module> modules = moduleBusiness.getAllModules();
            if (modules == null || modules.isEmpty()) {
                return Response.status(Status.NO_CONTENT).build();
            }
            return Response.ok(modules).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des modules: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{matricule}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModule(@PathParam("matricule") String matricule) {
        try {
            Module module = moduleBusiness.getModuleByMatricule(matricule);
            if (module == null) {
                return Response.status(Status.NOT_FOUND).entity("Module non trouvé").build();
            }
            return Response.ok(module).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du module: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createModule(Module module) {
        try {
            boolean created = moduleBusiness.addModule(module);
            if (!created) {
                return Response.status(Status.BAD_REQUEST).entity("UE associée introuvable ou données invalides").build();
            }
            return Response.status(Status.CREATED).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du module: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/{matricule}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateModule(@PathParam("matricule") String matricule, Module updated) {
        try {
            boolean ok = moduleBusiness.updateModule(matricule, updated);
            if (!ok) {
                return Response.status(Status.NOT_FOUND).entity("Module non trouvé").build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du module: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{matricule}")
    public Response deleteModule(@PathParam("matricule") String matricule) {
        try {
            boolean ok = moduleBusiness.deleteModule(matricule);
            if (!ok) {
                return Response.status(Status.NOT_FOUND).entity("Module non trouvé").build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du module: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
