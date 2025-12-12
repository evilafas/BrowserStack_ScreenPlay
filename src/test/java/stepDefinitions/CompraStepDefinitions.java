package stepDefinitions;

import com.browserstack.Interacctions.AddProductToCart;
import com.browserstack.Interacctions.filtrar;
import com.browserstack.questions.TextOfElement;
import com.browserstack.tasks.CheckoutTask;
import com.browserstack.tasks.LoginTask;
import com.browserstack.Interacctions.OpenWebTask;
import net.serenitybdd.screenplay.actors.OnStage;
import cucumber.api.java.es.Cuando;
import cucumber.api.java.es.Dado;
import cucumber.api.java.es.Entonces;
import static com.browserstack.userInterfaces.CheckoutPageUI.TXT_CONFIRMATION;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.hamcrest.Matchers.equalTo;

public class CompraStepDefinitions {



    @Dado("que el usuario se encuentra autenticado")
    public void queElUsuarioSeEncuentraAutenticado() {
        theActorCalled("Tester").wasAbleTo(
                OpenWebTask.openWeb(),
                LoginTask.login()
        );
    }
    @Cuando("el usuario filtra los productos por la marca {string}")
    public void elUsuarioFiltraLosProductosPorLaMarca(String marca) {
        OnStage.theActorInTheSpotlight().attemptsTo(
                filtrar.filter(marca)
        );
    }

    @Cuando("el usuario agrega al carrito el primer producto")
    public void elUsuarioAgregaAlCarritoElPrimerProducto() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                AddProductToCart.add()
        );
    }

    @Cuando("realiza el proceso de  checkout")
    public void realizaElProcesoDeCheckout() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                CheckoutTask.checkout()
        );
    }

    @Entonces("debe ser visible el mensaje de confirmacion {string}")
    public void debeSerVisibleElMensajeDeConfirmacion(String text) {
        OnStage.theActorInTheSpotlight().should(
            seeThat(TextOfElement.visible(TXT_CONFIRMATION), equalTo(text))
        );
    }
}
