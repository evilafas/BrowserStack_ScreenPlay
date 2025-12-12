package stepDefinitions;

import com.browserstack.questions.TextOfElement;
import com.browserstack.tasks.LoginTask;
import com.browserstack.Interacctions.OpenWebTask;
import io.cucumber.java.Before;
import io.cucumber.java.es.*;
import net.serenitybdd.screenplay.actions.Hit;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Click;
import org.openqa.selenium.Keys;

import static com.browserstack.userInterfaces.LoginPageUI.*;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.*;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.equalTo;

public class loginSteps {

    private static final String ACTOR = "Usuario";

    @Before
    public void before() {
        setTheStage(new OnlineCast());
        OnStage.setTheStage(new OnlineCast());
    }

    @Dado("que el usuario se encuentra en la pagina de BrowserStack")
    public void abrirPagina() {

        theActorCalled(ACTOR).attemptsTo(
                OpenWebTask.openWeb(),
                Enter.theValue("admin").into(By.id("username")),
                Enter.theValue("1234").into(By.id("password")),
                Click.on(By.id("loginBtn"))
        );
    }

    @Cuando("el usuario se loguea correctamente")
    public void login() {
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(INPUT_USERNAME, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue("demouser").into(INPUT_USERNAME),
                Hit.the(Keys.ENTER).into(INPUT_USERNAME),
                Enter.theValue("testingisfun99").into(INPUT_PASSWORD),
                Hit.the(Keys.ENTER).into(INPUT_PASSWORD),
                Click.on(BTN_LOGIN)
        );
    }

    @Entonces("el usuario {string} debera ser visible")
    public void validarUsuario(String text) {
        System.out.println("Validando usuario: " + text);
        theActorInTheSpotlight().should(
                seeThat(
                        TextOfElement.visible(By.xpath("//*")),
                        equalTo(text)
                )
        );
    }

}
