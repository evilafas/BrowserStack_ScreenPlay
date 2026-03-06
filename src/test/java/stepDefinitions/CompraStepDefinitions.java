package stepDefinitions;

import com.browserstack.interactions.AddProductToCart;
import com.browserstack.interactions.FiltrarPorMarca;
import com.browserstack.questions.TextOfElement;
import com.browserstack.tasks.CheckoutTask;
import com.browserstack.tasks.LoginTask;
import com.browserstack.tasks.OpenWebTask;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.actors.OnStage;

import static com.browserstack.userInterfaces.CheckoutPageUI.LBL_ORDER_CONFIRMATION;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.hamcrest.Matchers.equalTo;

public class CompraStepDefinitions {

    @Dado("que el usuario se encuentra autenticado")
    public void queElUsuarioSeEncuentraAutenticado() {
        theActorCalled("Comprador").wasAbleTo(
                OpenWebTask.openWeb(),
                LoginTask.login()
        );
    }

    @Cuando("el usuario filtra los productos por la marca {string}")
    public void elUsuarioFiltraLosProductosPorLaMarca(String strMarca) {
        OnStage.theActorInTheSpotlight().attemptsTo(
                FiltrarPorMarca.filter(strMarca)
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
    public void debeSerVisibleElMensajeDeConfirmacion(String strMensajeEsperado) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(TextOfElement.visible(LBL_ORDER_CONFIRMATION), equalTo(strMensajeEsperado))
        );
    }
}
