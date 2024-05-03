<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
  .rightAlign{
     text-align: right
  }
  .usd {
    font-size:8pt
  }
  .totalRow {
    background-color: #e6e6e6
  }
</style>

 <table class="table" style="width:400px">
        <caption>Items:</caption>
    <c:forEach var="item" items="${cart.cartList}">
        <tr>
            <td colspan="3">
                <span style="color:#036fab;">${item.book.title}</span>
            </td>
        </tr>
        <tr>
            <td>
               <span>${item.quantity}</span> 
            </td> 
            <td class="rightAlign">               
               <fmt:formatNumber 
                value="${item.book.price}" type="currency" currencySymbol="$"/>
            </td>
       
            <td class="rightAlign">
                 <fmt:formatNumber 
                value="${item.extPrice}" type="currency" currencySymbol="$" /><span class="usd">USD</span> 
            </td>
        
        </tr>       
    </c:forEach>
    <tr class="totalRow">
       <td>&nbsp;</td>
       <td class="rightAlign"><label>Subtotal:</label></td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${cart.subtotal}" type="currency" currencySymbol="$"/>
                <span class="usd">USD</span>
                </td>
    </tr>
    <tr class="totalRow">
       <td>&nbsp;</td>
       <td class="rightAlign"><label>Tax:</label></td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${cart.taxAmount}" type="currency"  />
                <span class="usd">USD</span>
                </td>
    </tr>
    <tr class="totalRow">
       <td>&nbsp;</td>
       <td class="rightAlign"><label>Shipping Fee:</label></td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${cart.shippingFee}" type="currency" currencySymbol="$"/>
                <span class="usd">USD</span>
                </td>
    </tr>
    <tr class="totalRow">
       <td>&nbsp;</td>
       <td class="rightAlign"><label>Grand Total:</label></td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${cart.grandTotal}" type="currency" currencySymbol="$" />
                <span class="usd">USD</span></td>
    </tr>
    </table><!--end div table-->    

