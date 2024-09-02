package it.water.permission.model;

import com.fasterxml.jackson.annotation.JsonView;
import it.water.core.api.permission.Permission;
import it.water.core.api.permission.ProtectedEntity;
import it.water.core.api.service.rest.WaterJsonView;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AccessControl;
import it.water.core.permission.annotations.DefaultRoleAccess;
import it.water.core.validation.annotations.NoMalitiusCode;
import it.water.core.validation.annotations.NotNullOnPersist;
import it.water.permission.actions.PermissionsActions;
import it.water.repository.jpa.model.AbstractJpaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * @Generated by Water Generator
 * Permission Entity Class.
 */
//JPA
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"roleId", "userId", "entityResourceName", "resourceId"}))
@Access(AccessType.FIELD)
//Lombok
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = {"id", "name", "entityResourceName", "actionIds"})
//Actions and default roles access
@AccessControl(availableActions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE, PermissionsActions.GIVE_PERMISSIONS, PermissionsActions.LIST_ACTIONS},
        rolesPermissions = {
                //Admin role can do everything
                @DefaultRoleAccess(roleName = WaterPermission.DEFAULT_MANAGER_ROLE, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE, PermissionsActions.GIVE_PERMISSIONS, PermissionsActions.LIST_ACTIONS}),
                //Viwer has read only access
                @DefaultRoleAccess(roleName = WaterPermission.DEFAULT_VIEWER_ROLE, actions = {CrudActions.FIND, CrudActions.FIND_ALL, PermissionsActions.LIST_ACTIONS}),
                //Editor can do anything but remove
                @DefaultRoleAccess(roleName = WaterPermission.DEFAULT_EDITOR_ROLE, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, PermissionsActions.LIST_ACTIONS})
        })
public class WaterPermission extends AbstractJpaEntity implements Permission,ProtectedEntity {

    public static final String DEFAULT_MANAGER_ROLE = "permissionManager";
    public static final String DEFAULT_VIEWER_ROLE = "permissionViewer";
    public static final String DEFAULT_EDITOR_ROLE = "permissionEditor";

    /**
     * String name for Permission
     */
    @JsonView({WaterJsonView.Compact.class})
    @Column
    @NotBlank
    @NoMalitiusCode
    @Size(max = 255)
    @NonNull
    private String name;
    /**
     * int actionIds for Permission
     */
    @JsonView({WaterJsonView.Compact.class})
    @Column
    @Positive
    @NonNull
    @Setter(AccessLevel.PUBLIC)
    private long actionIds;
    /**
     * String entityResourceName for Permission
     */
    @JsonView({WaterJsonView.Compact.class})
    @Column
    @NotNullOnPersist
    @NotEmpty
    @Size(max = 255)
    @NoMalitiusCode
    @NonNull
    private String entityResourceName;
    /**
     * long resourceId for Permission
     */
    @JsonView({WaterJsonView.Compact.class})
    @Column
    @NonNull
    private Long resourceId;

    /**
     * Role role for Permission
     */
    @JsonView({WaterJsonView.Compact.class})
    @NonNull
    private long roleId;

    /**
     * Permissions can be related to roles or directly to users
     */
    @JsonView({WaterJsonView.Compact.class})
    @NonNull
    private long userId;

}