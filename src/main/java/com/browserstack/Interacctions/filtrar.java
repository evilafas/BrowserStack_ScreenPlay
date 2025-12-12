package com.browserstack.Interacctions;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;


import static net.serenitybdd.screenplay.Tasks.instrumented;

public class filtrar implements Interaction {

    private String product;

    public filtrar(String product){
        this.product = product;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Target MARCA = Target.the("Opcion de marca").located(By.xpath("//*[text()='"+ product +"']"));
        actor.attemptsTo(
                Click.on(MARCA)
        );
    }
    public static filtrar filter(String product){
        return instrumented(filtrar.class, product);
    }

}
