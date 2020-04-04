package com.fillumina.xmi2jdl;

/**
 * Tests some real cases
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CompanyTest extends AbstractTest {

    private static final String FILENAME = "company.xmi";

    public CompanyTest() {
//        super(FILENAME, System.out);
        super(FILENAME, null);
    }
    
    @Override
    public void createTests() {
        allEntitisMustHaveADisplayFieldExcept();
        noCircularOneToOneWithMapIdRelationships();
        checkAllNamesFirstCharLowerCaseAndMaxLength();
        
        assertRelationship("Car", "authorized")
                .assertTarget("Employee")
                .assertType(RelationshipType.ManyToOne)
                .isNotRequired();

        assertRelationship("Employee", "hasContact")
                .assertTarget("Address")
                .assertType(RelationshipType.OneToOne)
                .isNotRequired();
        
        assertRelationship("Employee", "worksIn")
                .assertTarget("Branch")
                .assertType(RelationshipType.ManyToMany)
                .isNotRequired();
        
        assertRelationship("Employee", "secretaryOf")
                .assertTarget("Employee")
                .assertType(RelationshipType.OneToMany)
                .isNotRequired();
        
        assertRelationship("Branch", "chief")
                .assertTarget("Employee")
                .assertType(RelationshipType.ManyToOne)
                .isNotRequired();
        
        assertRelationship("Company", "branches")
                .assertTarget("Branch")
                .assertType(RelationshipType.OneToMany)
                .isRequired();
    }
        
}
