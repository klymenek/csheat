/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gandev.csheat.web.entitycontroller;

import de.gandev.csheat.web.entitycontroller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import de.gandev.csheat.entity.Cheat;
import java.util.ArrayList;
import java.util.List;
import de.gandev.csheat.entity.App;
import de.gandev.csheat.entity.OS;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ares
 */
@ApplicationScoped
public class OSJpaController implements Serializable {

    @PersistenceUnit(unitName = "csheatGlassfishPU")
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(OS OS) {
        if (OS.getCheats() == null) {
            OS.setCheats(new ArrayList<Cheat>());
        }
        if (OS.getApps() == null) {
            OS.setApps(new ArrayList<App>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cheat> attachedCheats = new ArrayList<Cheat>();
            for (Cheat cheatsCheatToAttach : OS.getCheats()) {
                cheatsCheatToAttach = em.getReference(cheatsCheatToAttach.getClass(), cheatsCheatToAttach.getId());
                attachedCheats.add(cheatsCheatToAttach);
            }
            OS.setCheats(attachedCheats);
            List<App> attachedApps = new ArrayList<App>();
            for (App appsAppToAttach : OS.getApps()) {
                appsAppToAttach = em.getReference(appsAppToAttach.getClass(), appsAppToAttach.getId());
                attachedApps.add(appsAppToAttach);
            }
            OS.setApps(attachedApps);
            em.persist(OS);
            for (Cheat cheatsCheat : OS.getCheats()) {
                OS oldOsOfCheatsCheat = cheatsCheat.getOs();
                cheatsCheat.setOs(OS);
                cheatsCheat = em.merge(cheatsCheat);
                if (oldOsOfCheatsCheat != null) {
                    oldOsOfCheatsCheat.getCheats().remove(cheatsCheat);
                    oldOsOfCheatsCheat = em.merge(oldOsOfCheatsCheat);
                }
            }
            for (App appsApp : OS.getApps()) {
                appsApp.getOs().add(OS);
                appsApp = em.merge(appsApp);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(OS OS) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OS persistentOS = em.find(OS.class, OS.getId());
            List<Cheat> cheatsOld = persistentOS.getCheats();
            List<Cheat> cheatsNew = OS.getCheats();
            List<App> appsOld = persistentOS.getApps();
            List<App> appsNew = OS.getApps();
            List<Cheat> attachedCheatsNew = new ArrayList<Cheat>();
            for (Cheat cheatsNewCheatToAttach : cheatsNew) {
                cheatsNewCheatToAttach = em.getReference(cheatsNewCheatToAttach.getClass(), cheatsNewCheatToAttach.getId());
                attachedCheatsNew.add(cheatsNewCheatToAttach);
            }
            cheatsNew = attachedCheatsNew;
            OS.setCheats(cheatsNew);
            List<App> attachedAppsNew = new ArrayList<App>();
            for (App appsNewAppToAttach : appsNew) {
                appsNewAppToAttach = em.getReference(appsNewAppToAttach.getClass(), appsNewAppToAttach.getId());
                attachedAppsNew.add(appsNewAppToAttach);
            }
            appsNew = attachedAppsNew;
            OS.setApps(appsNew);
            OS = em.merge(OS);
            for (Cheat cheatsOldCheat : cheatsOld) {
                if (!cheatsNew.contains(cheatsOldCheat)) {
                    cheatsOldCheat.setOs(null);
                    cheatsOldCheat = em.merge(cheatsOldCheat);
                }
            }
            for (Cheat cheatsNewCheat : cheatsNew) {
                if (!cheatsOld.contains(cheatsNewCheat)) {
                    OS oldOsOfCheatsNewCheat = cheatsNewCheat.getOs();
                    cheatsNewCheat.setOs(OS);
                    cheatsNewCheat = em.merge(cheatsNewCheat);
                    if (oldOsOfCheatsNewCheat != null && !oldOsOfCheatsNewCheat.equals(OS)) {
                        oldOsOfCheatsNewCheat.getCheats().remove(cheatsNewCheat);
                        oldOsOfCheatsNewCheat = em.merge(oldOsOfCheatsNewCheat);
                    }
                }
            }
            for (App appsOldApp : appsOld) {
                if (!appsNew.contains(appsOldApp)) {
                    appsOldApp.getOs().remove(OS);
                    appsOldApp = em.merge(appsOldApp);
                }
            }
            for (App appsNewApp : appsNew) {
                if (!appsOld.contains(appsNewApp)) {
                    appsNewApp.getOs().add(OS);
                    appsNewApp = em.merge(appsNewApp);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = OS.getId();
                if (findOS(id) == null) {
                    throw new NonexistentEntityException("The oS with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OS OS;
            try {
                OS = em.getReference(OS.class, id);
                OS.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The OS with id " + id + " no longer exists.", enfe);
            }
            List<Cheat> cheats = OS.getCheats();
            for (Cheat cheatsCheat : cheats) {
                cheatsCheat.setOs(null);
                cheatsCheat = em.merge(cheatsCheat);
            }
            List<App> apps = OS.getApps();
            for (App appsApp : apps) {
                appsApp.getOs().remove(OS);
                appsApp = em.merge(appsApp);
            }
            em.remove(OS);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<OS> findOSEntities() {
        return findOSEntities(true, -1, -1);
    }

    public List<OS> findOSEntities(int maxResults, int firstResult) {
        return findOSEntities(false, maxResults, firstResult);
    }

    private List<OS> findOSEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(OS.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public OS findOS(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(OS.class, id);
        } finally {
            em.close();
        }
    }

    public int getOSCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<OS> rt = cq.from(OS.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
