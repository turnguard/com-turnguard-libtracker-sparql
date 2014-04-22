package com.turnguard.rdf.com.turnguard.libtracker.sparql;

import java.util.UUID;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author turnguard
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LibtrackerSparqlTest {
    
    private static Libtracker.TrackerSparqlConnection con = null;    
    private static final String uuid = UUID.randomUUID().toString();
    private static final String test_subject = "<urn:uuid:"+uuid+">";
    public LibtrackerSparqlTest() {
        System.out.println("Construct");        
    }
    
    @BeforeClass
    public static void setUpClass() throws Libtracker.TrackerException {
        con = Libtracker.getTrackerSparqlConnection();
    }
    
    @AfterClass
    public static void tearDownClass() {      
    }
    
    @Before
    public void setUp() {
        System.out.println("setUp");        
    }
    
    @After
    public void tearDown() {
        System.out.println("tearDown");
    }
    
    @Test
    public void test_001_insert_test_triple() throws Libtracker.TrackerException {
        System.out.println("test_insert_test_triple");
        String insert = "INSERT DATA { "+test_subject+" a rdfs:Resource }";
        con.update(insert);
    }
    
    @Test
    public void test_002_select_test_triple() throws Libtracker.TrackerException {
        System.out.println("test_select_test_triple");        
        Libtracker.TrackerSparqlCursor cursor = null;
        int countColumns = 0;
        try {
            cursor = con.query("SELECT * WHERE { "+test_subject+" ?p ?o . }");
            countColumns = cursor.getColumnsCount();
            while(cursor.next()){
                for(int i = 0; i < countColumns; i++){
                    System.out.print(cursor.getString(i) + " ");
                }
                System.out.println();
            }
        } finally {
            if(cursor!=null){
                try {cursor.close();} catch(Exception ee){}
            }
        }      
    }
    
    @Test
    public void test_003_delete_test_triple() throws Libtracker.TrackerException {
        System.out.println("test_delete_test_triple");        
        con.update("DELETE DATA { "+test_subject+" a rdfs:Resource }");              
    }
    
    @Test
    public void test2() {}
}
