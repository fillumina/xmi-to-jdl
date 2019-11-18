package com.fillumina.xmi2jdl;

/**
 *
 * @author fra
 */
public class EntityDiagramValidator extends AbstractValidator {

    @Override
    void executeTests() {
        startTests();

        this.allConnectedToDiscountMustHaveNameField();

        endTests();
    }

    void allConnectedToDiscountMustHaveNameField() {
        test("allConnectedToDiscountMustHaveNameField");

        findEntityByName("Detail").ifPresentOrElse(( Entity detail) -> {
            detail.getAllRelationships().stream()
                    .map(e -> e.getOwner())
                    .filter(e -> e != detail && e.getFieldByName("name").isEmpty())
                    .forEach(e -> error("Entity missing 'name': ", e.getName()));

        }, () -> error("Detail not found!"));
    }
}
