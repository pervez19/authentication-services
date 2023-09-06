package com.mycompany.authenticationservices.controller;


import com.mycompany.authenticationservices.dto.Response;
import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;
import com.mycompany.authenticationservices.helper.AuthorizationConstants;
import com.mycompany.authenticationservices.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * This controller is to provide all the user relevant api's
 *
 * @author pervez
 * @version 1.0
 * @since 01 January 2023
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping("/api-check")
    public @ResponseBody String greeting() {
        return "Hello, World";
    }


    @PreAuthorize("hasAnyAuthority(" + AuthorizationConstants.CREATE_USER +")")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupDTO signupDTO) {
        log.info("Sign up attempt from  user: " + signupDTO.getUsername());
        UserDTO userDTO = userService.createUser(signupDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(userDTO.getId()).toUri())
                .body(new Response("success", "User Saved Successfully", userDTO));
    }


    /**
     * This API will update user by user id.
     *
     * @param userDTO - user object
     * @param id      - updated user id
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */

    @Operation(summary = "Update UserEntity", description = "Update existing UserEntity into DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "UserEntity data update into database", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        return ResponseEntity.ok(new Response("success", "User Updated Successful", userService.updateUser(userDTO, id)));
    }

    /**
     * This API will partially update user by user id.
     *
     * @param userDTO - user object
     * @param id      - updated user id
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */

    @Operation(summary = "Partially update UserEntity", description = "Partially update existing UserEntity into DB")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "UserEntity data partially update into database", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUser(@RequestBody UserDTO userDTO, @PathVariable Long id) {
        return ResponseEntity.ok(new Response("success", "User Updated Successful", userService.patchUser(userDTO, id)));
    }

    /**
     * This API will Delete user.
     *
     * @param id - deleted user id
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */
    @Operation(summary = "Delete UserEntity", description = "Delete existing UserEntity into DB")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "UserEntity data Delete into database", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * This API will get user details by user id.
     *
     * @param id - user id
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */

    @Operation(summary = "Find UserEntity by ID", description = "Find user by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the UserEntity", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new Response("success", "User Found", userService.getById(id)));
    }

    /**
     * This API will get user list.
     *
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */
    @Operation(summary = "Get a UserEntity List", description = "Get a UserEntity List")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the UserEntity", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})
    @GetMapping()
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(new Response("success", "User Found", userService.getUsers()));
    }

    /**
     * This API will get user list.
     *
     * @param accountId - account id
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */
    @Operation(summary = "Get a UserEntity List", description = "Get a UserEntity List")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the UserEntity", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})
    @GetMapping({"/account/{accountId}"})
    public ResponseEntity<?> getUsersByAccountId(@PathVariable("accountId") String accountId) {
        return ResponseEntity.ok(new Response("success", "User Found", userService.getByAccountId(accountId)));
    }

    /**
     * This API will get page of user.
     *
     * @param pageable - Pageable object
     * @return Response
     * @author pervez
     * @version 1.0
     * @since 01 January 2023
     */
    @Operation(summary = "This API will get page of user", description = "This API will get page of user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the UserEntity", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid pageable supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "UserEntity not found", content = @Content)})

    @GetMapping(value = "/page")
    public ResponseEntity<?> getUsers(Pageable pageable) {
        return ResponseEntity.ok(new Response("success", "User Found", userService.getUsers(pageable)));
    }
}
