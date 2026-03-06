package stepDefinitions;

import com.browserstack.questions.TextOfElement;
import com.browserstack.tasks.LoginTask;
import com.browserstack.tasks.OpenWebTask;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static com.browserstack.userInterfaces.HomePageUI.TXT_USERNAME;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;

public class LoginStepDefinitions {

    private static final String ACTOR = "Usuario";

    @Dado("que el usuario se encuentra en la pagina de BrowserStack")
    public void abrirPagina() {
        theActorCalled(ACTOR).wasAbleTo(
                OpenWebTask.openWeb()
        );
    }

    @Cuando("el usuario se loguea correctamente")
    public void login() {
        theActorInTheSpotlight().attemptsTo(
                LoginTask.login()
        );
    }

    @Entonces("el usuario {string} debera ser visible")
    public void validarUsuario(String strNombreUsuario) {
        theActorInTheSpotlight().should(
                seeThat(
                        TextOfElement.visible(TXT_USERNAME),
                        equalTo(strNombreUsuario)
                )
        );
    }
}
