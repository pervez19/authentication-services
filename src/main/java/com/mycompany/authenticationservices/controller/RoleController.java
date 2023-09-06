package com.mycompany.authenticationservices.controller;


import com.mycompany.authenticationservices.dto.PermissionDTO;
import com.mycompany.authenticationservices.dto.Response;
import com.mycompany.authenticationservices.dto.RoleDTO;
import com.mycompany.authenticationservices.dto.RolePermissionRequestDTO;
import com.mycompany.authenticationservices.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;


/**
 * This controller is to provide all the role relevant api's
 *
 * @author pervez
 * @version 1.0
 * @since 01 January 2023
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Find role list
     *
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Get all the roles", description = "Get all the roles exist in DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "get role response", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Not Found , Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping
    public ResponseEntity<?> getRoles() {
        log.info("getting all role data from DB ");
        return ResponseEntity.ok(new Response("success", "Role List Retrieved Successfully", roleService.findAll()));
    }

    /**
     * Create role
     *
     * @param roleDTO - role object
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Create Role", description = "Create New Role into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Role data store into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "400, 401", description = "Bad Request, Role name already exist", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @PostMapping
    public ResponseEntity<Response> createRole(@RequestBody @Valid RoleDTO roleDTO) {
        RoleDTO createdRoleDTO=roleService.save(roleDTO);
      return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                      .buildAndExpand(createdRoleDTO.getId()).toUri())
                .body(new Response("success", "Role Saved Successfully",createdRoleDTO));
    }

    /**
     * Find role by id
     *
     * @param id - role id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Find Role by ID", description = "Find Role by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Role Found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Role not found, Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(new Response("success", "Role Loaded Successfully !", roleService.findById(id)));
    }

    /**
     * Update role
     *
     * @param roleDTO - role request
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Update Roles", description = "Update existing Role into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Role data update into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @PutMapping()
    public ResponseEntity<Response> updateRole(@RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(new Response("success", "Role Updated Successfully", roleService.update(roleDTO)));
    }

    /**
     * Delete role by id
     *
     * @param id - role id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Delete Role", description = "Delete existing Role into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Role data Delete into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteRole(@PathVariable("id") Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create role permission
     *
     * @param rolePermissionRequest - Role permission request
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @PostMapping("/role-permission")
    public ResponseEntity<?> updateRolePermission(@RequestBody RolePermissionRequestDTO rolePermissionRequest) {
        return ResponseEntity.ok(new Response("success", "Role Permission Info saved successfully", roleService.updateRolePermission(rolePermissionRequest)));
    }

    /**
     * Find permission by role ids
     *
     * @param roleIds - Role ids
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Find Permission by role IDs", description = "Find Permission by role ids")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Permission Found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Permission not found, Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping("/permissions")
    public ResponseEntity<?> getPermissionByRoleIds(@RequestBody Set<Long> roleIds) {
        return ResponseEntity.ok(new Response("success", "Permission Loaded Successfully !", roleService.getPermissionByRoleIds(roleIds)));
    }

}
