package com.company.jmix_hrm.security;

import com.company.jmix_hrm.entity.User;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(
        name = "Trainee: View/Edit Self Record",
        code = TraineeRowLevelRole.CODE
)
public interface TraineeRowLevelRole {

    String CODE = "trainee-row-level-role";

    //    JPQL Row Level Policy - Used to filter the data when fetching the records from the database
    @JpqlRowLevelPolicy(
            entityClass = User.class,
            where = "{E}.id = :current_user_id")
    void traineeRowLevelRole();
    // In java annotation must be attached to class, field or method that's why we write method(), this method acts as placeholder and is never executed()
}
