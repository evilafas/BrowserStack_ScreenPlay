#language: es
Característica: Como usuario de la pagina BrowserStack quiero logearme correctamente

  @Login @Exitoso
  Escenario: Login exitoso
    Dado que el usuario se encuentra en la pagina de BrowserStack
    Cuando el usuario se loguea correctamente
    Entonces el usuario "demouser" debera ser visible
