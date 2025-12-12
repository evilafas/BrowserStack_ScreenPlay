#language: es

  Característica: Como usuario de la pagina BrowserStack quiero realizar el proceso de compra de un producto

    Escenario: Compra exitosa
      Dado que el usuario se encuentra autenticado
      Cuando el usuario filtra los productos por la marca "Google"
      Y el usuario agrega al carrito el primer producto
      Y realiza el proceso de  checkout
      Entonces debe ser visible el mensaje de confirmacion "Your Order has been successfully placed."
