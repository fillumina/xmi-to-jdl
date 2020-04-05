package com.fillumina.xmi2jdl;

/**
 * Tests attributes and special cases.
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ClassDiagramTest extends AbstractTest {

    private static final String FILENAME = "class-diagram.xmi";

    public ClassDiagramTest() {
//        super(FILENAME, System.out, true);
        super(FILENAME, null, true);
    }
    
    @Override
    public void createTests() {
        allEntitisMustHaveADisplayFieldExcept();
        noCircularOneToOneWithMapIdRelationships();
        checkAllNamesFirstCharLowerCaseAndMaxLength();

        // JHipster provides its own User
        assertEntityPresent("User");
        
        assertRelationship("Contact", "user")
                .assertTarget("User")
                .assertType(RelationshipType.OneToOne)
                .isUnidirectional()
                .isRequired();

        assertRelationship("Contact", "address")
                .assertTarget("Address")
                .assertType(RelationshipType.OneToMany)
                .isNotUnidirectional()
                .isRequired();
        
        assertAttribute("Contact", "name")
                .assertType("String")
                .isDisplay();

        assertAttribute("Contact", "age")
                .assertType("Integer")
                .isNotDisplay();
        
        assertAttribute("Contact", "status")
                .assertType("Status");
        
        assertEnumValues("Status", "MARRIED", "FREE");
        
        assertAttributeNotPresent("Contact", "spouseName");
    }
    
}
