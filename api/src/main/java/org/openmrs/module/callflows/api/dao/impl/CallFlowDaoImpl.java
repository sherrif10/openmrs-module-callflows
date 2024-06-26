/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;

import java.util.List;

public class CallFlowDaoImpl implements CallFlowDao {

    private DbSessionFactory dbSessionFactory;
    private Class mappedClass;

    public CallFlowDaoImpl() {
        this.mappedClass = CallFlow.class;
    }

    @Override
    public CallFlow findByName(String name) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("name", name));

        return (CallFlow) crit.uniqueResult();
    }

    @Override
    public List<CallFlow> findAllByName(String prefix) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.like("name", prefix, MatchMode.START));
        crit.add(Restrictions.eq("retired", Boolean.FALSE));

        return crit.list();
    }

    @Override
    public CallFlow create(CallFlow callFlow) {
        getSession().saveOrUpdate(callFlow);
        return callFlow;
    }

    @Override
    public CallFlow update(CallFlow callFlow) {
        getSession().saveOrUpdate(callFlow);
        return callFlow;
    }

    @Override
    public CallFlow findById(Integer id) {
        Object result = getSession().get(this.mappedClass, id);
        return result != null ? (CallFlow) result : null;
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("delete from callFlow.CallFlow").executeUpdate();
    }

    @Override
    public void delete(CallFlow callFlow) {
        getSession().delete(callFlow);
    }

    private DbSession getSession() {
        return dbSessionFactory.getCurrentSession();
    }

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }
}
