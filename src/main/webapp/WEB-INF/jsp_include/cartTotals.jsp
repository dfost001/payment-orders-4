<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style>
   .rightAlign {
       text-align:right
   }
</style>
<table class="table table-condensed">
                <caption>Purchase Amount:</caption>
                <tr>
                    <td>Subtotal:</td>
                    <td class="rightAlign">
                      &dollar; ${sessionScope.cart.formattedSubtotal} </td>
                </tr>
                <tr>
                    <td>Shipping Fee:</td>
                    <td class="rightAlign">&dollar; ${sessionScope.cart.formattedShipping}</td>
                </tr>
                <tr>
                    <td>Tax:</td>
                    <td class="rightAlign">&dollar; ${sessionScope.cart.formattedTax}</td>
                </tr>
                <tr>
                    <td>Grand Total:</td>
                    <td class="rightAlign">
                        &dollar; ${sessionScope.cart.formattedGrand}</td>
                </tr>
            </table>