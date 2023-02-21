// This class was generated on Wed, 22 Aug 2018 20:48:41 PDT by version 0.1.0-dev+904328-dirty of Braintree SDK Generator
// StatusDetails.java
// @version 0.1.0-dev+904328-dirty
// @type object
// @data H4sIAAAAAAAC/6yPwUoDMRCG7z7FMOdFPO+tkCpFWYsWLyJkNLMmEJN1JkGC9N2lXViVXnv8v5+Z+eYbd21i7PGxUKkKhguFqNjhE0mg18gDfRx67PCW228wrG8SphJywh53nsHNo5BHKJ5BeKzJgR73XmKHKxFq87WrDh+Y3H2KDfuRovIBfNYg7BawlTyxlMCK/fPiqUVCej/1EybN6Z/lgk5d5wq+fPvr6kmP0W7Xg9kMNxaygL1ebe7Wxp7pk1Rj3L/sL34AAAD//w==
// DO NOT EDIT
package com.paypal.payments;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.paypal.http.annotations.*;
/**
 * The details of the refund status.
 */
@Model
public class StatusDetails {

    // Required default constructor
    public StatusDetails() {}

	/**
	* The reason why the refund has the `PENDING` or `FAILED` status.
	*/
	@SerializedName("reason")
	private String reason;

	public String reason() { return reason; }

	public StatusDetails reason(String reason) {
	    this.reason = reason;
	    return this;
	}
}
