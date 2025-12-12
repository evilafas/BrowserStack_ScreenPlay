package com.browserstack.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;

public class TextOfElement implements Question<String> {

    private Target element;

    public TextOfElement (Target element){
        this.element = element;
    }

    public static TextOfElement visible(Target element){
        return new TextOfElement(element);
    }

    @Override
    public String answeredBy(Actor actor) {
        return element.resolveFor(actor).getText();
    }
}
