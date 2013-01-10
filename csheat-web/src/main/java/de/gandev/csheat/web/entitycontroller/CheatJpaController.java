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
import de.gandev.csheat.entity.App;
import de.gandev.csheat.entity.OS;
import de.gandev.csheat.entity.Cheat;
import de.gandev.csheat.entity.CheatVersion;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ares
 */
@ApplicationScoped
public class CheatJpaController implements Serializable {

    @PersistenceUnit(unitName = "csheatGlassfishPU")
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cheat cheat) {
        if (cheat.getVersions() == null) {
            cheat.setVersions(new ArrayList<CheatVersion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            App app = cheat.getApp();
            if (app != null) {
                app = em.getReference(app.getClass(), app.getId());
                cheat.setApp(app);
            }
            OS os = cheat.getOs();
            if (os != null) {
                os = em.getReference(os.getClass(), os.getId());
                cheat.setOs(os);
            }
            Cheat duplicateOf = cheat.getDuplicateOf();
            if (duplicateOf != null) {
                duplicateOf = em.getReference(duplicateOf.getClass(), duplicateOf.getId());
                cheat.setDuplicateOf(duplicateOf);
            }
            List<CheatVersion> attachedVersions = new ArrayList<CheatVersion>();
            for (CheatVersion versionsCheatVersionToAttach : cheat.getVersions()) {
                versionsCheatVersionToAttach = em.getReference(versionsCheatVersionToAttach.getClass(), versionsCheatVersionToAttach.getId());
                attachedVersions.add(versionsCheatVersionToAttach);
            }
            cheat.setVersions(attachedVersions);
            em.persist(cheat);
            if (app != null) {
                app.getCheats().add(cheat);
                app = em.merge(app);
            }
            if (os != null) {
                os.getCheats().add(cheat);
                os = em.merge(os);
            }
            if (duplicateOf != null) {
                Cheat oldDuplicateOfOfDuplicateOf = duplicateOf.getDuplicateOf();
                if (oldDuplicateOfOfDuplicateOf != null) {
                    oldDuplicateOfOfDuplicateOf.setDuplicateOf(null);
                    oldDuplicateOfOfDuplicateOf = em.merge(oldDuplicateOfOfDuplicateOf);
                }
                duplicateOf.setDuplicateOf(cheat);
                duplicateOf = em.merge(duplicateOf);
            }
            for (CheatVersion versionsCheatVersion : cheat.getVersions()) {
                Cheat oldCheatOfVersionsCheatVersion = versionsCheatVersion.getCheat();
                versionsCheatVersion.setCheat(cheat);
                versionsCheatVersion = em.merge(versionsCheatVersion);
                if (oldCheatOfVersionsCheatVersion != null) {
                    oldCheatOfVersionsCheatVersion.getVersions().remove(versionsCheatVersion);
                    oldCheatOfVersionsCheatVersion = em.merge(oldCheatOfVersionsCheatVersion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cheat cheat) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cheat persistentCheat = em.find(Cheat.class, cheat.getId());
            App appOld = persistentCheat.getApp();
            App appNew = cheat.getApp();
            OS osOld = persistentCheat.getOs();
            OS osNew = cheat.getOs();
            Cheat duplicateOfOld = persistentCheat.getDuplicateOf();
            Cheat duplicateOfNew = cheat.getDuplicateOf();
            List<CheatVersion> versionsOld = persistentCheat.getVersions();
            List<CheatVersion> versionsNew = cheat.getVersions();
            if (appNew != null) {
                appNew = em.getReference(appNew.getClass(), appNew.getId());
                cheat.setApp(appNew);
            }
            if (osNew != null) {
                osNew = em.getReference(osNew.getClass(), osNew.getId());
                cheat.setOs(osNew);
            }
            if (duplicateOfNew != null) {
                duplicateOfNew = em.getReference(duplicateOfNew.getClass(), duplicateOfNew.getId());
                cheat.setDuplicateOf(duplicateOfNew);
            }
            List<CheatVersion> attachedVersionsNew = new ArrayList<CheatVersion>();
            for (CheatVersion versionsNewCheatVersionToAttach : versionsNew) {
                versionsNewCheatVersionToAttach = em.getReference(versionsNewCheatVersionToAttach.getClass(), versionsNewCheatVersionToAttach.getId());
                attachedVersionsNew.add(versionsNewCheatVersionToAttach);
            }
            versionsNew = attachedVersionsNew;
            cheat.setVersions(versionsNew);
            cheat = em.merge(cheat);
            if (appOld != null && !appOld.equals(appNew)) {
                appOld.getCheats().remove(cheat);
                appOld = em.merge(appOld);
            }
            if (appNew != null && !appNew.equals(appOld)) {
                appNew.getCheats().add(cheat);
                appNew = em.merge(appNew);
            }
            if (osOld != null && !osOld.equals(osNew)) {
                osOld.getCheats().remove(cheat);
                osOld = em.merge(osOld);
            }
            if (osNew != null && !osNew.equals(osOld)) {
                osNew.getCheats().add(cheat);
                osNew = em.merge(osNew);
            }
            if (duplicateOfOld != null && !duplicateOfOld.equals(duplicateOfNew)) {
                duplicateOfOld.setDuplicateOf(null);
                duplicateOfOld = em.merge(duplicateOfOld);
            }
            if (duplicateOfNew != null && !duplicateOfNew.equals(duplicateOfOld)) {
                Cheat oldDuplicateOfOfDuplicateOf = duplicateOfNew.getDuplicateOf();
                if (oldDuplicateOfOfDuplicateOf != null) {
                    oldDuplicateOfOfDuplicateOf.setDuplicateOf(null);
                    oldDuplicateOfOfDuplicateOf = em.merge(oldDuplicateOfOfDuplicateOf);
                }
                duplicateOfNew.setDuplicateOf(cheat);
                duplicateOfNew = em.merge(duplicateOfNew);
            }
            for (CheatVersion versionsOldCheatVersion : versionsOld) {
                if (!versionsNew.contains(versionsOldCheatVersion)) {
                    versionsOldCheatVersion.setCheat(null);
                    versionsOldCheatVersion = em.merge(versionsOldCheatVersion);
                }
            }
            for (CheatVersion versionsNewCheatVersion : versionsNew) {
                if (!versionsOld.contains(versionsNewCheatVersion)) {
                    Cheat oldCheatOfVersionsNewCheatVersion = versionsNewCheatVersion.getCheat();
                    versionsNewCheatVersion.setCheat(cheat);
                    versionsNewCheatVersion = em.merge(versionsNewCheatVersion);
                    if (oldCheatOfVersionsNewCheatVersion != null && !oldCheatOfVersionsNewCheatVersion.equals(cheat)) {
                        oldCheatOfVersionsNewCheatVersion.getVersions().remove(versionsNewCheatVersion);
                        oldCheatOfVersionsNewCheatVersion = em.merge(oldCheatOfVersionsNewCheatVersion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = cheat.getId();
                if (findCheat(id) == null) {
                    throw new NonexistentEntityException("The cheat with id " + id + " no longer exists.");
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
            Cheat cheat;
            try {
                cheat = em.getReference(Cheat.class, id);
                cheat.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cheat with id " + id + " no longer exists.", enfe);
            }
            App app = cheat.getApp();
            if (app != null) {
                app.getCheats().remove(cheat);
                app = em.merge(app);
            }
            OS os = cheat.getOs();
            if (os != null) {
                os.getCheats().remove(cheat);
                os = em.merge(os);
            }
            Cheat duplicateOf = cheat.getDuplicateOf();
            if (duplicateOf != null) {
                duplicateOf.setDuplicateOf(null);
                duplicateOf = em.merge(duplicateOf);
            }
            List<CheatVersion> versions = cheat.getVersions();
            for (CheatVersion versionsCheatVersion : versions) {
                versionsCheatVersion.setCheat(null);
                versionsCheatVersion = em.merge(versionsCheatVersion);
            }
            em.remove(cheat);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cheat> findCheatEntities() {
        return findCheatEntities(true, -1, -1);
    }

    public List<Cheat> findCheatEntities(int maxResults, int firstResult) {
        return findCheatEntities(false, maxResults, firstResult);
    }

    private List<Cheat> findCheatEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cheat.class));
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

    public Cheat findCheat(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cheat.class, id);
        } finally {
            em.close();
        }
    }

    public int getCheatCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cheat> rt = cq.from(Cheat.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
