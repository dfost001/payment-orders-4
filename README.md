# payment-orders-4

PayPal Advanced Integration



**Description:**



* This application implements a PayPal online checkout sequence using Spring Webflow 2.2.4 and PayPal Orders V2 Rest API.
* Payer information is obtained on a form defined by the PayPal JavaScript API.



**Application Website:** [https://dinah-foster.com/payment-orders-4](https://dinah-foster.com/payment-orders-4)



**Execution Platform**



* **Machine Host:** EC2 Instance
* **Servlet Container:** Tomcat 8
* **Compiler**: Eclipse 2022
* **Persistence Provider:** MySQL 8
* **ORM Provider**: Hibernate 4
* **MVC Framework:** Spring 4.3



**Testing Credentials**



* **Credit Card Number**: 4032 0370 6489 1415
* **Expiration Date:** 08/2028
* **CVV2:** 122
* **Customer Id:** 4



**Webflow Navigation API:**



**Folder:** /WEB-INF/flows/checkout/checkout-flow.xml



A flow consists of an XML definition that represents an ordered sequence of views.
The XMl schema includes the following state elements to execute the navigational logic.



* **<action-state>** Executes sequential or conditional logic.
* **<transition>** Defines attributes to drive the flow.
* **<decision-state>** Executes conditional logic. The Spring method returns a Boolean.
* **<view-state>** Defines a view to render. Transitions on a view state are bound to HTML input.
* **<end-state>** A transition to an end-state terminates the current execution.
* **<subflow-state>** Defines another XML definition to execute.



**Summary:**  
The checkout flow sequence requires a non-empty cart on entry. Within the flow, first a valid customer is obtained, then shipping addresses are presented for selection. After selection, credit-card details are obtained via the PayPal JavaScript API and hosted card fields. Presenting details for review and capturing a payment are handled by web-flow server-side components. If the remote transaction completes, the order is persisted and there is a redirect to an MVC handler to display the receipt or process a refund.



**Precondition:**

* Since a precondition for the flow is a non-empty cart, the first state is an action-state that evaluates the contents of the MVC shopping cart component.
* If the flow is requested via browser navigation, it is possible to re-enter the flow with an emptied cart.
* A custom CartEmpty exception is thrown and handled within the flow by the on-exception attribute of the transition.
* The ‘to’ attribute is set to an error view. Html controls on the error view are bound to end-state.





```java

  <action-state id="throwEmptyCart"  >
      
       <evaluate expression="webflowDebug.throwEmptyCart(cart, flowRequestContext)" /> 
       
       <transition to="evalPaymentState" />
       
       <transition on-exception="com.mycompany.hosted.checkoutFlow.exceptions.WebflowCartEmptyException"    
            to="errNavigation"     />  
   </action-state> 

```  



**Syntax Notes:**



* <**action-state**> Must contain at least one <**evaluate**>.
* The expression attribute is usually set to a method invocation on a Spring bean .
* The method may return a String, Boolean, object or void. (Not Numbers)
* To define the next state to execute a <**transition**> sub-element is defined directly after <**evaluate**>.
* The possible result is assigned to the **‘on’** attribute. The **‘to’** attribute defines the **‘id’** of the next state.
* If more than one possible result,  a series of transition elements.
* If return value is void, the **‘on’** attribute is omitted.
* If the method throws an exception, it can be caught by the **<on-exception>** sub-element



**Feature**

•	If no exception is thrown from the first state, the transition is to an action procedure that accesses session attributes

* This evaluation  allows the client to exit the flow and  re-enter without repetition.

•	Evaluation of the session at flow start will also transparently synchronize the view on a browser-navigation error.

•	The procedure returns the string value of enum corresponding to the current session state.

* A series of transitions will execute one of the four views according to the enum-value.


```java

<action-state id="evalPaymentState">       
       <evaluate expression="paymentStateAttrs.evalPaymentState(
                   flowRequestContext, ERR\_GET\_DETAIL, ERR\_ON\_CAPTURE, myFlowAttrs)" />    
                   
       <transition on="ERR\_GET\_DETAIL"  to="getDetails" /> 
       
       <transition on="ERR\_ON\_CAPTURE"   to="capturePayment"  />   
       
       <transition on="DETAILS\_COMPLETED" to="showDetails" />    
        
       <transition on="SHIP\_SELECTED" to="paymentButtons" />
       
        <transition on="LOGGED\_IN" to="selectShipAddress" />
                    
       <transition on="NONE" to="login" />  
     </action-state>    

```

