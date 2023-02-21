package com.mycompany.hosted.checkoutFlow.jpa;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.exception_handler.EhrLogger;

import java.util.List;
import com.mycompany.hosted.model.validation.States;

@Repository
public class SupportedValidationImpl implements SupportedValidation{
	
	@PersistenceContext(unitName=WebFlowConstants.SUPPORT_UNIT)
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<States> getStates() {		
		
		String sql = "Select s from States s";
		
		Query query = em.createQuery(sql);
		
		List<States> states = (List<States>)query.getResultList();
		
		if(states == null || states.isEmpty())
			throw new RuntimeException(EhrLogger.doMessage(this.getClass(),
					"getStates", "Transaction returned a null or empty states list"));
		
		return states;
		
	}
	
	@Transactional
	public States getStateByCode(String code) {
		
		return em.find(States.class, code);
	}

}
