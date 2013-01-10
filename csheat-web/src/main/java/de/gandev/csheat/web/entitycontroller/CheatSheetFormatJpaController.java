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
import de.gandev.csheat.entity.CheatSheet;
import de.gandev.csheat.entity.CheatSheetFormat;
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
public class CheatSheetFormatJpaController implements Serializable {

    @PersistenceUnit(unitName = "csheatGlassfishPU")
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CheatSheetFormat cheatSheetFormat) {
        if (cheatSheetFormat.getCheatSheets() == null) {
            cheatSheetFormat.setCheatSheets(new ArrayList<CheatSheet>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CheatSheet> attachedCheatSheets = new ArrayList<CheatSheet>();
            for (CheatSheet cheatSheetsCheatSheetToAttach : cheatSheetFormat.getCheatSheets()) {
                cheatSheetsCheatSheetToAttach = em.getReference(cheatSheetsCheatSheetToAttach.getClass(), cheatSheetsCheatSheetToAttach.getId());
                attachedCheatSheets.add(cheatSheetsCheatSheetToAttach);
            }
            cheatSheetFormat.setCheatSheets(attachedCheatSheets);
            em.persist(cheatSheetFormat);
            for (CheatSheet cheatSheetsCheatSheet : cheatSheetFormat.getCheatSheets()) {
                CheatSheetFormat oldFormatOfCheatSheetsCheatSheet = cheatSheetsCheatSheet.getFormat();
                cheatSheetsCheatSheet.setFormat(cheatSheetFormat);
                cheatSheetsCheatSheet = em.merge(cheatSheetsCheatSheet);
                if (oldFormatOfCheatSheetsCheatSheet != null) {
                    oldFormatOfCheatSheetsCheatSheet.getCheatSheets().remove(cheatSheetsCheatSheet);
                    oldFormatOfCheatSheetsCheatSheet = em.merge(oldFormatOfCheatSheetsCheatSheet);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CheatSheetFormat cheatSheetFormat) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CheatSheetFormat persistentCheatSheetFormat = em.find(CheatSheetFormat.class, cheatSheetFormat.getId());
            List<CheatSheet> cheatSheetsOld = persistentCheatSheetFormat.getCheatSheets();
            List<CheatSheet> cheatSheetsNew = cheatSheetFormat.getCheatSheets();
            List<CheatSheet> attachedCheatSheetsNew = new ArrayList<CheatSheet>();
            for (CheatSheet cheatSheetsNewCheatSheetToAttach : cheatSheetsNew) {
                cheatSheetsNewCheatSheetToAttach = em.getReference(cheatSheetsNewCheatSheetToAttach.getClass(), cheatSheetsNewCheatSheetToAttach.getId());
                attachedCheatSheetsNew.add(cheatSheetsNewCheatSheetToAttach);
            }
            cheatSheetsNew = attachedCheatSheetsNew;
            cheatSheetFormat.setCheatSheets(cheatSheetsNew);
            cheatSheetFormat = em.merge(cheatSheetFormat);
            for (CheatSheet cheatSheetsOldCheatSheet : cheatSheetsOld) {
                if (!cheatSheetsNew.contains(cheatSheetsOldCheatSheet)) {
                    cheatSheetsOldCheatSheet.setFormat(null);
                    cheatSheetsOldCheatSheet = em.merge(cheatSheetsOldCheatSheet);
                }
            }
            for (CheatSheet cheatSheetsNewCheatSheet : cheatSheetsNew) {
                if (!cheatSheetsOld.contains(cheatSheetsNewCheatSheet)) {
                    CheatSheetFormat oldFormatOfCheatSheetsNewCheatSheet = cheatSheetsNewCheatSheet.getFormat();
                    cheatSheetsNewCheatSheet.setFormat(cheatSheetFormat);
                    cheatSheetsNewCheatSheet = em.merge(cheatSheetsNewCheatSheet);
                    if (oldFormatOfCheatSheetsNewCheatSheet != null && !oldFormatOfCheatSheetsNewCheatSheet.equals(cheatSheetFormat)) {
                        oldFormatOfCheatSheetsNewCheatSheet.getCheatSheets().remove(cheatSheetsNewCheatSheet);
                        oldFormatOfCheatSheetsNewCheatSheet = em.merge(oldFormatOfCheatSheetsNewCheatSheet);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = cheatSheetFormat.getId();
                if (findCheatSheetFormat(id) == null) {
                    throw new NonexistentEntityException("The cheatSheetFormat with id " + id + " no longer exists.");
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
            CheatSheetFormat cheatSheetFormat;
            try {
                cheatSheetFormat = em.getReference(CheatSheetFormat.class, id);
                cheatSheetFormat.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cheatSheetFormat with id " + id + " no longer exists.", enfe);
            }
            List<CheatSheet> cheatSheets = cheatSheetFormat.getCheatSheets();
            for (CheatSheet cheatSheetsCheatSheet : cheatSheets) {
                cheatSheetsCheatSheet.setFormat(null);
                cheatSheetsCheatSheet = em.merge(cheatSheetsCheatSheet);
            }
            em.remove(cheatSheetFormat);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CheatSheetFormat> findCheatSheetFormatEntities() {
        return findCheatSheetFormatEntities(true, -1, -1);
    }

    public List<CheatSheetFormat> findCheatSheetFormatEntities(int maxResults, int firstResult) {
        return findCheatSheetFormatEntities(false, maxResults, firstResult);
    }

    private List<CheatSheetFormat> findCheatSheetFormatEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CheatSheetFormat.class));
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

    public CheatSheetFormat findCheatSheetFormat(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CheatSheetFormat.class, id);
        } finally {
            em.close();
        }
    }

    public int getCheatSheetFormatCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CheatSheetFormat> rt = cq.from(CheatSheetFormat.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
