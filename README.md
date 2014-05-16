[OpenCFDIMovil](https://www.cfdimovil.com.mx/)
================================

Esta aplicacion implementa codigo del proyecto [factura-electronica](https://github.com/bigdata-mx/factura-electronica) 
con algunas modificaciones para correr sobre Appengine ademas de modificaciones sobre las validaciones,
necesarias para hacer la transformacion JSON-Objeto-XML y viceversa.

Utilizando los servicios de Google Cloud Endpoints, App Engine, Google Cloud Message
se porta la funcionalidad de facturacion desde cualquier origen utilizando JSON.

API de Facturacion expuesta de ejemplo [Asegurada con OAuth2](https://apis-explorer.appspot.com/apis-explorer/?base=https://opencfdimovil.appspot.com/_ah/api#p/)

Cliente de ejemplo para consumir los enpoints:
[CFDImovil Android](https://play.google.com/store/apps/details?id=com.cfdimovil.app.open)

Este cliente implementa el consumo de los servicios de prueba de ECODEX [7]

Gracias a Eduardo Serrano por su apoyo en la implementacion (eserrano@ecodex.com.mx)

## Products
- [App Engine][1]
- [Google Cloud Message][2]

## Language
- [Java][2]

## APIs
- [Google Cloud Endpoints][3]
- [Google App Engine Maven plugin][6]

## Setup Instructions

1. Cambia el valor de `application` en `appengine-web.xml` con el app ID
   que registraste en la consola de Appengine.

1. Cambia los valores del archivo `src/com/reemmy/common/Constant.java`
   con los respectivos IDs que registraste en el API console.[4].

1. mvn clean install

1. Corre la aplicacion con 'mvn appengine:enhance appengine:devserver' y
   confirma que este corriendo visitando la siguiente url:
   https://[tu-app].appspot.com/_ah/api/explorer, tendras que ver los
   endpoints que utilizara cualquier app para generar las facturas.

1. Puedes utilizar las librerias para consumir los endpoints utilizando
   `mvn appengine:endpoints_get_client_lib`

1. Para hacer deploy es necesario modificar tus key de produccion en
   `src/com/reemmy/common/Constant.java`
   asi como las URL de produccion para el uso de ECODEX [7]


[1]: https://developers.google.com/appengine
[2]: http://java.com/en/
[3]: https://developers.google.com/appengine/docs/java/endpoints/
[4]: https://code.google.com/apis/console
[5]: https://localhost:8080/
[6]: https://developers.google.com/appengine/docs/java/tools/maven
[7]: http://www.ecodex.com.mx/
