/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gandev.csheat;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ares
 */
public class PersistenceTest {

    public PersistenceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void em() {
        EntityManager em = Persistence.createEntityManagerFactory("csheatPU").createEntityManager();
    }
}
