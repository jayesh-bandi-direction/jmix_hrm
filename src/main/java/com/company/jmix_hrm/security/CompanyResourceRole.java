package com.company.jmix_hrm.security;

import com.company.jmix_hrm.entity.Company;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(
        name = "Company Resource Role",
        code = CompanyResourceRole.CODE,
        scope = SecurityScope.API
)
public interface CompanyResourceRole {

    String CODE = "company-resource-role";

    @EntityPolicy(entityClass = Company.class, actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityClass = Company.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void companyResourceRole();
}
