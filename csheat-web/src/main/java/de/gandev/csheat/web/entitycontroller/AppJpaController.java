/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gandev.csheat.web.entitycontroller;

import de.gandev.csheat.web.entitycontroller.exceptions.NonexistentEntityException;
import de.gandev.csheat.entity.App;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import de.gandev.csheat.entity.OS;
import java.util.ArrayList;
import java.util.List;
import de.gandev.csheat.entity.Cheat;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ares
 */
@ApplicationScoped
public class AppJpaController implements Serializable {

    @PersistenceUnit(unitName = "csheatGlassfishPU")
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(App app) {
        if (app.getOs() == null) {
            app.setOs(new ArrayList<OS>());
        }
        if (app.getCheats() == null) {
            app.setCheats(new ArrayList<Cheat>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<OS> attachedOs = new ArrayList<OS>();
            for (OS osOSToAttach : app.getOs()) {
                osOSToAttach = em.getReference(osOSToAttach.getClass(), osOSToAttach.getId());
                attachedOs.add(osOSToAttach);
            }
            app.setOs(attachedOs);
            List<Cheat> attachedCheats = new ArrayList<Cheat>();
            for (Cheat cheatsCheatToAttach : app.getCheats()) {
                cheatsCheatToAttach = em.getReference(cheatsCheatToAttach.getClass(), cheatsCheatToAttach.getId());
                attachedCheats.add(cheatsCheatToAttach);
            }
            app.setCheats(attachedCheats);
            em.persist(app);
            for (OS osOS : app.getOs()) {
                osOS.getApps().add(app);
                osOS = em.merge(osOS);
            }
            for (Cheat cheatsCheat : app.getCheats()) {
                App oldAppOfCheatsCheat = cheatsCheat.getApp();
                cheatsCheat.setApp(app);
                cheatsCheat = em.merge(cheatsCheat);
                if (oldAppOfCheatsCheat != null) {
                    oldAppOfCheatsCheat.getCheats().remove(cheatsCheat);
                    oldAppOfCheatsCheat = em.merge(oldAppOfCheatsCheat);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(App app) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            App persistentApp = em.find(App.class, app.getId());
            List<OS> osOld = persistentApp.getOs();
            List<OS> osNew = app.getOs();
            List<Cheat> cheatsOld = persistentApp.getCheats();
            List<Cheat> cheatsNew = app.getCheats();
            List<OS> attachedOsNew = new ArrayList<OS>();
            for (OS osNewOSToAttach : osNew) {
                osNewOSToAttach = em.getReference(osNewOSToAttach.getClass(), osNewOSToAttach.getId());
                attachedOsNew.add(osNewOSToAttach);
            }
            osNew = attachedOsNew;
            app.setOs(osNew);
            List<Cheat> attachedCheatsNew = new ArrayList<Cheat>();
            for (Cheat cheatsNewCheatToAttach : cheatsNew) {
                cheatsNewCheatToAttach = em.getReference(cheatsNewCheatToAttach.getClass(), cheatsNewCheatToAttach.getId());
                attachedCheatsNew.add(cheatsNewCheatToAttach);
            }
            cheatsNew = attachedCheatsNew;
            app.setCheats(cheatsNew);
            app = em.merge(app);
            for (OS osOldOS : osOld) {
                if (!osNew.contains(osOldOS)) {
                    osOldOS.getApps().remove(app);
                    osOldOS = em.merge(osOldOS);
                }
            }
            for (OS osNewOS : osNew) {
                if (!osOld.contains(osNewOS)) {
                    osNewOS.getApps().add(app);
                    osNewOS = em.merge(osNewOS);
                }
            }
            for (Cheat cheatsOldCheat : cheatsOld) {
                if (!cheatsNew.contains(cheatsOldCheat)) {
                    cheatsOldCheat.setApp(null);
                    cheatsOldCheat = em.merge(cheatsOldCheat);
                }
            }
            for (Cheat cheatsNewCheat : cheatsNew) {
                if (!cheatsOld.contains(cheatsNewCheat)) {
                    App oldAppOfCheatsNewCheat = cheatsNewCheat.getApp();
                    cheatsNewCheat.setApp(app);
                    cheatsNewCheat = em.merge(cheatsNewCheat);
                    if (oldAppOfCheatsNewCheat != null && !oldAppOfCheatsNewCheat.equals(app)) {
                        oldAppOfCheatsNewCheat.getCheats().remove(cheatsNewCheat);
                        oldAppOfCheatsNewCheat = em.merge(oldAppOfCheatsNewCheat);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = app.getId();
                if (findApp(id) == null) {
                    throw new NonexistentEntityException("The app with id " + id + " no longer exists.");
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
            App app;
            try {
                app = em.getReference(App.class, id);
                app.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The app with id " + id + " no longer exists.", enfe);
            }
            List<OS> os = app.getOs();
            for (OS osOS : os) {
                osOS.getApps().remove(app);
                osOS = em.merge(osOS);
            }
            List<Cheat> cheats = app.getCheats();
            for (Cheat cheatsCheat : cheats) {
                cheatsCheat.setApp(null);
                cheatsCheat = em.merge(cheatsCheat);
            }
            em.remove(app);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<App> findAppEntities() {
        return findAppEntities(true, -1, -1);
    }

    public List<App> findAppEntities(int maxResults, int firstResult) {
        return findAppEntities(false, maxResults, firstResult);
    }

    private List<App> findAppEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(App.class));
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

    public App findApp(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(App.class, id);
        } finally {
            em.close();
        }
    }

    public int getAppCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<App> rt = cq.from(App.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
