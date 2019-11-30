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
        //allEntitisMustHaveADisplayField();
        
        allEntitisMustHaveADisplayFieldExcept();
        forbiddenEntityNameCheck("Order", ".*Detail", "UserAuthority");
        allConnectedEntitiesMustHaveRelationNamedTheSame();
        noCircularOneToOneWithMapIdRelationships();
    }
    
    void allPricesShouldHaveCreationTime() {
        test("allPricesShouldHaveCreationTime");

        findEntitiesByRegexp(".*Price").forEach( e -> {
            log("checking ", e.getName());
            long count = e.getDataTypes().stream()
                    .filter( dt -> dt.getName().equals("creationDate"))
                    .count();
            if (count == 0) {
                warning("Price missing 'creationDate':", e.getName());
            }
        });
        
        endTest();
    }

    void allPricesNotOptionPriceShouldHavePrice() {
        test("allPricesNotOptionPriceShouldHavePrice");

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

//    void allEntitisMustHaveADisplayField() {
//        super.allEntitisMustHaveADisplayFieldExcept(
//            "Contact", "OrderOptionItem", "ColorSizeVariant", 
//            "Tag", "OrderStatusType", "CustomizationOption",
//            "OrderStatus", "Material", "OrderAreaCustomization",
//            "OrderAreaCustomization", "MediaContent", 
//            "ColorVariant", "User", "OrderProduct",
//            "OrderOptionCustomization"
//        );
//    }
}
