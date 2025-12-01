package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import metiers.UniteEnseignementBusiness;
import entities.UniteEnseignement;

@XmlRootElement // Nécessaire pour la sérialisation XML

@Path("/UE")
public class UERessources {
    private UniteEnseignementBusiness ueBusiness = new UniteEnseignementBusiness();

    private static final Logger LOGGER = Logger.getLogger(UERessources.class.getName());

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUE() {
        try {
            LOGGER.info("Récupération de la liste des unités d'enseignement");
            List<UniteEnseignement> ues = ueBusiness.getListeUE();

            if (ues == null || ues.isEmpty()) {
                LOGGER.info("Aucune unité d'enseignement trouvée");
                return Response.status(Status.NO_CONTENT).build();
            }

            return Response.ok(ues).build();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des unités d'enseignement: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                         .entity("Une erreur est survenue lors de la récupération des unités d'enseignement")
                         .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUE(UniteEnseignement ue) {
        if(ueBusiness.addUniteEnseignement(ue))
        return Response.status(Response.Status.CREATED).build();
        else
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{code}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUE(@PathParam("code") int code, UniteEnseignement updatedUE) {
        try {
            LOGGER.info("Mise à jour de l'UE avec le code: " + code);
            boolean updated = ueBusiness.updateUniteEnseignement(code, updatedUE);
            if (!updated) {
                return Response.status(Status.NOT_FOUND).entity("UE non trouvée").build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'UE: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{code}")
    public Response deleteUE(@PathParam("code") int code) {
        if (ueBusiness.deleteUniteEnseignement(code))
                return Response.ok().build();
        return Response.status(Status.NOT_FOUND).build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUE(
            @QueryParam("semestre") Integer semestre,
            @QueryParam("code") Integer code) {
            if (semestre != null) {
                LOGGER.info("Recherche des UE par semestre: " + semestre);
                List<UniteEnseignement> ues = ueBusiness.getUEBySemestre(semestre);
                if (ues == null || ues.isEmpty()) {
                    return Response.status(Status.NO_CONTENT).build();
                }
                return Response.ok(ues).build();
            }

            if (code != null) {
                LOGGER.info("Recherche de l'UE par code: " + code);
                UniteEnseignement ue = ueBusiness.getUEByCode(code);
                if (ue == null) {
                    return Response.status(Status.NOT_FOUND).entity("UE non trouvée").build();
                }
                return Response.ok(ue).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
