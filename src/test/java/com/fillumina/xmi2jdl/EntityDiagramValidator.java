package com.fillumina.xmi2jdl;

/**
 *
 * @author fra
 */
public class EntityDiagramValidator extends AbstractValidator {

    @Override
    void executeTests() {
        setVerbose(true);
        
        allPricesShouldHaveCreationTime();
        allPricesNotOptionPriceShouldHavePrice();
//        allEntitisMustHaveADisplayFieldExcept(
//            "Contact", "OrderOptionItem", "ColorSizeVariant", 
//            "Tag", "OrderStatusType", "CustomizationOption",
//            "OrderStatus", "Material", "OrderAreaCustomization",
//            "OrderAreaCustomization", "MediaContent", 
//            "ColorVariant", "User", "OrderProduct",
//            "OrderOptionCustomization"
//        );
        
        allEntitisMustHaveADisplayFieldExcept();
        forbiddenEntityNameCheck("Order", ".*Detail", "UserAuthority");
        allConnectedEntitiesMustHaveRelationNamedTheSame();
        noCircularOneToOneWithMapIdRelationships();
        
        allConnectedMustHaveUnidirectionalRelationExcept("Article");
        
        findAllEntitiesWithFieldName("imageUrl");
        
        showAllEntitiesConnectedTo("Article");
        
        allFieldsMustHaveValidation("active", "required");
    }
    
    void allPricesShouldHaveCreationTime() {
        test("all Prices should have CreationTime");

        findEntitiesByRegexp(".*Price").forEach( e -> {
            log("checking ", e.getName());
            long count = e.getDataTypes().stream()
                    .filter( dt -> dt.getName().equals("creationTime") || 
                            dt.getName().equals("removalTime"))
                    .count();
            if (count != 2) {
                warning("Price missing 'creationDate':", e.getName());
            }
        });
        
        endTest();
    }
    
    void allPricesNotOptionPriceShouldHavePrice() {
        test("all Prices but not OptionPrice should have price field");

        findEntitiesByRegexp("^((?!Option).)*Price$").forEach( e -> {
            log("checking ", e.getName());
            long count = e.getDataTypes().stream()
                    .filter( dt -> dt.getName().equals("price"))
                    .count();
            if (count == 0) {
                warning("Missing 'price':", e.getName());
            }
        });
        
        endTest();
    }

}
