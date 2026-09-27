package com.fillumina.xmi2jdl;

/**
 * Tests attributes and special cases.
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ClassDiagramTest extends AbstractTest {

    private static final String FILENAME = "class-diagram.xmi";

    public ClassDiagramTest() {
//        super(FILENAME, true, System.out);
        super(FILENAME, true, null);
    }
    
    @Override
    public void createTests() {
        allEntitisMustHaveADisplayFieldExcept();
        noCircularOneToOneWithMapIdRelationships();
        checkAllNamesFirstCharLowerCaseAndMaxLength();

        assertEntity("Address")
                .isFilter()
                .isPagination();

        assertEntity("Contact")
                .isSkipClient()
                .isInfiniteScroll();

        assertEntity("User")
                .isSkipServer();
        
        // the derived identifier marker is JHipster 6 and 7 syntax: it is
        // kept as a flag and never written to the JDL, which JHipster 9
        // would reject
        assertRelationship("Contact", "user")
                .assertTarget("User")
                .assertType(RelationshipType.OneToOne)
                .isUnidirectional()
                .isRequired()
                .isMapId()
                .assertValidation("");

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
                .isNotDisplay()
                .assertValidation("max(110) min(1)");
        
        assertAttribute("Contact", "status")
                .assertType("Status");
        
        assertAttribute("Address", "email")
                .assertType("String")
                .isDisplay();
        
        assertEnumValues("Status", "MARRIED", "FREE");
        
        assertAttributeNotPresent("Contact", "spouseName");
    }
    
}
