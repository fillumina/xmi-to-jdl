package com.fillumina.xmi2jdl;

/**
 *
 * @author fra
 */
public class EntityDiagramValidator extends AbstractValidator {

    @Override
    void executeTests() {
        allConnectedToDiscountMustHaveNameField();
        allPricesShouldHaveCreationTime();
        allConnectedToMediaShouldHaveMediaAsField();
        allPricesNotOptionPriceShouldHavePrice();
        allEntitisMustHaveADisplayField();
    }

    void allConnectedToDiscountMustHaveNameField() {
        test("allConnectedToDiscountMustHaveNameField");

        findEntityByName("Detail").ifPresentOrElse(( Entity detail) -> {
            detail.getAllRelationships().stream()
                    .map(e -> e.getOwner())
                    .filter(e -> e != detail)
                    .peek(e -> log("checking " + e.getName())) 
                    .filter(e -> e.getFieldByName("name").isEmpty())
                    .forEach(e -> error("Entity missing 'name': ", e.getName()));

        }, () -> error("Detail not found!"));
        
        endTest();
    }

    void allPricesShouldHaveCreationTime() {
        test("allPricesShouldHaveCreationTime");

        findEntitiesByRegexp(".*Price").forEach( e -> {
            log("checking ", e.getName());
            long count = e.getDataTypes().stream()
                    .filter( dt -> dt.getName().equals("creationDate"))
                    .count();
            if (count == 0) {
                error("Price missing 'creationDate':", e.getName());
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
                error("Missing 'price':", e.getName());
            }
        });
        
        endTest();
    }

    void allConnectedToMediaShouldHaveMediaAsField() {
        test("allConnectedToMediaShouldHaveMediaAsField");

        findEntityByName("MediaContent").ifPresentOrElse(( Entity media) -> {
            media.getAllRelationships().stream()
                    .filter(e -> e.getOwner() != media && 
                            !e.getOwner().getName().equals("MimeData"))
                    .peek(e -> log("checking " + e.getOwner().getName(), e.getName())) 
                    .filter(e -> !e.getName().equals("media"))
                    .forEach(e -> error("Entity", e.getOwner().getName(), 
                            "missing 'media':", e.getName()));

        }, () -> error("MediaContent not found!"));
        
        endTest();
    }

    void allEntitisMustHaveADisplayField() {
        super.allEntitisMustHaveADisplayField(
            "Contact", "OrderOptionItem", "ColorSizeVariant", 
            "Tag", "OrderStatusType", "CustomizationOption",
            "OrderStatus", "Material", "OrderAreaCustomization",
            "OrderAreaCustomization", "MediaContent", 
            "ColorVariant", "User", "OrderProduct",
            "OrderOptionCustomization"
        );
    }
}
