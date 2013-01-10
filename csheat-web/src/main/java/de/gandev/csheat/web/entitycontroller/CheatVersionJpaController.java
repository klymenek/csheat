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
import de.gandev.csheat.entity.CheatSheet;
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
public class CheatVersionJpaController implements Serializable {

    @PersistenceUnit(unitName = "csheatGlassfishPU")
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CheatVersion cheatVersion) {
        if (cheatVersion.getCheatSheets() == null) {
            cheatVersion.setCheatSheets(new ArrayList<CheatSheet>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cheat cheat = cheatVersion.getCheat();
            if (cheat != null) {
                cheat = em.getReference(cheat.getClass(), cheat.getId());
                cheatVersion.setCheat(cheat);
            }
            List<CheatSheet> attachedCheatSheets = new ArrayList<CheatSheet>();
            for (CheatSheet cheatSheetsCheatSheetToAttach : cheatVersion.getCheatSheets()) {
                cheatSheetsCheatSheetToAttach = em.getReference(cheatSheetsCheatSheetToAttach.getClass(), cheatSheetsCheatSheetToAttach.getId());
                attachedCheatSheets.add(cheatSheetsCheatSheetToAttach);
            }
            cheatVersion.setCheatSheets(attachedCheatSheets);
            em.persist(cheatVersion);
            if (cheat != null) {
                cheat.getVersions().add(cheatVersion);
                cheat = em.merge(cheat);
            }
            for (CheatSheet cheatSheetsCheatSheet : cheatVersion.getCheatSheets()) {
                cheatSheetsCheatSheet.getCheats().add(cheatVersion);
                cheatSheetsCheatSheet = em.merge(cheatSheetsCheatSheet);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CheatVersion cheatVersion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CheatVersion persistentCheatVersion = em.find(CheatVersion.class, cheatVersion.getId());
            Cheat cheatOld = persistentCheatVersion.getCheat();
            Cheat cheatNew = cheatVersion.getCheat();
            List<CheatSheet> cheatSheetsOld = persistentCheatVersion.getCheatSheets();
            List<CheatSheet> cheatSheetsNew = cheatVersion.getCheatSheets();
            if (cheatNew != null) {
                cheatNew = em.getReference(cheatNew.getClass(), cheatNew.getId());
                cheatVersion.setCheat(cheatNew);
            }
            List<CheatSheet> attachedCheatSheetsNew = new ArrayList<CheatSheet>();
            for (CheatSheet cheatSheetsNewCheatSheetToAttach : cheatSheetsNew) {
                cheatSheetsNewCheatSheetToAttach = em.getReference(cheatSheetsNewCheatSheetToAttach.getClass(), cheatSheetsNewCheatSheetToAttach.getId());
                attachedCheatSheetsNew.add(cheatSheetsNewCheatSheetToAttach);
            }
            cheatSheetsNew = attachedCheatSheetsNew;
            cheatVersion.setCheatSheets(cheatSheetsNew);
            cheatVersion = em.merge(cheatVersion);
            if (cheatOld != null && !cheatOld.equals(cheatNew)) {
                cheatOld.getVersions().remove(cheatVersion);
                cheatOld = em.merge(cheatOld);
            }
            if (cheatNew != null && !cheatNew.equals(cheatOld)) {
                cheatNew.getVersions().add(cheatVersion);
                cheatNew = em.merge(cheatNew);
            }
            for (CheatSheet cheatSheetsOldCheatSheet : cheatSheetsOld) {
                if (!cheatSheetsNew.contains(cheatSheetsOldCheatSheet)) {
                    cheatSheetsOldCheatSheet.getCheats().remove(cheatVersion);
                    cheatSheetsOldCheatSheet = em.merge(cheatSheetsOldCheatSheet);
                }
            }
            for (CheatSheet cheatSheetsNewCheatSheet : cheatSheetsNew) {
                if (!cheatSheetsOld.contains(cheatSheetsNewCheatSheet)) {
                    cheatSheetsNewCheatSheet.getCheats().add(cheatVersion);
                    cheatSheetsNewCheatSheet = em.merge(cheatSheetsNewCheatSheet);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = cheatVersion.getId();
                if (findCheatVersion(id) == null) {
                    throw new NonexistentEntityException("The cheatVersion with id " + id + " no longer exists.");
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
            CheatVersion cheatVersion;
            try {
                cheatVersion = em.getReference(CheatVersion.class, id);
                cheatVersion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cheatVersion with id " + id + " no longer exists.", enfe);
            }
            Cheat cheat = cheatVersion.getCheat();
            if (cheat != null) {
                cheat.getVersions().remove(cheatVersion);
                cheat = em.merge(cheat);
            }
            List<CheatSheet> cheatSheets = cheatVersion.getCheatSheets();
            for (CheatSheet cheatSheetsCheatSheet : cheatSheets) {
                cheatSheetsCheatSheet.getCheats().remove(cheatVersion);
                cheatSheetsCheatSheet = em.merge(cheatSheetsCheatSheet);
            }
            em.remove(cheatVersion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CheatVersion> findCheatVersionEntities() {
        return findCheatVersionEntities(true, -1, -1);
    }

    public List<CheatVersion> findCheatVersionEntities(int maxResults, int firstResult) {
        return findCheatVersionEntities(false, maxResults, firstResult);
    }

    private List<CheatVersion> findCheatVersionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CheatVersion.class));
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

    public CheatVersion findCheatVersion(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CheatVersion.class, id);
        } finally {
            em.close();
        }
    }

    public int getCheatVersionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CheatVersion> rt = cq.from(CheatVersion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
