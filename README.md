# payment-orders-4

PayPal Advanced Integration



**Description:**



* This application implements a PayPal online checkout sequence using Spring Webflow 2.2.4 and PayPal Orders V2 Rest API.
* Payer information is obtained on a form defined by the PayPal JavaScript API.



_Please use the URL below to interact with the application executing on a platform:_



* **URL:** [https://dinah-foster.com/payment-orders-4](https://dinah-foster.com/payment-orders-4)
* **Machine Host:** EC2 Instance
* **Servlet Container:** Tomcat 8
* **Compiler**: Eclipse 2022
* **Persistence Provider:** MySQL 8
* **ORM Provider**: Hibernate 4
* **MVC Framework:** Spring 4.3

**Precondition:** Since a precondition for the flow is a non-empty cart, the first state is an action-state that evaluates the contents of the MVC shopping cart component. 
* If the flow is requested via browser navigation, it is possible to re-enter the flow with an emptied cart. A custom CartEmpty exception is thrown and handled within the flow by the on-exception attribute of the transition. The ‘to’ attribute is set to an error view. Html controls on the error view are bound to end-state.

```java

  <action-state id="throwEmptyCart"  >
      
       <evaluate expression="webflowDebug.throwEmptyCart(cart, flowRequestContext)" /> 
       
       <transition to="evalPaymentState" />
       
       <transition on-exception="com.mycompany.hosted.checkoutFlow.exceptions.WebflowCartEmptyException"    
            to="errNavigation"     />  
   </action-state> 

```  


