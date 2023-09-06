package com.mycompany.authenticationservices.controller;

import com.mycompany.authenticationservices.dto.PermissionDTO;
import com.mycompany.authenticationservices.dto.Response;
import com.mycompany.authenticationservices.service.PermissionService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * Find permission list
     *
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Get all the permissions", description = "Get all the permissions exist in DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "get permission response", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Not Found , Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping
    public ResponseEntity<?> getPermissions() {
        log.info("getting all permission data from DB ");
        return ResponseEntity.ok(new Response("success", "Permission List Retrieved Successfully", permissionService.getPermissions()));
    }

    /**
     * Create permission
     *
     * @param permissionDTO - permission object
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Create Permission", description = "Create New Permission into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Permission data store into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionDTO.class)))),
            @ApiResponse(responseCode = "400, 401", description = "Bad Request, Permission name already exist", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @PostMapping
    public ResponseEntity<Response> createPermission(@RequestBody @Valid PermissionDTO permissionDTO) {
        PermissionDTO createdPermissionDTO=permissionService.save(permissionDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(createdPermissionDTO.getId()).toUri())
                .body(new Response("success", "Permission Saved Successfully",createdPermissionDTO));
    }

    /**
     * Find permission by id
     *
     * @param id - permission id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Find Permission by ID", description = "Find Permission by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Permission Found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionDTO.class)))),
            @ApiResponse(responseCode = "404, 500", description = "Permission not found, Internal Server Error", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(new Response("success", "Permission Loaded Successfully !", permissionService.getPermissionById(id)));
    }

    /**
     * Update permission
     *
     * @param permissionDTO - permission request
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Update Permissions", description = "Update existing Permission into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Permission data update into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @PutMapping()
    public ResponseEntity<Response> updatePermission(@RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(new Response("success", "Permission Updated Successfully", permissionService.update(permissionDTO)));
    }

    /**
     * Delete permission by id
     *
     * @param id - permission id
     * @return responseEntity - response entity with status code and data
     * @author pervez
     * @since 01 January 2023
     */
    @Operation(summary = "Delete Permission", description = "Delete existing Permission into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Permission data Delete into database", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))))})
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deletePermission(@PathVariable("id") Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
