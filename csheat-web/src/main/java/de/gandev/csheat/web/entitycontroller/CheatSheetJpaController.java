/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gandev.csheat.web.entitycontroller;

import de.gandev.csheat.web.entitycontroller.exceptions.NonexistentEntityException;
import de.gandev.csheat.entity.CheatSheet;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import de.gandev.csheat.entity.CheatSheetFormat;
import de.gandev.csheat.entity.CheatVersion;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ares
 */
@ApplicationScoped
public class CheatSheetJpaController implements Serializable {

    @PersistenceUnit(unitName = "csheatGlassfishPU")
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CheatSheet cheatSheet) {
        if (cheatSheet.getCheats() == null) {
            cheatSheet.setCheats(new ArrayList<CheatVersion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CheatSheetFormat format = cheatSheet.getFormat();
            if (format != null) {
                format = em.getReference(format.getClass(), format.getId());
                cheatSheet.setFormat(format);
            }
            List<CheatVersion> attachedCheats = new ArrayList<CheatVersion>();
            for (CheatVersion cheatsCheatVersionToAttach : cheatSheet.getCheats()) {
                cheatsCheatVersionToAttach = em.getReference(cheatsCheatVersionToAttach.getClass(), cheatsCheatVersionToAttach.getId());
                attachedCheats.add(cheatsCheatVersionToAttach);
            }
            cheatSheet.setCheats(attachedCheats);
            em.persist(cheatSheet);
            if (format != null) {
                format.getCheatSheets().add(cheatSheet);
                format = em.merge(format);
            }
            for (CheatVersion cheatsCheatVersion : cheatSheet.getCheats()) {
                cheatsCheatVersion.getCheatSheets().add(cheatSheet);
                cheatsCheatVersion = em.merge(cheatsCheatVersion);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CheatSheet cheatSheet) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CheatSheet persistentCheatSheet = em.find(CheatSheet.class, cheatSheet.getId());
            CheatSheetFormat formatOld = persistentCheatSheet.getFormat();
            CheatSheetFormat formatNew = cheatSheet.getFormat();
            List<CheatVersion> cheatsOld = persistentCheatSheet.getCheats();
            List<CheatVersion> cheatsNew = cheatSheet.getCheats();
            if (formatNew != null) {
                formatNew = em.getReference(formatNew.getClass(), formatNew.getId());
                cheatSheet.setFormat(formatNew);
            }
            List<CheatVersion> attachedCheatsNew = new ArrayList<CheatVersion>();
            for (CheatVersion cheatsNewCheatVersionToAttach : cheatsNew) {
                cheatsNewCheatVersionToAttach = em.getReference(cheatsNewCheatVersionToAttach.getClass(), cheatsNewCheatVersionToAttach.getId());
                attachedCheatsNew.add(cheatsNewCheatVersionToAttach);
            }
            cheatsNew = attachedCheatsNew;
            cheatSheet.setCheats(cheatsNew);
            cheatSheet = em.merge(cheatSheet);
            if (formatOld != null && !formatOld.equals(formatNew)) {
                formatOld.getCheatSheets().remove(cheatSheet);
                formatOld = em.merge(formatOld);
            }
            if (formatNew != null && !formatNew.equals(formatOld)) {
                formatNew.getCheatSheets().add(cheatSheet);
                formatNew = em.merge(formatNew);
            }
            for (CheatVersion cheatsOldCheatVersion : cheatsOld) {
                if (!cheatsNew.contains(cheatsOldCheatVersion)) {
                    cheatsOldCheatVersion.getCheatSheets().remove(cheatSheet);
                    cheatsOldCheatVersion = em.merge(cheatsOldCheatVersion);
                }
            }
            for (CheatVersion cheatsNewCheatVersion : cheatsNew) {
                if (!cheatsOld.contains(cheatsNewCheatVersion)) {
                    cheatsNewCheatVersion.getCheatSheets().add(cheatSheet);
                    cheatsNewCheatVersion = em.merge(cheatsNewCheatVersion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = cheatSheet.getId();
                if (findCheatSheet(id) == null) {
                    throw new NonexistentEntityException("The cheatSheet with id " + id + " no longer exists.");
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
            CheatSheet cheatSheet;
            try {
                cheatSheet = em.getReference(CheatSheet.class, id);
                cheatSheet.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cheatSheet with id " + id + " no longer exists.", enfe);
            }
            CheatSheetFormat format = cheatSheet.getFormat();
            if (format != null) {
                format.getCheatSheets().remove(cheatSheet);
                format = em.merge(format);
            }
            List<CheatVersion> cheats = cheatSheet.getCheats();
            for (CheatVersion cheatsCheatVersion : cheats) {
                cheatsCheatVersion.getCheatSheets().remove(cheatSheet);
                cheatsCheatVersion = em.merge(cheatsCheatVersion);
            }
            em.remove(cheatSheet);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CheatSheet> findCheatSheetEntities() {
        return findCheatSheetEntities(true, -1, -1);
    }

    public List<CheatSheet> findCheatSheetEntities(int maxResults, int firstResult) {
        return findCheatSheetEntities(false, maxResults, firstResult);
    }

    private List<CheatSheet> findCheatSheetEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CheatSheet.class));
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

    public CheatSheet findCheatSheet(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CheatSheet.class, id);
        } finally {
            em.close();
        }
    }

    public int getCheatSheetCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CheatSheet> rt = cq.from(CheatSheet.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
