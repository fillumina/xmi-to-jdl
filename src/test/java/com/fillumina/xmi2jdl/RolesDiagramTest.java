package com.fillumina.xmi2jdl;

/**
 * Tests all possible relationships between entities.
 * Remember that required relationships with self are not allowed.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RolesDiagramTest extends AbstractTest {
    
    private static final String FILENAME = "roles-class-diagram.xmi";

    public RolesDiagramTest() {
//        super(FILENAME, false, System.out);
        super(FILENAME, false, null);
    }
    
    @Override
    public void createTests() {
        checkRequiredRelationshipsWithItself();
                
        assertRelationship("One", "oneToOne")
                .assertTarget("One")
                .assertType(RelationshipType.OneToOne)
                .isNotRequired();

        assertRelationship("One", "oneToOneReq")
                .assertTarget("OneReq")
                .assertType(RelationshipType.OneToOne)
                .isNotRequired();

        assertRelationship("One", "oneToMany")
                .assertTarget("Many")
                .assertType(RelationshipType.OneToMany)
                .isNotRequired();

        assertRelationship("One", "oneToManyReq")
                .assertTarget("ManyReq")
                .assertType(RelationshipType.OneToMany)
                .isNotRequired();

        
        
        assertRelationship("Many", "manyToOne")
                .assertTarget("One")
                .assertType(RelationshipType.ManyToOne)
                .isNotRequired();

        assertRelationship("Many", "manyToOneReq")
                .assertTarget("OneReq")
                .assertType(RelationshipType.ManyToOne)
                .isNotRequired();

        assertRelationship("Many", "manyToMany")
                .assertTarget("Many")
                .assertType(RelationshipType.ManyToMany)
                .isNotRequired();

        assertRelationship("Many", "manyToManyReq")
                .assertTarget("ManyReq")
                .assertType(RelationshipType.ManyToMany)
                .isNotRequired();

        

        assertRelationship("OneReq", "oneReqToOne")
                .assertTarget("One")
                .assertType(RelationshipType.OneToOne)
                .isRequired();

        assertRelationship("OneReq", "oneReqToMany")
                .assertTarget("Many")
                .assertType(RelationshipType.OneToMany)
                .isRequired();

        assertRelationship("OneReq", "oneReqToManyReq")
                .assertTarget("ManyReq")
                .assertType(RelationshipType.OneToMany)
                .isRequired();

        
        
        assertRelationship("ManyReq", "manyReqToOne")
                .assertTarget("One")
                .assertType(RelationshipType.ManyToOne)
                .isRequired();

        assertRelationship("ManyReq", "manyReqToOneReq")
                .assertTarget("OneReq")
                .assertType(RelationshipType.ManyToOne)
                .isRequired();

        assertRelationship("ManyReq", "manyReqToMany")
                .assertTarget("Many")
                .assertType(RelationshipType.ManyToMany)
                .isRequired();

    }

}
