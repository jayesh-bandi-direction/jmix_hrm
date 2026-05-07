package com.company.jmix_hrm.security;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.User;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "ManagerRowLevelRole", code = ManagerRowLevelRole.CODE)
public interface ManagerRowLevelRole {

    String CODE = "manager-row-level-role";

    @JpqlRowLevelPolicy(entityClass = Department.class, where = "{E}.company = :current_user_company")
    void department();

    @JpqlRowLevelPolicy(entityClass = User.class, where = "{E}.company = :current_user_company")
    void user();
}