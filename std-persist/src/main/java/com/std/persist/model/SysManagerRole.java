package com.std.persist.model;

import java.io.Serializable;

public class SysManagerRole implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys-manager-role.n_id
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys-manager-role.n_manager_id
     *
     * @mbggenerated
     */
    private Long managerId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys-manager-role.n_role_id
     *
     * @mbggenerated
     */
    private Long roleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sys-manager-role
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys-manager-role.n_id
     *
     * @return the value of sys-manager-role.n_id
     *
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys-manager-role.n_id
     *
     * @param id the value for sys-manager-role.n_id
     *
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys-manager-role.n_manager_id
     *
     * @return the value of sys-manager-role.n_manager_id
     *
     * @mbggenerated
     */
    public Long getManagerId() {
        return managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys-manager-role.n_manager_id
     *
     * @param managerId the value for sys-manager-role.n_manager_id
     *
     * @mbggenerated
     */
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys-manager-role.n_role_id
     *
     * @return the value of sys-manager-role.n_role_id
     *
     * @mbggenerated
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys-manager-role.n_role_id
     *
     * @param roleId the value for sys-manager-role.n_role_id
     *
     * @mbggenerated
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}